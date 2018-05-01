package net.confide.common.widget.recycler;

/**
 * Adapter手动通知RecyclerView刷新的接口
 */
public interface AdapterCallback<Data> {

    //手动通知RecyclerView进行刷新
    void update(Data data);
}
