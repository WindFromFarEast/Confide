package net.confide.common.app;


import net.confide.common.factory.presenter.BaseContract;

/**
 *
 */
public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter> extends ToolbarActivity
    implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //界面关闭时销毁Presenter
        if (mPresenter != null) {
            mPresenter.destroy();
        }
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
            //如果当前Activity设置了占位控件,优先使用占位控件来显示Loading
            mPlaceHolderView.triggerLoading();
        }
    }

    /**
     * 隐藏Loading
     */
    protected void hideLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }
}
