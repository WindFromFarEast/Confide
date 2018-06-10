package net.confide.factory.presenter.account;

import android.text.TextUtils;
import net.confide.common.Common;
import net.confide.common.factory.data.DataSource;
import net.confide.common.factory.presenter.BasePresenter;
import net.confide.factory.R;
import net.confide.factory.data.helper.AccountHelper;
import net.confide.factory.model.api.account.RegisterModel;
import net.confide.factory.model.db.User;
import net.confide.factory.persistence.Account;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User> {

    public RegisterPresenter(RegisterContract.View mView) {
        super(mView);
    }

    /**
     * 用户注册
     */
    @Override
    public void register(String phone, String name, String password) {
        //在start方法中默认启动了loading
        start();
        //得到View接口 （MVP）
        RegisterContract.View view = getView();
        //
        if (!checkMobile(phone)) {
            //输入的手机号不合法
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (name.length() < 2) {
            //姓名需要大于两位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            //密码也需要大于两位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            //输入的参数均合法,进行注册
            RegisterModel model = new RegisterModel(phone, password, name, Account.getPushId());
            //进行网络请求注册用户,this为回调接口
            AccountHelper.register(model, this);
        }
    }

    /**
     * 检查手机号是否合法
     */
    @Override
    public boolean checkMobile(String phone) {
        return !TextUtils.isEmpty(phone) && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    /**
     * 注册成功回调
     * @param user
     */
    @Override
    public void onDataLoaded(User user) {
        // 当网络请求成功，注册好了，回送一个用户信息回来
        // 告知界面，注册成功
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        // 此时是从网络回送回来的，并不保证处于主现场状态
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用主界面注册成功
                view.registerSuccess();
            }
        });
    }

    /**
     * 注册失败回调
     * @param strRes
     */
    @Override
    public void onDataNotAvailable(final int strRes) {
        // 网络请求告知注册失败
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        // 此时是从网络回送回来的，并不保证处于主现场状态
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用主界面注册失败显示错误
                view.showError(strRes);
            }
        });
    }
}
