package net.confide.factory;

import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.confide.common.app.Application;
import net.confide.common.factory.data.DataSource;
import net.confide.factory.data.group.GroupCenter;
import net.confide.factory.data.group.GroupDispatcher;
import net.confide.factory.data.message.MessageCenter;
import net.confide.factory.data.message.MessageDispatcher;
import net.confide.factory.data.user.UserCenter;
import net.confide.factory.data.user.UserDispatcher;
import net.confide.factory.model.api.PushModel;
import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.card.GroupCard;
import net.confide.factory.model.card.GroupMemberCard;
import net.confide.factory.model.card.MessageCard;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.persistence.Account;
import net.confide.factory.utils.DBFlowExclusionStrategy;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {

    private static final String TAG = Factory.class.getSimpleName();
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
     * @param str
     */
    public static void dispatchPush(String str) {
        //首先检查登录状态
        if (!Account.isLogin()) {
            return;
        }
        PushModel model = PushModel.decode(str);
        if (model == null) {
            return;
        }
        Log.e(TAG, model.toString());
        //对推送集合进行遍历
        for (PushModel.Entity entity : model.getEntities()) {
            switch (entity.type) {
                case PushModel.ENTITY_TYPE_LOGOUT: {
                    //退出
                    instance.logout();
                    return;
                }
                case PushModel.ENTITY_TYPE_MESSAGE: {
                    //普通消息
                    MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_FRIEND: {
                    //添加好友
                    UserCard card = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP: {
                    //添加群
                    GroupCard card = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS: {
                    //群成员变更,获取的是群成员列表
                    Type type = new TypeToken<List<GroupMemberCard>>(){}.getType();
                    List<GroupMemberCard> card = getGson().fromJson(entity.content, type);
                    getGroupCenter().dispatch(card.toArray(new GroupMemberCard[0]));
                    break;
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS: {
                    //成员退出
                    //TODO
                    break;
                }
            }
        }
    }

    /**
     * 获取用户中心的实现类
     * @return
     */
    public static UserCenter getUserCenter() {
        return UserDispatcher.instance();
    }

    /**
     * 获取消息中心的实现类
     * @return
     */
    public static MessageCenter getMessageCenter() {
        return MessageDispatcher.instance();
    }

    /**
     * 获取群中心的实现类
     * @return
     */
    public static GroupCenter getGroupCenter() {
        return GroupDispatcher.instance();
    }
}
