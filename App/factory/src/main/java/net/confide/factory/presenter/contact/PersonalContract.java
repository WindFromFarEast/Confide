package net.confide.factory.presenter.contact;

import net.confide.common.factory.presenter.BaseContract;
import net.confide.factory.model.db.User;


/**
 * 查看用户详细信息MVP架构契约
 */
public interface PersonalContract  {

    interface Presenter extends BaseContract.Presenter {
        //获取用户信息
        User getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter> {
        //获取用户id
        String getUserId();
        //Presenter加载用户信息成功后回调的方法
        void onLoadDone(User user);
        //是否发起聊天
        void allowSayHello(boolean isAllow);
        //设置关注状态
        void setFollowStatus(boolean isFollow);
    }
}
