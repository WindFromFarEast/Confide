package net.confide.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.confide.common.factory.data.DataSource;
import net.confide.common.factory.presenter.BasePresenter;
import net.confide.common.widget.recycler.RecyclerAdapter;
import net.confide.factory.data.helper.UserHelper;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.db.AppDataBase;
import net.confide.factory.model.db.User;
import net.confide.factory.model.db.User_Table;
import net.confide.factory.persistence.Account;
import net.confide.factory.utils.DiffUiDataCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * 联系人列表MVP——Presenter
 */
public class ContactPresenter extends BasePresenter<ContactContract.View> implements ContactContract.Presenter {

    public ContactPresenter(ContactContract.View mView) {
        super(mView);
    }

    /**
     * 数据加载
     */
    @Override
    public void start() {
        super.start();
        //首先加载本地数据库中的联系人数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)//正排序
                .limit(100)//限制一百条数据
                .async()//异步加载
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<User>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction,
                                                  @NonNull List<User> tResult) {
                        //使用RecyclerView列表的Adapter替换数据源
                        getView().getRecyclerAdapter().replace(tResult);
                        getView().onAdapterDataChange();

                    }
                })
                .execute();
        //再加载网络数据库中的联系人数据
        UserHelper.refreshContacts(new DataSource.Callback<List<UserCard>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //网络请求失败,因为本地已有数据
            }

            @Override
            public void onDataLoaded(final List<UserCard> userCards) {
                //将UserCard列表转化为User列表
                final List<User> users = new ArrayList<>();
                for (UserCard userCard : userCards) {
                    users.add(userCard.build());
                }
                //网络请求成功,保存UserCard列表到本地数据库,即联系人列表
                DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
                definition.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        FlowManager.getModelAdapter(User.class)
                                .saveAll(users);
                    }
                }).build().execute();
                //将老数据和新数据进行差异比较
                List<User> old = getView().getRecyclerAdapter().getItems();
                diff(users, old);
            }
        });

        //TODO
    }

    /**
     * 比较从网络获取的联系人最新数据和本地数据库保存的旧数据
     * @param newList 新数据
     * @param oldList 旧数据
     */
    private void diff(List<User> newList, List<User> oldList) {
        //差异计算
        DiffUtil.Callback callback = new DiffUiDataCallback<>(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //数据对比完成后进行新数据的赋值
        getView().getRecyclerAdapter().replace(newList);
        //刷新列表
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
        getView().onAdapterDataChange();
    }
}
