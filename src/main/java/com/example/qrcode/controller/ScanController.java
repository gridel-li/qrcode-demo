package com.example.qrcode.controller;

import cn.hutool.core.lang.UUID;
import com.example.qrcode.common.*;
import com.example.qrcode.domain.AuthUserFactory;
import com.example.qrcode.domain.LoginQrcodeVO;
import com.example.qrcode.domain.SysUser;
import com.example.qrcode.exception.ExceptionResultCode;
import com.example.qrcode.security.JwtTokenUtil;
import com.example.qrcode.service.ISystemService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @program: qrcode-demo
 * @Title: QrCodeController
 * @description:
 * @author: 李英杰
 * @create: 2020-01-09 16:58
 * @version: 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("/scan")
@Slf4j
public class ScanController {


    public static final Integer QR_CODE_WIDTH = 230;//宽
    public static final Integer QR_CODE_HEIGHT = 230;//高
    public static final Integer QR_CODE_SECOND = 60;//二维码时间

    @Resource
    private RedisRepository redisRepository;
    @Resource
    private ISystemService systemService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Value("${qrcode.imgDomain}")
    private String imgDomain;
    @Value("${qrcode.format}")
    private String format;

    /**
     * @description: 生成二维码
     * @author: 李英杰
     * @date: 2020/1/15
     * @param: [oldId]
     * @return: com.example.qrcode.common.Message
     */
    @GetMapping("getQrcode")
    public Message getQrCode(@RequestParam(required = false) String oldQrcode){
        //如果有旧的二维码 把旧的删掉
        if (StringUtils.isNotBlank(oldQrcode)){
            String loginQrcodeKey = RedisKeyBuilder.getLoginQrcodeKey(oldQrcode);
            redisRepository.del(loginQrcodeKey);
        }
        String qrcodeId = UUID.randomUUID().toString();
        // 1.创建二维码图片的存储目录以及二维码图片存储路径 /qrcode/2020/01/15
        String dateDir = DateUtil.getYear() + "/" + DateUtil.getMonth() + "/" + DateUtil.getSimpleDay();
        String sysPath = System.getProperty("user.dir") + "/qrcode/"+ dateDir;
        //创建文件夹
        FileUtil.createDir(sysPath);

        String qrcodeName = qrcodeId + "." + format;
        //二维码图片的存储路径
        String qrcodeStorePath = sysPath + "/" + qrcodeName;;
        //二维码图片的访问路径
        //String qrcodeImgUrl = imgDomain + "/" + dateDir + "/" + qrcodeName;
        //生成文件
        File qrcodeFile = new File(qrcodeStorePath);
        //3、生成二维码图片
        String qrcodeContent = imgDomain + "/qrcode/" + dateDir + "/" + qrcodeName;

        //String qrcodeContent = imgDomain + "?qrcodeId=" + qrcodeId;
        HashMap<EncodeHintType, String> hints = new HashMap<>(1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrcodeContent, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);
            BufferedImage image = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            ImageIO.write(image, format, qrcodeFile);
            MatrixToImageWriter.writeToFile(bitMatrix, format, qrcodeFile);

            LoginQrcodeVO loginQrcode = new LoginQrcodeVO();
            loginQrcode.setQrcodeId(qrcodeId);
            loginQrcode.setQrcodeImgUrl(qrcodeContent);
            // 4.写入Redis 缓存
            String loginQrcodeKey = RedisKeyBuilder.getLoginQrcodeKey(qrcodeId);
            // 设置登录二维码的value为unscan表示没有登录,也没有被扫描，2分钟有效;
            redisRepository.setExpire(loginQrcodeKey, QrCodeEnum.unscan.toString(), QR_CODE_SECOND);
            return Message.ok(loginQrcode);

        } catch (Exception e) {
            log.error("生成二维码发生异常，异常信息：{}", e);
            return Message.error("生成二维码异常");
        }
    }

    /**
     * 前端做轮询操作 验证二维码是否已登录
     * @param qrcodeId
     * @param request
     * @return
     */
    @PostMapping("checkQrcodeLogin")
    @ResponseBody
    public Message checkQrcodeLogin(String qrcodeId, HttpServletRequest request) {
        if (StringUtils.isBlank(qrcodeId)){
            return Message.error(ExceptionResultCode.PARAM_ERR);
        }
        String loginQrcodeKey = RedisKeyBuilder.getLoginQrcodeKey(qrcodeId);
        if (!redisRepository.exists(loginQrcodeKey)){
            return Message.error(ExceptionResultCode.INVALID_QRCODE);
        }

        String qrcodeStatus = redisRepository.get(loginQrcodeKey);
        Message message = new Message();
        if (QrCodeEnum.unscan.toString().equalsIgnoreCase(qrcodeStatus)){           //继续循环,等待操作
            message.ok(CommonConstants.ResultObj.QRCODE_UNSCAN,"未扫描二维码,继续等待");
            return message;
        }else if (QrCodeEnum.scan.toString().equalsIgnoreCase(qrcodeStatus)){       //继续循环,等待操作
            message.ok(CommonConstants.ResultObj.QRCODE_SCAN,"已扫描二维码,请在客户端操作");
            return message;
        }else if (QrCodeEnum.cancel.toString().equalsIgnoreCase(qrcodeStatus)){     //结束循环,如果需要再次操作需要重新扫码
            message.ok(CommonConstants.ResultObj.QRCODE_CANCEL,"用户取消授权");
            return message;
        }else if (QrCodeEnum.login.toString().equalsIgnoreCase(qrcodeStatus)){     //结束循环,匹配用户信息,登录成功
            String qrcodeAccessKey = RedisKeyBuilder.getQrcodeAccessKey(qrcodeId);
            String loginName = redisRepository.get(qrcodeAccessKey);
            if (StringUtils.isNotBlank(loginName)){
                SysUser sysUser = systemService.getUserByLoginName(loginName);
                UserDetails userDetails = AuthUserFactory.create(sysUser);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                final String jwtToken = jwtTokenUtil.generateToken(userDetails, jwtTokenUtil.getRandomKey());
                message.ok(CommonConstants.ResultObj.QRCODE_LOGIN,"确认登录成功");
                return message.addData("token",jwtToken);
            }else{
                return message.error("用户信息错误，请重新操作");
            }
        }else{
            return message.error();
        }
    }

    /**
     * App扫一扫统一入口
     * 说明：一般APP的扫一扫功能都是规划统一入口，以及针对不同的内容最终提供几种类型的响应，比如：
     * HTTP：通过webview打开一个http的连接
     * TEXT：弹出一个提示框内容
     * INTERNAL：打开一个APP的内部跳转
     * @param qrcodeId
     * @return
     */
    @PostMapping("/appScan")
    public Message appScan(@RequestParam String qrcodeId) {
        //APP扫描到的内容信息
        //获取uuid，校验redis中是否存在该标识
        String loginQrcodeKey = RedisKeyBuilder.getLoginQrcodeKey(qrcodeId);
        if(!redisRepository.exists(loginQrcodeKey)) {
            //如果不存在该KEY，表示二维码已经失效
            return Message.error(ExceptionResultCode.INVALID_QRCODE);
        } else {
            //更新二维码，并将二维码唯一标识与token绑定，有效时间120S
            redisRepository.setExpire(loginQrcodeKey, QrCodeEnum.scan.toString(), QR_CODE_SECOND);
            String loginName = "admin";//因为token太长了 用登录用户名代替 把当前登录用户和二维码绑定
            String loginNameKey = RedisKeyBuilder.getLoginNameKey(loginName);
            redisRepository.setExpire(loginNameKey, qrcodeId, QR_CODE_SECOND);
            //APP端打开一个页面，确认或者取消登录
            return Message.ok();
        }
    }

    /**
     * 扫码确认
     * @param status
     * @return
     */
    @PostMapping("/qrcodeConfirm")
    public Message qrcodeConfirm(@RequestParam Integer status) {
        String loginName = "admin";
        String loginNameKey = RedisKeyBuilder.getLoginNameKey(loginName);
        String qrcodeId = redisRepository.get(loginNameKey);
        String loginQrcodeKey = RedisKeyBuilder.getLoginQrcodeKey(qrcodeId);
        if (StringUtils.isBlank(qrcodeId) || !redisRepository.exists(loginQrcodeKey) || !redisRepository.exists(loginNameKey)){
            return Message.error(ExceptionResultCode.INVALID_QRCODE);
        }
        Message msg = new Message();
        if(1==status){
            redisRepository.setExpire(loginQrcodeKey,QrCodeEnum.login.toString(),QR_CODE_SECOND);
            String qrcodeAccessKey = RedisKeyBuilder.getQrcodeAccessKey(qrcodeId);
            redisRepository.setExpire(qrcodeAccessKey,loginName,QR_CODE_SECOND);
            return msg.ok(CommonConstants.ResultObj.QRCODE_LOGIN,"确认登录成功");
        }else if (0==status){
            redisRepository.del(loginNameKey);
            redisRepository.setExpire(loginQrcodeKey,QrCodeEnum.cancel.toString(),QR_CODE_SECOND);
            return msg.ok(CommonConstants.ResultObj.QRCODE_CANCEL,"用户取消授权");
        }else{
            return Message.error();
        }

    }


    @GetMapping("/file")
    public void downLoadFile(String filePath, HttpServletResponse httpServletResponse) {
        String property = System.getProperty("user.dir");
        filePath = property + filePath;
        File file = new File(filePath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            OutputStream outputStream = httpServletResponse.getOutputStream();
            String substring = filePath.substring(filePath.lastIndexOf("/")+1);
            httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + new String(substring.getBytes("utf-8"),"ISO-8859-1"));
            IOUtils.copy(fileInputStream, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
