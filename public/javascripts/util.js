function GetQueryString(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null) return  unescape(r[2]); return null;
}

function isPhone(str){
    var reg = /^(13[0-9]|14[57]|15[012356789]|18[012356789])\d{8}$/;
    return reg.test(str);
}
function isExmail(str){
    //var reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$;
}
function isSmsCode(str){
    var reg = /^\d{6}$/;
    return reg.test(str);
}

function isPwd(str){
    return str.length >= 8 && str.length <= 20;
}

function ajaxGet(url,successFunc){
    $.ajax({
        url:url,
        dataType:'json',
        type:'GET',
        success:function(res){
            successFunc(res);
        }.bind(this),
        error:function(xhr,status,err){
            console.log(xhr,status,err.toString());
        }.bind(this)
    })
}

function ajaxPost(url,postData,successFunc){
    $.ajax({
        url:url,
        dataType:'json',
        type:'POST',
        data:postData,
        success:function(data){
            successFunc(data);
        }.bind(this),
        error:function(xhr,status,err){
            console.log(xhr,status,err.toString());
        }.bind(this)
    })
}

function ajaxJsonPost(url,postData,successFunc){
    $.ajax({
        url:url,
        dataType:'json',
        contentType:'application/json',
        data: JSON.stringify(postData),
        type:'POST',
        success:function(data){
            successFunc(data);
        }.bind(this),
        error:function(xhr,status,err){
            console.log(xhr,status,err.toString());
        }.bind(this)
    })
}
