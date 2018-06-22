package net.confide.factory.data.user;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import net.confide.common.factory.data.DataSource;
import net.confide.factory.data.BaseDbRepository;
import net.confide.factory.model.db.User;
import net.confide.factory.model.db.User_Table;
import net.confide.factory.persistence.Account;
import java.util.List;

/**
 * 联系人仓库,作用是观察数据库中User表的变化,实质上为观察者
 */
public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {

    /**
     * 加载
     * @param callback 加载成功后的回调
     */
    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)//正排序
                .limit(100)//限制一百条数据
                .async()//异步加载
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 检查User是否是需要关注的数据
     * @param user
     * @return
     */
    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }

}
