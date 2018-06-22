package net.confide.factory.data.message;

import android.text.TextUtils;

import net.confide.factory.data.helper.DbHelper;
import net.confide.factory.data.helper.GroupHelper;
import net.confide.factory.data.helper.MessageHelper;
import net.confide.factory.data.helper.UserHelper;
import net.confide.factory.model.card.MessageCard;
import net.confide.factory.model.db.Group;
import net.confide.factory.model.db.Message;
import net.confide.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 消息中心的实现类
 */
public class MessageDispatcher implements MessageCenter{

    //当前类以单例模式提供
    private static volatile MessageCenter instance;
    //单线程的线程池,用于对卡片消息的处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * 单例模式提供方法
     */
    public static MessageCenter instance() {
        if (instance == null) {
            synchronized (MessageDispatcher.class) {
                if (instance == null) {
                    instance = new MessageDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        // 丢到单线程池中
        executor.execute(new MessageCardHandler(cards));
    }

    /**
     * 消息卡片的处理线程
     */
    private class MessageCardHandler implements Runnable {
        private final MessageCard[] cards;

        MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            //卡片基础信息过滤
            for (MessageCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;
                //消息卡片有可能是推送过来的,也有可能是自己造的
                //发送消息流程：写消息-->存储本地->发送网络->网络返回->刷新界面
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    // 如果本地消息显示已经完成则不做处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;
                    // 新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE)
                        //修改信息时间
                        message.setCreateAt(card.getCreateAt());
                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    //更新消息状态
                    message.setStatus(card.getStatus());
                } else {
                    //没找到本地消息 初次在数据库存储
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    if (receiver == null && group == null)
                        continue;

                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
