package net.confide.factory.net;

import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.api.account.AccountRspModel;
import net.confide.factory.model.api.account.LoginModel;
import net.confide.factory.model.api.account.RegisterModel;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.user.UserUpdateModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求所有接口
 */
public interface RemoteService {

    /**
     * 网络请求注册接口
     * @param model
     * @return
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     * @param model
     * @return
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备ID接口
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    /**
     * 用户更新接口
     * @param model
     * @return
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);
}
