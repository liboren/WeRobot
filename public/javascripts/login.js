/**
 * Created by liboren on 2016/3/20.
 */
var LoginPage=React.createClass({
    getInitialState:function(){
        return({
            uuid:"",
            loginstate:"等待扫码"
        });
    },
    componentWillMount:function(){
        var uuid = this.state.uuid;
        ajaxGet("/getuuid", function(res){
            console.log(res);
            if(res.errCode == 0){
                uuid = res.uuid;
                this.setState({uuid:res.uuid});

                getCode();
            }else{
                alert(msg);
            }
        }.bind(this));



        function getCode() {

            var url = "/checkuserlogin?uuid="+ uuid;

            ajaxGet(url, function(res){
                console.log(res);
                if(res.errCode === 0){
                    console.log(res.result);
                    if(res.result == "200"){
                        console.log("登录成功");
                        window.location.href = "/homepage"
                    }
                    else {
                        if (res.result == "400") {
                            console.log("等待扫码");
                        }
                        else if (res.result == "408") {
                            console.log("登录超时");
                        }
                        else if (res.result == "201") {
                            console.log("扫码成功");
                        }
                        else {
                            console.log("登录错误");
                        }
                        setTimeout(function () {
                            getCode();
                        }, 1000);
                    }
                }else{
                    alert(msg);
                }
            }.bind(this));
        }

    },



    render:function(){
        var twoDcode = "http://login.weixin.qq.com/qrcode/" + this.state.uuid;
        return (
            <div id="login-app">
                <div>
                    <img src={twoDcode} />

                </div>
                <div>
                    <span>当前状态：{this.state.loginstate}</span>
                </div>
            </div>
        )}
});


React.render(
    <LoginPage/>,
    document.getElementById('content_render')
);