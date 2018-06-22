package net.confide.factory.data.helper;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import net.confide.common.factory.data.DataSource;
import net.confide.factory.Factory;
import net.confide.factory.R;
import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.api.account.AccountRspModel;
import net.confide.factory.model.api.account.LoginModel;
import net.confide.factory.model.api.account.RegisterModel;
import net.confide.factory.model.db.AppDataBase;
import net.confide.factory.model.db.User;
import net.confide.factory.net.NetWork;
import net.confide.factory.net.RemoteService;
import net.confide.factory.persistence.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户功能相关类，如注册、登录等
 */
public class AccountHelper {

    /**
     * 网络通信进行用户的注册
     *
     * @param model    注册Model
     * @param callback 注册成功、失败的回调接口
     */
    public static void register(final RegisterModel model, final DataSource.Callback<User> callback) {
        //使用Retrofit对网络请求接口做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        // 异步的请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 网络通信进行用户的登录
     *
     * @param model
     * @param callback
     */
    public static void login(final LoginModel model, final DataSource.Callback<User> callback) {
        //使用Retrofit对网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        //发起网络异步请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备id进行绑定
     *
     * @param callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {
        //检查当前pushId是否为空
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId))
            return;
        RemoteService service = NetWork.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 请求的回调部分封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {

        final DataSource.Callback<User> callback;

        AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call,
                               Response<RspModel<AccountRspModel>> response) {
            // 请求成功返回
            // 从返回中得到我们的全局Model，内部是使用的Gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();
            if (rspModel.success()) {
                // 拿到实体
                AccountRspModel accountRspModel = rspModel.getResult();
                // 获取我的信息
                User user = accountRspModel.getUser();
                //使用数据库封装工具类进行保存
                DbHelper.save(User.class, user);
                // 第一种，之间保存
                //user.save();
                    /*
                    // 第二种通过ModelAdapter
                    FlowManager.getModelAdapter(User.class)
                            .save(user);

                    // 第三种，事务中
                    DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                    definition.beginTransactionAsync(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            FlowManager.getModelAdapter(User.class)
                                    .save(user);
                        }
                    }).build().execute();
                    */
                // 同步到XML持久化中
                Account.login(accountRspModel);

                // 判断绑定状态，是否绑定设备
                if (accountRspModel.isBind()) {
                    // 设置绑定状态为True
                    Account.setBind(true);
                    // 然后返回
                    if (callback != null)
                        callback.onDataLoaded(user);
                } else {
                    // 进行绑定的唤起
                    bindPush(callback);
                }
            } else {
                // 错误解析
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            // 网络请求失败
            if (callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }
}
