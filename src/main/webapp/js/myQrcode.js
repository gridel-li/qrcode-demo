var isContinue = true;
var type = "";
var text = "";
var BaseUrl = "http://localhost:9111";
var APP_NAME = "scan_login_demo";

$(function () {
    //加载验证码接口
    var uniqueKey = sessionStorage.getItem(APP_NAME + "_uniqueKey");
    if( uniqueKey == null || uniqueKey == ""){
        uniqueKey = randomString(32);
        sessionStorage.setItem(APP_NAME + "_uniqueKey",uniqueKey);
    }
    //reloadcode("false");
});

var countdown = 120;

//捕获回车事件登录
document.onkeydown = function(event_e){
    if(window.event)
        event_e = window.event;
    var int_keycode = event_e.charCode||event_e.keyCode;
    if(int_keycode ==13){
        onLogin();
    }
}
//界面加载自动聚焦到用户名输入框
document.getElementById("username").focus();

//使用ajax的方式提交登录信息，然后通过json数据格式将返回结果返回
function onLogin(){
    // 获取是否记住密码登陆
    var ifLogin = $("#ifLogin").val();
    //点击登录按钮之后不可重复再点击
    $("#onLogin").attr('disabled','disabled');
    $("#onLogin").text("登录中...");
    $("#onLogin").focus();
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    //var rand = document.getElementById("rand").value;
    var uniqueCode = sessionStorage.getItem(APP_NAME + "_uniqueKey");

    if(username == "" || username == null ){
        flag = "请输入您的账号信息";
        $("#username").focus();
        $("#onLogin").removeAttr('disabled');
        $("#onLogin").text("登　　录");
        alert(flag);
        return
    }
    if( password == "" || password == null ){
        flag = "请输入您的密码信息";
        $("#password").focus();
        $("#onLogin").removeAttr('disabled');
        $("#onLogin").text("登　　录");
        alert(flag);
        return
    }
    /*if(rand == "" || rand == null ){
        flag = "请输入右侧图片中的数字验证码";
        $("#rand").focus();
        $("#onLogin").removeAttr('disabled');
        $("#onLogin").text("登　　录");
        alert(flag);
        return
    }*/
    var params= {username: username,password: password,verifyCode: rand,uniqueCode: uniqueCode};
    params = JSON.stringify(params);
    $.ajax({
        type: 'POST',
        url: BaseUrl + 'login',
        dataType: "json",
        contentType:"application/json",
        data: params,
        success:function(data){
            var isSuccess = data.isSuccess;
            if( isSuccess == true ){
                sessionStorage.setItem(APP_NAME, data.data);
                window.location.href = "./confirm.html";
            }else{
                $("#onLogin").removeAttr('disabled');
                $("#onLogin").text("登　　录");
                alert(data.responseMsg);
                reloadcode();
            }
        }
    });
}

//切换成二维码登录
function qrcode_login(){
    $("#mask_qrcode").hide();
    $("#mask_confirm").hide();
    $("#input_nav").hide();
    $("#qrcode_nav").show();
    //先获取二维码的唯一编码
    var params= {oldQrcode:text};
    $.ajax({
        type: 'GET',
        url: BaseUrl+ '/scan/getQrcode',
        dataType: "json",
        contentType:"application/json",
        data: params,
        success:function(data){
            var code = data.meta.code;
            if( code == 200 ){
                var qrcodeInfo = data.data.content;
                var qrcodeId = qrcodeInfo.qrcodeId;
                var qrcodeImgUrl = qrcodeInfo.qrcodeImgUrl;
                $("#qr_code_img").attr("src", qrcodeImgUrl);
                $("#qrcodeVal").val(qrcodeId);
                console.log("二维码内容：");
                console.log(qrcodeId);
                //不断向后台发起请求检测当前二维码的状态
                isContinue = true;
                setTimeout(function () { longPolling(); }, 3000);
            }else{
                alert(data.responseMsg);
            }
        }
    });
}

//切换成账号密码登录
function acc_login(){
    $("#input_nav").show();
    $("#qrcode_nav").hide();
    isContinue = false;
    type = "";
}

//重新获取登录二维码
function retry_qrcode_login(){
    isContinue = true;
    type = "";
    qrcode_login();
    $("#mask_qrcode").hide();
    $("#mask_confirm").hide();
}

