package net.confide.common.factory.data;

import java.util.List;

/**
 * 基础的数据库数据源接口定义
 * @param <Data>
 */
public interface DbDataSource<Data> extends DataSource {

    //数据源加载
    void load(SucceedCallback<List<Data>> callback);
}
