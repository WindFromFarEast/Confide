package net.confide.factory.net;

import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.api.account.AccountRspModel;
import net.confide.factory.model.api.account.LoginModel;
import net.confide.factory.model.api.account.RegisterModel;
import net.confide.factory.model.api.message.MsgCreateModel;
import net.confide.factory.model.card.MessageCard;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.user.UserUpdateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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

    /**
     * 用户搜索接口
     * @param name
     * @return
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    /**
     * 用户关注接口
     */
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String userId);

    /**
     * 获取联系人列表的接口
     * @return
     */
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    /**
     * 获取一个用户信息的接口
     * @param userId
     * @return
     */
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);

    /**
     * 发送消息的接口
     * @param model
     * @return
     */
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);
}
