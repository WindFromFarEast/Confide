package net.confide.factory.presenter.contact;

import net.confide.common.factory.presenter.BaseContract;
import net.confide.common.widget.recycler.RecyclerAdapter;
import net.confide.factory.model.db.User;

/**
 * 查询联系人列表MVP契约
 */
public interface ContactContract{

    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }
}
