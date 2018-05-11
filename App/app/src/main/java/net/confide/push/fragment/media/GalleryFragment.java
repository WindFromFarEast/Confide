package net.confide.push.fragment.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import net.confide.common.widget.GalleryView;
import net.confide.push.R;
import net.qiujuer.genius.ui.Ui;

/**
 * 图片选择Fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleryFragment() {
    }

    /**
     * 需要重写的方法
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_gallery, container, false);
        //获取GalleryView实例对象
        mGallery = root.findViewById(R.id.galleryView);
        return root;
    }

    /**
     * 在onStart生命周期中进行GalleryView的初始化
     */
    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    /**
     * GalleryView选中图片的回调
     * @param count 已选中图片的数量
     */
    @Override
    public void onSelectedCountChanged(int count) {
        //只允许选一张
        if (count > 0) {
            //隐藏Fragment
            dismiss();
            if (mListener != null) {
                String[] path = mGallery.getSelectedPath();
                //通知监听者已选择图片
                mListener.onSelectedImage(path[0]);
                mListener = null;
            }
        }
    }

    /**
     * 设置监听选中图片这一行为的监听器,并返回GalleryFragment实例以便于流式调用
     * @param listener
     * @return GalleryFragment
     */
    public GalleryFragment setListener(OnSelectedListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 监听选中了一张图片的监听器
     */
    public interface OnSelectedListener {
        void onSelectedImage(String path);
    }

    /**
     * 自定义BottomSheetDialog类：解决弹出时顶部状态栏变黑的问题
     */
    private static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if (window == null) {
                return;
            }
            //以下是解决状态栏变黑的逻辑
            //获取屏幕高度
            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            //获取顶部StatusBar的高度
            int statusHeight = (int) Ui.dipToPx(getContext().getResources(), 25);
            //Dialog的高度就是屏幕高度减去顶部状态栏的高度
            int dialogHeight = screenHeight - statusHeight;
            //设置window的高度
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }
}
