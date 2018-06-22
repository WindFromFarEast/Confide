package net.confide.factory.data.user;


import net.confide.factory.model.card.UserCard;

/**
 * 用户中心的基本定义接口
 */
public interface UserCenter {

    //分发处理用户卡片的信息,并更新到数据库
    void dispatch(UserCard... cards);
}
