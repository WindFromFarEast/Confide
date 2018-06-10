package net.confide.push.fragment.account;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import net.confide.common.app.PresenterFragment;
import net.confide.factory.presenter.account.RegisterContract;
import net.confide.factory.presenter.account.RegisterPresenter;
import net.confide.push.R;
import net.confide.push.activities.MainActivity;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户注册Fragment
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter> implements RegisterContract.View {

    private AccountTrigger mAccountTrigger;

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_password)
    EditText mPassword;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //获取Activity的引用
        mAccountTrigger = (AccountTrigger) context;
    }

    /**
     * 初始化Presenter:mPresenter
     * @return
     */
    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    /**
     * 点击注册按钮后回调
     */
    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        //调用Presenter进行用户注册
        mPresenter.register(phone, name, password);
    }

    /**
     * 点击“已经注册,登录”后回调
     */
    @OnClick(R.id.txt_go_login)
    void onShowLoginClick() {
        //切换到登录Fragment
        mAccountTrigger.triggerView();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        //当提示需要显示错误时触发
        mLoading.stop();//停止Loading
        //使能输入控件
        mPhone.setEnabled(true);
        mName.setEnabled(true);
        mPassword.setEnabled(true);
        //注册按钮可以再次点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //正在注册时回调的方法
        //
        mLoading.start();
        //不使能输入控件
        mPhone.setEnabled(false);
        mName.setEnabled(false);
        mPassword.setEnabled(false);
        //不允许再次点击注册按钮
        mSubmit.setEnabled(false);
    }

    /**
     * 注册成功时回调
     */
    @Override
    public void registerSuccess() {
        //注册成功,直接登录,跳转到MainActivity
        MainActivity.show(getContext());
        //注销当前界面
        getActivity().finish();
    }
}
