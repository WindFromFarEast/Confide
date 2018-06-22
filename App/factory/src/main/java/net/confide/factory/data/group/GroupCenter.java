package net.confide.factory.data.group;

import net.confide.factory.model.card.GroupCard;
import net.confide.factory.model.card.GroupMemberCard;

/**
 * 群中心的接口定义
 */
public interface GroupCenter {

    //群卡片处理
    void dispatch(GroupCard... cards);
    //群成员处理
    void dispatch(GroupMemberCard... cards);
}
