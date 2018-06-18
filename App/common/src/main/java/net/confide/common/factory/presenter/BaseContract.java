package net.confide.common.factory.presenter;

import android.support.annotation.StringRes;
import net.confide.common.widget.recycler.RecyclerAdapter;

/**
 * MVP模式中公共的契约
 */
public interface BaseContract {

    /**
     * 基础View层
     * @param <T>
     */
    interface View<T extends Presenter> {
        //显示一个字符串错误
        void showError(@StringRes int str);
        //显示进度条
        void showLoading();
        //设置Presenter
        void setPresenter(T presenter);
    }

    /**
     * 基础Presenter层
     */
    interface Presenter {
        //公用的开始方法
        void start();
        //公用的销毁方法
        void destroy();
    }

    /**
     * 拥有RecyclerView列表的View层
     * @param <ViewModel> RecyclerView中需要的数据源
     * @param <T>
     */
    interface RecyclerView<T extends Presenter, ViewModel> extends View<T> {
        //为了单独更新一条数据(局部刷新),需要View层提供Adapter
        RecyclerAdapter<ViewModel> getRecyclerAdapter();
        //数据源发生变化,回调此方法通知View层
        void onAdapterDataChange();
    }
}
