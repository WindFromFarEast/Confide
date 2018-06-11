package net.confide.factory.presenter.search;

import net.confide.common.factory.presenter.BasePresenter;


/**
 * 搜索功能MVP架构中的GroupPresenter具体实现类
 */
public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView> implements SearchContract.Presenter {

    public SearchGroupPresenter(SearchContract.GroupView mView) {
        super(mView);
    }

    @Override
    public void search(String content) {

    }
}
