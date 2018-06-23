package net.web.confide.push.service;

import net.web.confide.push.bean.api.base.ResponseModel;
import net.web.confide.push.bean.api.message.MessageCreateModel;
import net.web.confide.push.bean.api.user.UpdateInfoModel;
import net.web.confide.push.bean.card.MessageCard;
import net.web.confide.push.bean.card.UserCard;
import net.web.confide.push.bean.db.Group;
import net.web.confide.push.bean.db.Message;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.factory.GroupFactory;
import net.web.confide.push.factory.MessageFactory;
import net.web.confide.push.factory.PushFactory;
import net.web.confide.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 消息发送的入口
 */
@Path("/msg")
public class MessageService extends BaseService{

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        if (!MessageCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //查询是否已经在数据库中有这条消息
        Message message = MessageFactory.findById(model.getId());
        if (message != null) {
            return ResponseModel.buildOk(new MessageCard(message));
        }
        if (model.getReceiverType() == Message.RECEIVER_TYPE_GROUP) {
            return pushToGroup(self, model);
        } else {
            return pushToUser(self, model);
        }
    }

    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null) {
            return ResponseModel.buildNotFoundUserError("Can't find receiver");
        }
        if (receiver.getId().equalsIgnoreCase(sender.getId())) {
            //不能自己给自己发送信息
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        Message message = MessageFactory.add(sender, receiver, model);
        return buildAndPushResponse(sender, message);
    }

    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        Group group = GroupFactory.findById(sender, model.getReceiverId());
        if (group == null) {
            return ResponseModel.buildNotFoundUserError("Can't find receiver group");
        }
        //数据库添加
        Message message = MessageFactory.add(sender, group, model);
        //走通用的推送方法
        return buildAndPushResponse(sender, message);
    }

    /**
     * 推送并构建一个返回信息
     * @param sender
     * @param message
     * @return
     */
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message == null) {
            //存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //进行推送
        PushFactory.pushNewMessage(sender, message);
        return ResponseModel.buildOk(new MessageCard(message));
    }
}
