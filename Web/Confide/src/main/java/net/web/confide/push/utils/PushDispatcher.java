package net.web.confide.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.web.confide.push.bean.api.base.PushModel;
import net.web.confide.push.bean.db.User;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息推送工具类
 */
public class PushDispatcher {
    private static String appId = "Srn3teI3vn8vtzM3B3pV87";
    private static String appKey = "uAeqFhxVWo7TjZbG4jvHe3";
    private static String masterSecret = "EpzdGLN4Mf9zdxTQawu9D1";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
    //最根本的发送者
    private IGtPush pusher;
    //要收到消息的人和内容的列表
    private List<BatchBean> beans = new ArrayList<>();

    public PushDispatcher() {
        //最根本的发送者
        pusher = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 添加消息到要发送的消息列表
     * @param receiver 消息接收者
     * @param model PushModel
     * @return 是否添加成功
     */
    public boolean add(User receiver, PushModel model) {
        //基础检查,必须有接受者的设备id号
        if (receiver == null || Strings.isNullOrEmpty(receiver.getPushId()) || model == null) {
            return false;
        }
        String pushString  = model.getPushString();
        if (Strings.isNullOrEmpty(pushString)) {
            return false;
        }
        //构建一个目标+内容的封装类对象
        BatchBean bean = buildMessage(receiver.getPushId(), pushString);
        //加入到消息列表
        beans.add(bean);
        return true;
    }

    /**
     * 对要发送的数据进行封装
     * @param clientId 接收者设备Id
     * @param text 发送的数据
     * @return BatchBean
     */
    private BatchBean buildMessage(String clientId, String text) {
        SingleMessage message = new SingleMessage();
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(1); // 这个Type为int型，填写1则自动启动app
        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 3600 * 1000);//离线消息时长
        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);
        return new BatchBean(message, target);
    }


    /**
     * 消息最终发送
     */
    public boolean submit() {
        IBatch batch = pusher.getBatch();
        //是否有数据需要发送
        boolean haveData = false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!haveData) {
            //没有数据直接返回
            return false;
        }
        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //失败的情况下重新发送一次
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher").log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.getLogger("PushDispatcher").log(Level.WARNING, "推送服务器响应异常");
        return false;
    }

    //给每个人发送消息的Bean封装
    private static class BatchBean {
        SingleMessage message;//发送的单条消息
        Target target;//目标人

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }

}
