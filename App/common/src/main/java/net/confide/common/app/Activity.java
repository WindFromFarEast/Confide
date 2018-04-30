package net.confide.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Activity的自定义基类
 * Created by xwx on 2018/4/30.
 */

public abstract class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面未初始化之前调用,初始化窗口
        initWindows();
        if (initArgs(getIntent().getExtras())) {
            //
            getContentLayoutId();
            initWidget();
            initData();
        } else {
            finish();
        }
    }

    protected void initWindows() {

    }

    //初始化相关参数,判断从其他Activity传过来的参数是否正确,如果参数正确返回true,参数错误返回false,默认返回true
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    //获取当前界面资源文件id,由子类实现
    protected abstract int getContentLayoutId();

    //初始化控件
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    //初始化数据
    protected void initData() {

    }

    //按下界面导航
    @Override
    public boolean onSupportNavigateUp() {
        //finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * 按下返回按键触发
     */
    @Override
    public void onBackPressed() {
        //获取当前Activity下的所有Fragment
        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        //判断当前Activity下是否有Fragment
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                //首先判断该Fragment是否继承于自定义Fragment
                if (fragment instanceof net.confide.common.app.Fragment) {
                    //该Fragment继承于自定义Fragment
                    //判断Fragment是否对返回按键事件进行了拦截
                    if (((net.confide.common.app.Fragment) fragment).onBackPressed()) {
                        //Fragment已经拦截了返回按键事件,Activity不用进行处理
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }
}
