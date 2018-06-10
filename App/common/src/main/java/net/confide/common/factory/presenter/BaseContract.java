package net.confide.common.factory.presenter;

import android.support.annotation.StringRes;

/**
 * MVP模式中公共的契约
 */
public interface BaseContract {

    interface View<T extends Presenter> {
        //显示一个字符串错误
        void showError(@StringRes int str);
        //显示进度条
        void showLoading();
        //设置Presenter
        void setPresenter(T presenter);
    }

    interface Presenter {
        //公用的开始方法
        void start();
        //公用的销毁方法
        void destroy();
    }
}
