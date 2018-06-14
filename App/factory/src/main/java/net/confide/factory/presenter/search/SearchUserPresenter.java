package net.confide.factory.presenter.search;

import net.confide.common.factory.data.DataSource;
import net.confide.common.factory.presenter.BasePresenter;
import net.confide.factory.data.helper.UserHelper;
import net.confide.factory.model.card.UserCard;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * 搜索功能MVP架构中的UserPresenter具体实现类
 */
public class SearchUserPresenter extends BasePresenter<SearchContract.UserView> implements SearchContract.Presenter, DataSource.Callback<List<UserCard>> {

    private Call searchCall;

    public SearchUserPresenter(SearchContract.UserView mView) {
        super(mView);
    }

    /**
     * 搜索接口
     * @param content
     */
    @Override
    public void search(String content) {
        start();
        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            //如果有上一次的搜索请求,并且没有被取消,那么取消上一次的搜索请求,仅进行这一次的搜索请求
            call.cancel();
        }
        searchCall = UserHelper.search(content, this);
    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        final SearchContract.UserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    //搜索成功
                    view.onSearchDone(userCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final SearchContract.UserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    //搜索失败
                    view.showError(strRes);
                }
            });
        }
    }
}
