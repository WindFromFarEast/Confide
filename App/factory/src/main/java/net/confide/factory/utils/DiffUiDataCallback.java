package net.confide.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 *
 */
public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {

    private List<T> mOldList, mNewList;

    /**
     * 构造方法,在其中进行新、旧数据的初始化
     * @param mOldList
     * @param mNewList
     */
    public DiffUiDataCallback(List<T> mOldList, List<T> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        //旧数据源大小
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        //新数据源大小
        return mNewList.size();
    }

    /**
     * 判断两个对象是否是相同的,这个相同指的不是==
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldList.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);
        return beanNew.isSame(beanOld);
    }

    /**
     * 进一步判断两个对象内容是否相同
     * 例如同一个用户,被修改了desc后就内容就不再相同,但还是同一个对象
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldList.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);
        return beanNew.isUiContentSame(beanOld);
    }

    /**
     * 进行比较的数据类型接口,实现该接口的类的对象具有和同类型的对象比较的功能
     * @param <T>
     */
    public interface UiDataDiffer<T> {
        //传递一个旧数据,判断和当前标示的是同一个数据
        boolean isSame(T old);
        //传递一个旧数据,判断和旧数据相比，内容是否发生了改变
        boolean isUiContentSame(T old);
    }
}
