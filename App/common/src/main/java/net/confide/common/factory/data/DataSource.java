package net.confide.common.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口定义
 */
public interface DataSource {

    /**
     * 成功与失败的回调接口
     * @param <T>
     */
    interface Callback<T> extends SucceedCallback<T>, FailedCallback {

    }

    /**
     * 成功的回调接口
     */
    interface SucceedCallback<T> {
        //数据成功加载
        void onDataLoaded(T t);
    }

    /**
     * 失败的回调接口
     */
    interface FailedCallback {
        //数据加载失败
        void onDataNotAvailable(@StringRes int strRes);
    }
}
