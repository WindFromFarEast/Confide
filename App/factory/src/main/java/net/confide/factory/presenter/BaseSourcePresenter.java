package net.confide.factory.presenter;


import net.confide.common.factory.data.DataSource;
import net.confide.common.factory.data.DbDataSource;
import net.confide.common.factory.presenter.BaseContract;
import net.confide.common.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * 基础的仓库源Presenter
 */
public abstract class BaseSourcePresenter<Data, ViewModel, Source extends DbDataSource<Data>, View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel, View> implements DataSource.SucceedCallback<List<Data>> {

    protected Source mSource;

    public BaseSourcePresenter(Source source, View mView) {
        super(mView);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null)
            mSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}
