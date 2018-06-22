package net.confide.factory.data.helper;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import net.confide.factory.model.db.AppDataBase;
import net.confide.factory.model.db.Group;
import net.confide.factory.model.db.GroupMember;
import net.confide.factory.model.db.Group_Table;
import net.confide.factory.model.db.Message;
import net.confide.factory.model.db.Session;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据库辅助工具类
 * 增删改
 */
public class DbHelper {

    //单例模式
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    private DbHelper() { }

    /**
     * 观察者集合
     * Class<?> 观察的表
     * Set<ChangedListener> 每个表对应的观察者有很多
     */
    private final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    /**
     * 从所有的观察者中，获取到某一个表的所有监听器
     * @param modelClass 表对应的class
     * @param <Model> 泛型
     * @return
     */
    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> modelClass) {
        if (changedListeners.containsKey(modelClass)) {
            return changedListeners.get(modelClass);
        }
        return null;
    }

    /**
     * 添加某个表的观察者
     * @param tClass 被观察的表
     * @param listener 观察者
     * @param <Model> 表的泛型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass, ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            changedListeners = new HashSet<>();
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    /**
     * 删除某个表的指定观察者
     * @param tClass 表的Class
     * @param listener 观察者
     * @param <Model> 泛型
     */
    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass, ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            //容器本身为null,代表根本没有观察者
            return;
        }
        //删除指定观察者
        changedListeners.remove(listener);
    }

    /**
     * 保存到本地数据库
     * @param tClass Class信息
     * @param models Class实例数组
     * @param <Model> 限定能保存的类型必须是继承自数据库基础类BaseModel的类
     */
    public static<Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //网络请求成功,保存UserCard列表到本地数据库,即联系人列表
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);//获取当前数据库的管理者
        //提交一个事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter =  FlowManager.getModelAdapter(tClass);
                //保存
                adapter.saveAll(Arrays.asList(models));
                //唤起通知
                instance.notifySave(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 删除数据库内的数据
     * @param tClass Class信息
     * @param models Class实例数组
     * @param <Model> 限定能删除的类型必须是继承自数据库基础类BaseModel的类
     */
    public static<Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);//获取当前数据库的管理者
        //提交一个事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter =  FlowManager.getModelAdapter(tClass);
                //删除
                adapter.deleteAll(Arrays.asList(models));
                //唤起删除
                instance.notifyDelete(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 通知保存
     * @param tClass ...
     * @param models ...
     * @param <Model> ...
     */
    @SuppressWarnings({"unused", "unchecked"})
    private final <Model extends BaseModel> void notifySave(final Class<Model> tClass, final Model... models) {
        //首先拿到表的所有观察者
        final Set<ChangedListener> listeners = getListeners(tClass);
        //通知观察者
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models);
            }
        }
        //例外情况,群成员变更需要通知群信息更新
        //消息变化，应该通知会话列表更新
        if (GroupMember.class.equals(tClass)) {
            //群成员变更,通知群信息更新
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            //消息变更,通知消息列表更新
            updateSession((Message[]) models);
        }
    }

    /**
     * 通知删除
     * @param tClass ...
     * @param models ...
     * @param <Model> ...
     */
    @SuppressWarnings({"unused", "unchecked"})
    private final <Model extends BaseModel> void notifyDelete(final Class<Model> tClass, final Model... models) {
        //首先拿到表的所有观察者
        final Set<ChangedListener> listeners = getListeners(tClass);
        //通知观察者
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }
        //例外情况,群成员变更需要通知群信息更新
        //消息变化，应该通知会话列表更新
        if (GroupMember.class.equals(tClass)) {
            //群成员变更,通知群信息更新
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            //消息变更,通知消息列表更新
            updateSession((Message[]) models);
        }
    }

    /**
     * 从群成员中找出对应的群,并对群信息进行更新
     * @param members
     */
    private void updateGroup(GroupMember... members) {
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members) {
            //添加群id
            groupIds.add(member.getGroup().getId());
        }
        //异步数据查询并发起通知
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);//获取当前数据库的管理者
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //找到需要通知的群
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();
                instance.notifySave(Group.class, groups.toArray(new Group[0]));
            }
        }).build().execute();
    }

    /**
     * 从消息列表中找出对应的会话,并对会话列表进行更新
     * @param messages
     */
    private void updateSession(Message... messages) {
        //标识一个Session的唯一性
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }
        //异步数据查询并发起通知
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);//获取当前数据库的管理者
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifies.size()];
                int index = 0;

                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    if (session == null) {
                        //第一次发消息,创建一个会话
                        session = new Session(identify);
                    }
                    //把会话刷新到当前Message的最新状态
                    session.refreshToNow();
                    //数据库存储
                    adapter.save(session);
                    //添加到Session集合
                    sessions[index++] = session;
                }
                instance.notifySave(Session.class, sessions);
            }
        }).build().execute();
    }

    /**
     * 通知监听器
     */
    @SuppressWarnings({"unused", "unchecked"})
    public interface ChangedListener<Data extends BaseModel> {
        //通知数据已经保存
        void onDataSave(Data... list);
        //通知数据已经删除
        void onDataDelete(Data... list);
    }
}
