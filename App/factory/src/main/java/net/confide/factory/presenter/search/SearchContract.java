package net.confide.factory.presenter.search;

import net.confide.common.factory.presenter.BaseContract;
import net.confide.factory.model.card.GroupCard;
import net.confide.factory.model.card.UserCard;

import java.util.List;

/**
 * 搜索功能MVP架构契约
 */
public interface SearchContract {

    /**
     * 搜索Presenter
     */
    interface Presenter extends BaseContract.Presenter {
        //搜索
        void search(String content);
    }

    /**
     * 搜索用户界面
     */
    interface UserView extends BaseContract.View<Presenter> {
        //搜索成功回调
        void onSearchDone(List<UserCard> userCards);
    }

    /**
     * 搜索群界面
     */
    interface GroupView extends BaseContract.View<Presenter> {
        //搜索成功回调
        void onSearchDone(List<GroupCard> groupCards);
    }
}
