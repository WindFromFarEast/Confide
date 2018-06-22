package net.confide.factory.data.message;

import net.confide.factory.model.card.MessageCard;

/**
 * 消息中心,进行消息卡片的消费
 */
public interface MessageCenter {

    void dispatch(MessageCard... cards);
}
