package net.confide.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment自定义基类
 * Created by xwx on 2018/4/30.
 */

public abstract class Fragment extends android.support.v4.app.Fragment{

    //根布局
    protected View mRoot;
    //
    protected Unbinder mRootUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //初始化参数
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            //获取到当前界面文件id
            int layId = getContentLayoutId();
            //通过当前界面文件id创建根布局,但是不在创建时就添加到container中
            View root = inflater.inflate(layId, container, false);
            //初始化控件
            initWidget(root);
            //
            mRoot = root;
        } else {
            //如果Fragment被回收,再重新初始化Fragment的时候,有可能mRoot还未被回收,因此需要手动移除其父控件
            if (mRoot.getParent() != null) {
                ((ViewGroup)(mRoot.getParent())).removeView(mRoot);
            }
        }
        //在return的时候根布局root会自动添加到container中,若root已有父控件会报错
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //在Fragment的界面初始化完成后再初始化数据
        initData();
    }

    //初始化相关参数
    protected void initArgs(Bundle bundle) {

    }

    //获取布局资源文件id,由子类实现
    protected abstract int getContentLayoutId();

    //初始化控件
    protected void initWidget(View root) {
        //注册ButterKnife
        mRootUnbinder = ButterKnife.bind(this, root);
    }

    //初始化数据
    protected void initData() {

    }

    /**
     * 点击返回按键触发
     * @return true:已处理返回逻辑,Activity不用finish
     * @return false:未处理返回逻辑,交由Activity处理
     */
    public boolean onBackPressed() {
        return false;//默认不处理
    }
}
