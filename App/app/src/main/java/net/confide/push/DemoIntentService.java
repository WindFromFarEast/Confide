package net.confide.push;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import net.confide.factory.Factory;
import net.confide.factory.data.helper.AccountHelper;
import net.confide.factory.persistence.Account;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class DemoIntentService extends GTIntentService {

    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        //收到常规消息
        byte[] payload = msg.getPayload();
        if (payload != null) {
            String message = new String(payload);
            Log.e(TAG, "GET_MSG_DATA" + message);
            onMessageArrived(message);
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        //收到设备pushId
        onClientInit(clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
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
