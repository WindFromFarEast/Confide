package net.confide.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import net.confide.factory.Factory;
import net.confide.factory.data.helper.AccountHelper;
import net.confide.factory.persistence.Account;

/**
 * 个推消息接收器
 */
public class MessageReceiver extends BroadcastReceiver {

    //日志TAG
    private static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        //判断当前消息意图
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID: {
                Log.i(TAG, "GET_CLIENTID" + bundle.toString());
                //id初始化时
                onClientInit(bundle.getString("clientid"));
                break;
            }
            case PushConsts.GET_MSG_DATA: {
                //收到常规消息
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String message = new String(payload);
                    Log.i(TAG, "GET_MSG_DATA" + message);
                    onMessageArrived(message);
                }
                break;
            }
            default: {
                Log.i(TAG, "OTHER:" + bundle.toString());
                break;
            }
        }
    }

    /**
     * ID初始化
     */
    private void onClientInit(String cid) {
        //设置设备Id
        Log.i("PUSHID", cid);
        Account.setPushId(cid);
        if (Account.isLogin()) {
            //账户处于登录状态,绑定pushId
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 收到消息推送后触发的方法
     * @param message
     */
    private void onMessageArrived(String message) {
        Factory.dispatchPush(message);
    }
}
