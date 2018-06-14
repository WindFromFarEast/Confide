package net.confide.factory.presenter.contact;


import net.confide.common.factory.presenter.BaseContract;
import net.confide.factory.model.card.UserCard;

/**
 * 关注功能MVP模式契约
 */
public interface FollowContract {

    interface Presenter extends BaseContract.Presenter {
        //关注一个人
        void follow(String id);
    }

    interface View extends BaseContract.View<Presenter> {
        //成功关注
        void onFollowSucceed(UserCard userCard);
    }
}
