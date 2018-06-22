package net.confide.factory.presenter.contact;

import android.support.v7.util.DiffUtil;
import net.confide.common.factory.data.DataSource;
import net.confide.common.widget.recycler.RecyclerAdapter;
import net.confide.factory.data.helper.UserHelper;
import net.confide.factory.data.user.ContactDataSource;
import net.confide.factory.data.user.ContactRepository;
import net.confide.factory.model.db.User;
import net.confide.factory.presenter.BaseSourcePresenter;
import net.confide.factory.utils.DiffUiDataCallback;
import java.util.List;

/**
 * 联系人列表MVP——Presenter
 */
public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View> implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {

    public ContactPresenter(ContactContract.View mView) {
        super(new ContactRepository() ,mView);
    }

    /**
     * 数据加载
     */
    @Override
    public void start() {
        super.start();
        //加载网络数据库中的联系人数据
        UserHelper.refreshContacts();
    }

    //需要保证该方法在子线程
    @Override
    public void onDataLoaded(List<User> users) {
        final ContactContract.View view = getView();
        if (view == null) {
            return;
        }
        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        //获取旧数据
        List<User> old = adapter.getItems();
        //差异计算
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //调用基类方法进行界面刷新
        refreshData(result, users);
    }
}
