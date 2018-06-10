package net.confide.factory.presenter.account;

import net.confide.common.factory.presenter.BaseContract;

/**
 * MVP模式契约——登录
 */
public interface LoginContract {

    interface View extends BaseContract.View<Presenter>{
        //登录成功
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter {
        //发起一个登录
        void login(String phone, String password);
    }
}
