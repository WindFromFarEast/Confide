package net.confide.factory.persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.confide.factory.Factory;
import net.confide.factory.model.api.account.AccountRspModel;
import net.confide.factory.model.db.User;
import net.confide.factory.model.db.User_Table;

import org.w3c.dom.Text;

/**
 * 用户信息工具类：用来对当前用户的判断和操作
 */
public class Account {

    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";

    //设备推送Id
    private static String pushId;
    //设备Id是否已经绑定到服务器的标志位
    private static boolean isBind;
    //登录状态token
    private static String token;
    //登录的用户id
    private static String userId;
    //登录的账户
    private static String account;

    /**
     * 存储数据到xml文件
     */
    private static void save(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(), Context.MODE_PRIVATE);
        sp.edit().putString(KEY_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND, isBind)
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();
    }

    /**
     * 加载已缓存的设备pushId
     * @param context
     */
    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(), Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        isBind = sp.getBoolean(KEY_IS_BIND, false);
        token = sp.getString(KEY_TOKEN, "");
        userId = sp.getString(KEY_USER_ID, "");
        account = sp.getString(KEY_ACCOUNT, "");
    }

    /**
     * 设置并存储设备pushId
     * @param pushId
     */
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 获取当前设备pushId
     * @return
     */
    public static String getPushId() {
        return Account.pushId;
    }

    /**
     * 判断当前账号是否处于登录状态
     * @return
     */
    public static boolean isLogin() {
        return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token);
    }

    /**
     * 是否已经完善用户信息
     * @return
     */
    public static boolean isCompleted() {
        if (isLogin()) {
            //在成功登录的情况下,判断用户的头像、描述、性别是否完善
            User self = getUser();
            return !TextUtils.isEmpty(self.getDesc())
                    && !TextUtils.isEmpty(self.getPortrait())
                    && self.getSex() != 0;
        }
        //未登录时默认未完善用户信息
        return false;
    }

    /**
     * 判断是否已经绑定到服务器
     * @return
     */
    public static boolean isBind() {
        return isBind;
    }

    /**
     * 设置绑定状态
     * @param isBind
     */
    public static void setBind(boolean isBind){
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到xml
     * @param model
     */
    public static void login(AccountRspModel model) {
        //存储当前登录的token,用户id,方便从数据库查询信息
        Account.token = model.getToken();
        Account.account = model.getAccount();
        Account.userId = model.getUser().getId();
        save(Factory.app());
    }

    /**
     * 获取当前登录的用户信息
     * @return
     */
    public static User getUser() {
        return TextUtils.isEmpty(userId) ? new User() : SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    /**
     * 得到当前用户id
     * @return
     */
    public static String getUserId() {
        return getUser().getId();
    }

    /**
     * 获取登录token
     * @return
     */
    public static String getToken() {
        return token;
    }
}
