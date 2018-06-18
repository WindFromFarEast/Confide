package net.confide.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.confide.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * RecyclerView适配器基类——抽象类,不能被实例化,只能被继承
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class RecyclerAdapter<Data> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {

    //数据源
    private List<Data> mDataList;
    //点击子项监听器
    private AdapterListener<Data> mListener;

    /**
     * 适配器构造方法
     */
    public RecyclerAdapter() {
        this(null);
    }

    /**
     * 适配器构造方法
     * @param listener 子项点击事件监听器
     */
    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(), listener);
    }

    /**
     * 适配器构造方法
     * @param dataList 数据源
     * @param listener 子项点击事件监听器
     */
    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }


    /**
     * 重写默认的布局类型方法
     * @param position
     * @return 返回值为xml布局文件id
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局的类型,交由子类重写
     * @param position
     * @param data
     * @return XML布局文件id,用于创建ViewHolder
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 创建ViewHolder
     * @param parent
     * @param viewType 约定为xml布局文件资源id
     * @return ViewHolder实例对象
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewType, parent, false);
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);
        //将view的tag设置为ViewHolder
        root.setTag(R.id.tag_recycler_holder, holder);
        //设置点击事件
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        //进行界面注解绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        //绑定callback
        holder.callback = this;
        return holder;
    }

    /**
     * 创建ViewHolder实例,交给子类重写
     * @param root 根布局
     * @param viewType xml布局文件id
     * @return ViewHolder实例对象
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    /**
     * 每一个子项进入到屏幕时回调的方法,在此进行数据的获取以及绑定到ViewHolder,并且进行界面更新
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> holder, int position) {
        //获取数据
        Data data = mDataList.get(position);
        //绑定数据,同时会触发绑定的回调holder.onBind(),onBind交给ViewHolder的子类重写
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 返回数据源集合
     * @return
     */
    public List<Data> getItems() {
        return mDataList;
    }

    /**
     * 插入一条新数据并刷新
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入多条新数据并刷新
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    /**
     * 插入多条数据并刷新
     * @param dataList
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    /**
     * 删除所有数据并刷新
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 将原来的数据替换为新的数据
     * @param dataList 要替换的数据集合
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    /**
     *
     * @param data
     * @param holder
     */
    @Override
    public void update(Data data, ViewHolder<Data> holder) {
        //得到ViewHolder坐标
        int pos = holder.getAdapterPosition();
        if (pos >= 0) {
            mDataList.remove(pos);
            mDataList.add(pos, data);
            notifyItemChanged(pos);
        }
    }

    /**
     * 点击事件
     * @param v root根布局
     */
    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            int pos = viewHolder.getAdapterPosition();
            mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    /**
     * 长按点击事件
     * @param v root根布局
     * @return 是否响应长按事件
     */
    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            int pos = viewHolder.getAdapterPosition();
            mListener.onItemLongClick(viewHolder, mDataList.get(pos));
        }
        return true;
    }

    /**
     * 设置子项监听器
     * @param listener
     */
    public void setAdapterListener(AdapterListener<Data> listener) {
        mListener = listener;
    }

    /**
     * 将点击事件抛到外面处理的接口
     * @param <Data>
     */
    public interface AdapterListener<Data> {

        //点击事件
        void onItemClick(ViewHolder holder, Data data);
        //长按点击事件
        void onItemLongClick(ViewHolder holder, Data data);
    }

    //ViewHolder
    public abstract static class ViewHolder<Data> extends RecyclerView.ViewHolder {

        //数据
        protected Data mData;
        //
        private Unbinder unbinder;
        //通知RecyclerView刷新数据的接口
        private AdapterCallback<Data> callback;

        //ViewHolder构造方法
        public ViewHolder(View itemView) {
            super(itemView);
        }

        //绑定数据
        void bind(Data data) {
            mData = data;
            //刷新界面
            onBind(data);
        }

        //触发绑定数据的回调,必须被重写
        protected abstract void onBind(Data data);

        //手动刷新
        public void updateData(Data data) {
            if (this.callback != null) {
                callback.update(data, this);
            }
        }

    }

    /**
     * 对回调接口AdapterListener做一次实现
     * @param <Data>
     */
    public abstract static class AdapterListenerImpl<Data> implements AdapterListener<Data> {

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, Data data) {

        }
    }
}