//长连接检测当前二维码的状态
function longPolling() {

    if( isContinue == false ){
        return
    }
    var qrcodeVal = $("#qrcodeVal").val();
    var params= {qrcodeId:qrcodeVal};
    $.ajax({
        type: 'POST',
        url: BaseUrl + '/scan/checkQrcodeLogin',
        dataType: "json",
        data:params,
        timeout: 10000,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $("#state").append("[state: " + textStatus + ", error: " + errorThrown + " ]<br/>");
            if (textStatus == "timeout") { // 请求超时
                // 递归调用
                setTimeout(function () { longPolling(); }, 3000);
                // 其他错误，如网络错误等
            } else if (!textStatus == "error") {
                setTimeout(function () { longPolling(); }, 3000);
            }
        },
        success: function (data, textStatus) {
            if (textStatus == "success") { // 请求成功
                var resCode = data.meta.code;
                if( resCode == 3007 ){
                    //如果是当前请求的二维码内容失效时提在页面提示
                    //二维码已失效，添加遮罩
                    $("#mask_confirm").hide();
                    $("#mask_qrcode").show();
                }else if( resCode == 2001 ){
                    //未扫描 继续等待
                    setTimeout(function () { longPolling(); }, 3000);
                }else if( resCode == 2002 ){
                    //扫描成功，请在手机上确认登录
                    $("#mask_confirm").show();
                    setTimeout(function () { longPolling(); }, 3000);
                }else if( resCode == 2004 ){
                    //取消登录，二维码失效，添加遮罩
                    $("#mask_confirm").hide();
                    $("#mask_qrcode").show();
                }else if( resCode == 2003 ){
                    //登录成功
                    window.location.href= "index.html";
                }
            }
        }
    });
}

function reloadcode(refresh) {
    var uniqueKey = sessionStorage.getItem(APP_NAME + "_uniqueKey");
    var params = JSON.stringify({uniqueKey:uniqueKey,refresh:refresh});
    $.ajax({
        type: 'POST',
        url: BaseUrl + '/auth/getVerifyCode',
        dataType: "json",
        contentType: "application/json",
        data: params,
        success: function (data) {
            var isSuccess = data.isSuccess;
            if (isSuccess == true) {
                $("#safecode").attr('src', "data:image/jpeg;base64," + data.data);
            }
        }
    });
}

function randomString(len) {
    len = len || 32;
    let timestamp = new Date().getTime();
    let $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhijklmnopqrstuvwxyz123456789';
    let maxPos = $chars.length;
    let randomStr = '';
    for (let i = 0; i < len; i++) {
        randomStr +=$chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return randomStr + timestamp;
}

//密码大写输入提示
function capitalTip(id){
    $('#' + id).after('<div class="capslock" id="capital_password"><span>大写锁定已开启</span></div>');
    var capital = false; //聚焦初始化，防止刚聚焦时点击Caps按键提示信息显隐错误

    // 获取大写提示的标签，并提供大写提示显示隐藏的调用接口
    var capitalTip = {
        $elem: $('#capital_'+id),
        toggle: function (s) {
            if(s === 'none'){
                this.$elem.hide();
            }else if(s === 'block'){
                this.$elem.show();
            }else if(this.$elem.is(':hidden')){
                this.$elem.show();
            }else{
                this.$elem.hide();
            }
        }
    }
    $('#' + id).on('keydown.caps',function(e){
        if (e.keyCode === 20 && capital) { // 点击Caps大写提示显隐切换
            capitalTip.toggle();
        }
    }).on('focus.caps',function(){capital = false}).on('keypress.caps',function(e){capsLock(e)}).on('blur.caps',function(e){
        //输入框失去焦点，提示隐藏
        capitalTip.toggle('none');
    });
    function capsLock(e){
        var keyCode = e.keyCode || e.which;// 按键的keyCode
        var isShift = e.shiftKey || keyCode === 16 || false;// shift键是否按住
        if(keyCode === 9){
            capitalTip.toggle('none');
        }else{
            //指定位置的字符的 Unicode 编码 , 通过与shift键对于的keycode，就可以判断capslock是否开启了
            // 90 Caps Lock 打开，且没有按住shift键
            if (((keyCode >= 65 && keyCode <= 90) && !isShift) || ((keyCode >= 97 && keyCode <= 122) && isShift)) {
                // 122 Caps Lock打开，且按住shift键
                capitalTip.toggle('block'); // 大写开启时弹出提示框
                capital = true;
            } else {
                capitalTip.toggle('none');
                capital = true;
            }
        }
    }
};

//调用提示
capitalTip('password');
