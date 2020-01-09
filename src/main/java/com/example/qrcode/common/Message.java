package com.example.qrcode.common;


import com.example.qrcode.exception.ExceptionResultCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: geometry-bi
 * @description: 前后端统一消息定义协议 Message  之后前后端数据交互都按照规定的类型进行交互
 * @author: 肖乔辉
 * @create: 2018-08-17 15:39
 * @version: 1.0.0
 */
public class Message<T> {

    // 消息头meta 存放状态信息 code message
    private Map<String, Object> meta = new HashMap<String, Object>();
    // 消息内容  存储实体交互数据
    private Map<String, Object> data = new HashMap<String, Object>();


    public Map<String, Object> getMeta() {
        return meta;
    }

    public Message setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Message setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public Message addMeta(String key, Object object) {
        this.meta.put(key, object);
        return this;
    }

    public Message addData(String key, Object object) {
        this.data.put(key, object);
        return this;
    }

    public Message() {
        this.addMeta("success", Boolean.TRUE);
        this.addMeta("code", CommonConstants.ResultObj.SUCCESS_CODE);
        this.addMeta("msg", CommonConstants.ResultObj.SUCCESS_MSG);
        this.addMeta("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public Message(ExceptionResultCode status, T content) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", status.getCode());
        this.addMeta("msg", status.getMessage());
        this.addMeta("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.addData("content", content);
    }

    public Message(Object content) {
        this.addMeta("success", Boolean.TRUE);
        this.addMeta("code", CommonConstants.ResultObj.SUCCESS_CODE);
        this.addMeta("msg", CommonConstants.ResultObj.SUCCESS_MSG);
        this.addMeta("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.addData("content", content);
    }


    /**
     * @description: 不需要任何返回的成功状态用此方法
     * @author: 李英杰
     * @date: 2019/9/2
     * @param: []
     * @return: com.geominfo.authing.common.base.Message
     */
    public static Message ok() {
        return new Message();
    }

    /*
     * @description: 操作成功返回一个对象用此方法
     * @author: 李英杰
     * @date: 2019/9/2
     * @param: [content]
     * @return: com.geominfo.authing.common.base.Message
     */
    public static Message ok(Object content) {
        return new Message(content);
    }

    public static Message error(ExceptionResultCode error, Object content) {
        return new Message(error, content);
    }
    public static Message error() {
        return new Message().error(CommonConstants.ResultObj.ERROR_CODE,CommonConstants.ResultObj.ERROR_MSG);
    }
    public static Message error(String msg) {
        return new Message().error(CommonConstants.ResultObj.ERROR_CODE,msg);
    }


    public Message ok(int statusCode, String statusMsg) {
        this.addMeta("success", Boolean.TRUE);
        this.addMeta("code", statusCode);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this;
    }

    public Message error(int statusCode, String statusMsg) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", statusCode);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this;
    }

}
