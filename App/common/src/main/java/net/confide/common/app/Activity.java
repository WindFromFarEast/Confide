package net.confide.common.app;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Activity的自定义基类
 * Created by xwx on 2018/4/30.
 */
public abstract class Activity extends AppCompatActivity {

    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面未初始化之前调用,初始化窗口
        initWindows();
        //判断从其他Activity传过来的参数是否正确
        if (initArgs(getIntent().getExtras())) {
            //获取布局文件资源id
            int layoutId = getContentLayoutId();
            //将布局设置到Activity界面中
            setContentView(layoutId);
            //initPermission();
            initWidget();
            initData();
        } else {
            //参数错误直接finish当前Activity
            finish();
        }
    }

    protected void initWindows() {

    }

    //初始化相关参数,判断从其他Activity传过来的参数是否正确,如果参数正确返回true,参数错误返回false,默认返回true
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    //获取布局资源文件id,由子类实现
    protected abstract int getContentLayoutId();

    //初始化控件
    protected void initWidget() {
        //注册ButterKnife
        ButterKnife.bind(this);
        //锁定屏幕方向为竖直
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
     * 按下返回键触发
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

//    /**
//     * 初始化权限
//     */
//    protected void initPermission() {
//        //请求读写内存的权限
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this
//                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
//                    , PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_WRITE_EXTERNAL_REQUEST_CODE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //允许权限后进行的操作
//                } else {
//                    Toast.makeText(this, "拒绝权限将导致应用异常", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//        }
//    }
}
