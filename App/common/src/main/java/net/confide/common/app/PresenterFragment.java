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
        if (mPlaceHolderView != null) {
            //如果当前Fragment设置了占位控件,优先使用占位控件来显示Error
            mPlaceHolderView.triggerError(str);
        } else {
            //否则使用吐司来提示
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            //如果当前Fragment设置了占位控件,优先使用占位控件来显示Loading
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }
}
