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
import net.confide.factory.presenter.contact.FollowPresenter;

import java.util.List;

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

    /**
     * 搜索用户的接口
     * @param name
     * @param callback
     */
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        //使用Retrofit对网络请求接口做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        // 异步请求
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {

            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //将当前调度者返回
        return call;
    }

    /**
     * 关注用户的接口
     * @param id
     */
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        //使用Retrofit对网络请求接口做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call
        Call<RspModel<UserCard>> call = service.userFollow(id);
        // 异步请求
        call.enqueue(new Callback<RspModel<UserCard>>() {

            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    //将关注返回的用户保存到数据库
                    UserCard userCard = rspModel.getResult();
                    User user =  userCard.build();
                    //TODO 通知联系人列表进行刷新

                    //
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

}
