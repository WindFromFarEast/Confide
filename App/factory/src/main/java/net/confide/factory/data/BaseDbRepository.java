package net.confide.factory.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.confide.common.factory.data.DbDataSource;
import net.confide.common.utils.CollectionUtil;
import net.confide.factory.data.helper.DbHelper;
import net.confide.factory.model.db.BaseDbModel;
import net.confide.factory.model.db.User;
import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础的数据库仓库
 * 实现对数据库的基本监听操作
 */
public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>
        , DbHelper.ChangedListener<Data>, QueryTransaction.QueryResultListCallback<Data>{

    //和Presenter交互的回调接口
    private SucceedCallback<List<Data>> callback;
    //缓存的数据源
    private final List<Data> dataList = new LinkedList<>();
    private Class<Data> dataClass;//当前泛型对应的Class信息

    @SuppressWarnings("unchecked")
    public BaseDbRepository() {
        //拿到当前类的泛型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback = callback;
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    @Override
    public void onDataSave(Data[] list) {
        boolean isChanged = false;
        //当数据库变更时回调
        for (Data data : list) {
            //是我关注的用户,同时不是本人
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        if (isChanged) {
            notifyDataChange();
        }
    }

    @Override
    public void onDataDelete(Data[] list) {
        //在删除情况下不用过滤判断
        boolean isChanged = false;
        //当数据库删除时回调
        for (Data data : list) {
            if (dataList.remove(data)) {
                isChanged = true;
            }
        }
        //有数据变更则进行界面刷新
        if (isChanged) {
            notifyDataChange();
        }
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        //数据库加载数据成功的回调
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }
        //转变为数组
        Data[] users = CollectionUtil.toArray(tResult, dataClass);
        //回到数据集更新的操作中
        onDataSave(users);
    }

    //插入或者更新
    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            //替换
            replace(index, data);
        } else {
            //插入
            insert(data);
        }
    }

    /**
     * 插入
     * @param data
     */
    private void insert(Data data) {
        dataList.add(data);
    }

    /**
     * 替换
     * @param index
     * @param data
     */
    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    private int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    protected abstract boolean isRequired(Data data);

    /**
     * 添加对数据库的观察者
     */
    protected void registerDbChangedListener() {
        DbHelper.addChangedListener(dataClass, this);
    }

    /**
     * 通知界面刷新
     */
    private void notifyDataChange() {
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null) {
            callback.onDataLoaded(dataList);
        }
    }
}
