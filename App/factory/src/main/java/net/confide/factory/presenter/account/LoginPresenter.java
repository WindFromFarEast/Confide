package net.confide.factory.presenter.account;

import android.text.TextUtils;

import net.confide.common.factory.data.DataSource;
import net.confide.common.factory.presenter.BasePresenter;
import net.confide.factory.R;
import net.confide.factory.data.helper.AccountHelper;
import net.confide.factory.model.api.account.LoginModel;
import net.confide.factory.model.db.User;
import net.confide.factory.persistence.Account;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 登录逻辑实现(Presenter)
 */
public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter, DataSource.Callback<User> {

    public LoginPresenter(LoginContract.View mView) {
        super(mView);
    }

    @Override
    public void login(String phone, String password) {
        start();
        //获取当前Presenter对应的View
        final LoginContract.View view = getView();
        //输入参数检查
        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(password)) {
            view.showError(R.string.data_account_login_invalid_parameter);
        } else {
            LoginModel model = new LoginModel(phone, password, Account.getPushId());
            AccountHelper.login(model, this);
        }
    }

    @Override
    public void onDataLoaded(User user) {
        final LoginContract.View view = getView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final LoginContract.View view = getView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
