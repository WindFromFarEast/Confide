package net.web.confide.push.factory;

import com.google.common.base.Strings;
import net.web.confide.push.bean.api.base.PushModel;
import net.web.confide.push.bean.card.MessageCard;
import net.web.confide.push.bean.db.*;
import net.web.confide.push.utils.Hib;
import net.web.confide.push.utils.PushDispatcher;
import net.web.confide.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息存储与处理的工具类
 */
public class PushFactory {

    /**
     * 发送一条消息并在当前的发送历史记录中存储
     * @param sender
     * @param message
     */
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null)
            return;
        //消息卡片,用于发送到客户端
        MessageCard card = new MessageCard(message);
        //要推送的字符串
        String entity = TextUtil.toJson(card);
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            //给好友发送消息
            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null)
                return;
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            //真实推送的model
            PushModel pushModel = new PushModel();
            //每一条历史记录都是独立的,可以单独发送
            pushModel.add(history.getEntityType(), history.getEntity());
            //把PushModel交给发送者进行发送
            dispatcher.add(receiver, pushModel);
            //保存历史记录到数据库
            Hib.queryOnly(session -> session.save(history));
        } else {
            //给群发送消息
            Group group = message.getGroup();
            if (group == null) {
                group = GroupFactory.findById(message.getGroupId());
            }
            if (group == null) {
                return;
            }
            Set<GroupMember> members = GroupFactory.getMembers(group);
            if (members == null || members.size() == 0) {
                return;
            }
            //过滤自己
            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId()
                            .equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            if (members.size() == 0) {
                return;
            }
            List<PushHistory> histories = new ArrayList<>();
            addGroupMembersPushModel(dispatcher, histories, members, entity, PushModel.ENTITY_TYPE_MESSAGE);
            //保存到数据库
            Hib.queryOnly(session -> {
                for (PushHistory history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }
        //消息发送者进行消息发送
        dispatcher.submit();
    }

    /**
     * 给群成员构建一个消息
     * 并且把消息存储到数据库的历史记录中
     * @param dispatcher
     * @param histories
     * @param members
     * @param entity
     * @param entityTypeMessage
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher, List<PushHistory> histories,
                                                 Set<GroupMember> members, String entity, int entityTypeMessage) {
        for (GroupMember member : members) {
            User receiver = member.getUser();
            if (receiver == null)
                return;
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);
            //构建消息Model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());
            dispatcher.add(receiver ,pushModel);
        }
    }
}
