package net.confide.factory.model.api.message;

import net.confide.factory.model.card.MessageCard;
import net.confide.factory.model.db.Message;
import net.confide.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

public class MsgCreateModel {

    //ID从客户端生成UUDI
    private String id;

    //消息内容
    private String content;

    //附件
    private String attach;

    //消息类型
    private int type = Message.TYPE_STR;

    //接收者id
    private String receiverId;
    //接收者类型:群或人
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    /**
     * 建造者模式,建造MsgCreateModel
     */
    public static class Builder {

        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        public Builder receiver(String receiverId, int receiverType) {
            model.receiverId = receiverId;
            model.receiverType = receiverType;
            return this;
        }

        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel build() {
            return this.model;
        }
    }

    //当前Model对应的Card
    private MessageCard card;

    /**
     * 为当前Model创建一个对应的Card
     * @return
     */
    public MessageCard buildCard() {
        if (card == null) {
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());
            //如果是群
            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }

    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();
        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }
        return model;
    }
}
