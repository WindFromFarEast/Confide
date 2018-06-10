package net.confide.common.app;

import android.content.Context;
import net.confide.common.factory.presenter.BaseContract;


public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment implements BaseContract.View<Presenter>{

    protected Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //初始化Presenter
        initPresenter();
    }

    /**
     * 初始化Presenter
     * @return
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        Application.showToast(str);
    }

    @Override
    public void showLoading() {
        //TODO 显示一个加载框
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }
}
