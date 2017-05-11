/**
 * Created by liboren on 2016/3/20.
 */
var LoginPage=React.createClass({
    getInitialState:function(){
        return({
            progressbarlist:[]
        });
    },
    gmLogin:function(){
        var text = $("#account").val().trim();
        var pwd = $("#password").val();
        var upPath = "/loginsubmit";
        if(!text){
            alert("账户名为空!");
            return false;
        }
        if(!pwd){
            alert("密码为空!");
            return false;
        }
        ajaxPost(upPath, {account: text, password: pwd}, function(res){
            if(res.errCode == 0){
                var userid = res.userid;
                window.location.href = "/scan?userid="+userid;
            }else{
                alert(res.msg);
            }
        }.bind(this));
    },


    render:function(){

        return (
            <div id="login-app">
                <div className="loginPage">
                    <div className="tongtu">
                    <div className="formArea">
                        <div>
                            <div style={{float: 'left',width: '30%'}}>
                                <p className="title">用户登录</p>
                            </div>
                        </div>
                            <div className="form-group">
                                <input style={{marginLeft:'10%',width:'80%',height:'40px'}} id="account" name="account" type="text" className="form-control" placeholder="手机号/邮箱" required autofocus />
                                <input style={{marginLeft:'10%',width:'80%',height:'40px'}} id="password" name="password" type="password" className="form-control" placeholder="密码" required />
                                <a style={{marginLeft:'10%',color:'gray'}} href="/resetpwdtype">忘记密码？</a>
                                <button className="loginBtn" onClick={this.gmLogin}>登&nbsp;&nbsp;&nbsp;录</button>
                            </div>

                            <p style={{marginLeft:'10%'}}>还没有账号？<a style={{color:'#b01c2e'}} href="/registerpage">免费注册</a></p>

                    </div>
                        </div>
                    <div  id="home_footer">
                        <div className="contact_us">
                            <p>
                    <span className="foot-tag">
                        <a href="/feedback" target="_blank">意见反馈</a>
                    </span>
                                |
                    <span className="foot-tag">
                        <a href="/help" target="_blank">用户帮助</a>
                    </span>
                                |

                    <span className="foot-tag">
                        <a href="/business" target="_blank">商务合作</a>
                    </span>
                                |
                    <span className="foot-tag">
                        <a href="/contact" target="_blank">公司信息</a>
                    </span>
                                |
                    <span className="foot-tag">
                        <a href="/service" target="_blank">客服电话</a>
                    </span>
                                |
                    <span className="foot-tag">
                        <a href="/disclaimer" target="_blank">免责声明</a>
                    </span>
                            </p>
                        </div>
                        <div className="bottomBar">
                            <p>版权所有: 暴龙科技有限公司&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;增值电信业务经营许可证&nbsp;B2-20040082号</p>
                        </div>

                    </div>
                </div>
            </div>
        )}
});


React.render(
    <LoginPage/>,
    document.getElementById('content_render')
);