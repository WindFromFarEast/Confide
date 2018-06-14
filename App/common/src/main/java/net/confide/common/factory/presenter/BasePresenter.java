package net.confide.common.factory.presenter;


public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter{

    private T mView;

    public BasePresenter(T mView) {
        setView(mView);
    }

    @SuppressWarnings("unchecked")
    protected void setView(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    protected final T getView() {
        return mView;
    }

    /**
     * 加载中
     */
    @Override
    public void start() {
        T view = mView;
        if (view != null) {
            view.showLoading();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if (view != null) {
            view.setPresenter(null);
        }
    }
}
