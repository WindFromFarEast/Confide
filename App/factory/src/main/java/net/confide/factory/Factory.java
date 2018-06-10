package net.confide.factory;

import android.support.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.confide.common.app.Application;
import net.confide.common.factory.data.DataSource;
import net.confide.factory.model.api.RspModel;
import net.confide.factory.persistence.Account;
import net.confide.factory.utils.DBFlowExclusionStrategy;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {

    //单例模式
    private static final Factory instance;
    //全局线程池
    private final Executor executor;
    //全局的Gson
    private final Gson gson;

    static {
        instance = new Factory();
    }

    private Factory() {
        //新建一个容量为4的线程池
        executor = Executors.newFixedThreadPool(4);
        //初始化Gson
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    /**
     * Factory中的初始化
     */
    public static void setup() {
        //数据库初始化
        FlowManager.init(new FlowConfig.Builder(app()).openDatabasesOnInit(true).build());
        //初始化Account
        Account.load(app());
    }

    public static Application app() {
        return Application.getInstance();
    }

    /**
     * 异步执行
     */
    public static void runOnAsyn(Runnable runnable) {
        //将runnable交给线程池执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局Gson
     * @return
     */
    public static Gson getGson() {
        return instance.gson;
    }

    /**
     * 错误码解析
     * @param model
     * @param callback 返回错误资源id
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback) {
        if (model == null)
            return;

        // 进行Code区分
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                Application.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;
        }
    }

    private static void decodeRspCode(final @StringRes int resId, DataSource.FailedCallback callback) {
        if (callback != null) {
            callback.onDataNotAvailable(resId);
        }
    }

    /**
     * 账号退出
     */
    private void logout() {

    }

    /**
     * 处理推送过来的消息
     * @param message
     */
    public static void dispatchPush(String message) {
        //TODO
    }
}
