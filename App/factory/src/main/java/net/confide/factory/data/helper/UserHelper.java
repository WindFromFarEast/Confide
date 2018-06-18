package net.confide.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.confide.common.factory.data.DataSource;
import net.confide.factory.Factory;
import net.confide.factory.R;
import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.db.User;
import net.confide.factory.model.db.User_Table;
import net.confide.factory.model.user.UserUpdateModel;
import net.confide.factory.net.NetWork;
import net.confide.factory.net.RemoteService;
import net.confide.factory.presenter.contact.FollowPresenter;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户操作类——涉及到网络
 */
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

    /**
     * 刷新联系人
     * @param callback
     * @return
     */
    public static void refreshContacts(final DataSource.Callback<List<UserCard>> callback) {
        //使用Retrofit对网络请求接口做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call
        Call<RspModel<List<UserCard>>> call = service.userContacts();
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
    }

    /**
     * 从本地查询用户信息
     * @param id
     * @return
     */
    public static User findFromLocal(String id) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 从网络查询用户信息
     * @param id
     * @return
     */
    public static User findFromNet(String id) {
        RemoteService service = NetWork.remote();
        try {
            Response<RspModel<UserCard>> response = service.userFind(id).execute();
            UserCard card = response.body().getResult();
            if (card != null) {
                //TODO 数据库刷新 但是不通知
                User user = card.build();
                user.save();
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索用户,优先从本地搜索,没有的话再从网络搜索
     * @param id
     * @return
     */
    public static User search(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            return findFromNet(id);
        }
        return user;
    }

    /**
     * 搜索用户,优先从网络搜索
     * @param id
     * @return
     */
    public static User searchFirstOfNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }
}
