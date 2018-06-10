package net.confide.factory.presenter.user;


import net.confide.common.factory.presenter.BaseContract;

/**
 * 更新用户信息基本契约
 */
public interface UpdateInfoContract {

    interface Presenter extends BaseContract.Presenter {
        //更新用户信息
        void update(String photoFilePath, String desc, boolean isMan);
    }

    interface View extends BaseContract.View<Presenter> {
        //用户信息更新成功的回调
        void updateSuccess();
    }
}
