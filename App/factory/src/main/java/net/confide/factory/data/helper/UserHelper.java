package net.confide.factory.data.helper;

import net.confide.common.factory.data.DataSource;
import net.confide.factory.Factory;
import net.confide.factory.R;
import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.db.User;
import net.confide.factory.model.user.UserUpdateModel;
import net.confide.factory.net.NetWork;
import net.confide.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHelper {

    /**
     * 更新用户信息至服务器
     * @param model
     */
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        //使用Retrofit对网络请求接口做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        // 异步的请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    //数据库存储
                    UserCard userCard = rspModel.getResult();
                    User user = userCard.build();
                    user.save();
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                // 网络请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }
}
