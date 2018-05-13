package net.confide.push.fragment.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.confide.common.app.Application;
import net.confide.push.R;
import net.confide.push.fragment.media.GalleryFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 权限申请弹出框
 */
public class PermissionFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks{

    //权限请求回调的标识码
    private static final int RC = 0x0100;

    public PermissionFragment() {
        // Required empty public constructor
    }

    /**
     * BottomSheetDialogFragment需要重写的方法
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_permission, container, false);
        root.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPerm();
            }
        });
        return root;
    }

    /**
     * 刷新布局中图片状态
     * @param root 布局
     */
    private void refreshState(View root) {
        if (root == null)
            return;
        root.findViewById(R.id.im_state_permission_network).setVisibility(haveNetWork(getContext())
                ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_read).setVisibility(haveReadPerm(getContext())
                ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_write).setVisibility(haveWritePerm(getContext())
                ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_audio).setVisibility(haveAudioPerm(getContext())
                ? View.VISIBLE : View.GONE);
    }

    /**
     * 判断是否有网络权限
     * @param context 上下文
     * @return 是否有权限
     */
    private static boolean haveNetWork(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 判断是否有读取外部存储权限
     * @param context 上下文
     * @return 是否有权限
     */
    private static boolean haveReadPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 判断是否有写入外部存储权限
     * @param context 上下文
     * @return 是否有权限
     */
    private static boolean haveWritePerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 判断是否有权限
     * @param context 上下文
     * @return 是否有权限
     */
    private static boolean haveAudioPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 私有条件的显示当前Fragment的方法
     * @param manager FragmentManager
     */
    private static void show(FragmentManager manager) {
        //调用BottomSheetDialogFragment的show方法
        new PermissionFragment()
                .show(manager, PermissionFragment.class.getName());
    }

    /**
     * 检查是否已经授权了所有权限
     */
    public static boolean haveAll(Context context, FragmentManager manager) {
        boolean haveAll = haveNetWork(context) && haveReadPerm(context)
                && haveWritePerm(context) && haveAudioPerm(context);
        //没有获取所有权限则弹出权限申请框
        if (!haveAll) {
            show(manager);
        }
        return haveAll;
    }

    /**
     * 申请所有权限
     */
    @AfterPermissionGranted(RC)
    private void requestPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Application.showToast(R.string.label_permission_ok);
            refreshState(getView());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions),
                    RC, perms);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        refreshState(getView());
    }

    /**
     * 部分权限被拒绝时回调
     * @param perms 被拒绝的权限
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        refreshState(getView());
    }

    /**
     * 权限申请回调
     * 在此将权限申请状态交给EasyPermission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
