package net.confide.push.fragment.search;

import net.confide.common.app.PresenterFragment;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.presenter.search.SearchContract;
import net.confide.factory.presenter.search.SearchUserPresenter;
import net.confide.push.R;
import net.confide.push.activities.SearchActivity;

import java.util.List;

/**
 * 搜索界面中的用户搜索Fragment
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter> implements SearchActivity.SearchFragment, SearchContract.UserView {


    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    /**
     * 注意这个不是Presenter中的search,而是SearchFragment接口中定义的search
     * 在这里需要调用Presenter中的Fragment
     * @param content
     */
    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {

    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        //初始化当前View的Presenter
        return new SearchUserPresenter(this);
    }
}
