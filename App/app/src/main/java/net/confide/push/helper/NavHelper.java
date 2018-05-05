package net.confide.push.helper;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import android.support.v4.app.Fragment;

/**
 * 工具类
 * 解决对Fragment的调度与重用
 */
public class NavHelper<T> {

    //所有的Tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();
    //上下文
    private final Context context;
    //显示Fragment的容器的ID
    private final int containerId;
    //FragmentManager
    private final FragmentManager fragmentManager;
    //
    private final OnTabChangedListener<T> listener;
    //当前选中的Tab
    private Tab<T> currentTab;

    /**
     * 构造方法
     */
    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, OnTabChangedListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**
     * 添加Tab
     * @param menuId Tab对应的菜单子项Id
     * @param tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        //允许流式添加
        return this;
    }

    /**
     * 获取当前显示的Tab
     * @return 当前显示的Tab
     */
    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 点击底部导航栏的菜单子项后,点击事件的回调方法将对事件的处理交给该方法
     * @param itemId 被点击的菜单子项的id
     * @return 是否处理成功
     */
    public boolean performClickMenu(int itemId) {
        //在tabs集合中寻找点击的菜单子项对应的tab,如果有就进行处理
        Tab<T> tab = tabs.get(itemId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行Tab选择操作
     * @param tab 点击的菜单子项
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;
        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                //点击的tab就是之前选中的tab,进行tab的刷新
                notifyReselect(tab);
                return;
            }
        }
        currentTab = tab;
        doTabChange(currentTab, oldTab);
    }

    /**
     * Fragment的调度
     * @param newTab 点击的Tab
     * @param oldTab 上一次点击的Tab
     */
    private void doTabChange(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (oldTab != null) {
            if (oldTab.fragment != null) {
                //从界面中移除旧Tab对应的Fragment
                transaction.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if (newTab.fragment == null) {
                Fragment fragment = Fragment.instantiate(context, newTab.clx.getName(), null);
                newTab.fragment = fragment;
                transaction.add(containerId, fragment, newTab.clx.getName());
            } else {
                transaction.attach(newTab.fragment);
            }
        }
        transaction.commit();
        //回调：通知Activity已经完成Tab点击的处理
        notifyTabSelected(newTab, oldTab);
    }

    /**
     * 回调：通知Activity已经完成点击新Tab的处理,利用接口让Activity自定义后续操作
     * @param newTab
     * @param oldTab
     */
    private void notifyTabSelected(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }

    /**
     * 通知Activity已经完成二次点击Tab的处理
     * @param tab
     */
    private void notifyTabReselected(Tab<T> tab) {

    }

    /**
     * 二次点击同一个tab时触发tab的刷新
     * @param tab 触发二次点击的tab
     */
    private void notifyReselect(Tab<T> tab) {

    }

    /**
     * 菜单子项类
     * @param <T> 额外属性
     */
    public static class Tab<T> {

        //构造方法
        public Tab(Class<? extends Fragment> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        //Fragment对应的Class信息
        public Class<? extends Fragment> clx;
        //额外的字段,用户自定义
        public T extra;
        //当前Tab对应的Fragment
        Fragment fragment;
    }

    /**
     * 事件处理完成后的回调接口
     * @param <T>
     */
    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
