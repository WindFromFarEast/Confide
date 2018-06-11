package net.confide.factory.presenter.search;

import net.confide.common.factory.presenter.BasePresenter;

/**
 * 搜索功能MVP架构中的UserPresenter具体实现类
 */
public class SearchUserPresenter extends BasePresenter<SearchContract.UserView> implements SearchContract.Presenter {

    public SearchUserPresenter(SearchContract.UserView mView) {
        super(mView);
    }

    @Override
    public void search(String content) {

    }
}
