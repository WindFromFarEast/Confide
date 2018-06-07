package net.web.confide.push.service;

import com.google.common.base.Strings;
import net.web.confide.push.bean.api.account.AccountRspModel;
import net.web.confide.push.bean.api.account.LoginModel;
import net.web.confide.push.bean.api.account.RegisterModel;
import net.web.confide.push.bean.api.base.ResponseModel;
import net.web.confide.push.bean.card.UserCard;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *
 */
//该类访问路径为:127.0.0.1/api/account
@Path("/account")
public class AccountService extends BaseService{

    /**
     * 后台用户登录接口
     * @param model
     * @return
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel model) {
        //校验
        if (!LoginModel.check(model)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            //如果有携带pushId
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            //登录成功,返回当前登录的账户
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        } else {
            //登录失败
            return ResponseModel.buildLoginError();
        }
    }

    /**
     * 后台用户注册接口
     * @param model
     * @return
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        //校验
        if (!RegisterModel.check(model)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        //首先查询是否已经注册过
        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null) {
            //已经注册过该账户
            return ResponseModel.buildHaveAccountError();
        }
        //再通过名称查询是否已经注册过
        user = UserFactory.findByName(model.getName().trim());
        if (user != null) {
            //已经注册过该账户
            return ResponseModel.buildHaveNameError();
        }
        //没有注册过,那就进行注册
        user = UserFactory.register(model.getAccount(), model.getPassword(), model.getName());
        if (user != null) {
            //如果有携带pushId
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            //成功注册,返回当前账户
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        } else {
            //注册时发生异常
            return ResponseModel.buildRegisterError();
        }
    }

    /**
     * 绑定设备pushId
     * @return
     */
    @POST
    @Path("/bind/{pushId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //从请求头中获取token字段,pushId从url地址中获取
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token") String token,
                                               @PathParam("pushId") String pushId) {
        //校验
        if (Strings.isNullOrEmpty(token) || Strings.isNullOrEmpty(pushId)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        //通过token获取用户信息
        User self = getSelf();
        return bind(self, pushId);
    }

    /**
     * 绑定
     * @param self
     * @param pushId
     * @return
     */
    private ResponseModel<AccountRspModel> bind(User self, String pushId) {
        //进行设备pushId绑定
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        //返回当前账户,并且已经绑定了
        AccountRspModel rspModel = new AccountRspModel(user, true);
        return ResponseModel.buildOk(rspModel);
    }
}
