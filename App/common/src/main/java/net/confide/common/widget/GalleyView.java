package net.confide.common.widget;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.confide.common.R;
import net.confide.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 自定义图片选择器控件——基于RecyclerView
 */
public class GalleyView extends RecyclerView {

    //RecyclerView适配器
    private Adapter mAdapter = new Adapter();
    //LoaderCallback
    private LoaderManager.LoaderCallbacks mLoaderCallBack = new LoaderCallBack();
    //
    private static final int LOADER_ID = 0x0100;
    //已经被选中的图片集合
    private List<Image> mSelectedImages = new LinkedList<>();
    //最多选中图片数量
    private static final int MAX_IMAGE_COUNT = 3;
    //最小图片大小
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;
    //
    private SelectedChangeListener mListener;

    public GalleyView(Context context) {
        super(context);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 控件初始化
     */
    private void init() {
        //设置LayoutManager,一行显示四张图片
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        //设置点击子项监听器
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {

            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //点击RecyclerView子项的回调方法,若允许点击(未达到最大选中数量),改变图片状态,更新界面
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
        //设置适配器
        setAdapter(mAdapter);
    }

    /**
     * 初始化Loader
     * @param loaderManager
     * @return loader的id,方便外界销毁loader
     */
    public int setup(android.support.v4.app.LoaderManager loaderManager, SelectedChangeListener listener) {
        this.mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallBack);
        return LOADER_ID;
    }

    /**
     * 点击RecyclerView子项的具体回调逻辑
     * @param image
     * @return true：需要刷新界面 false: 不需要刷新界面
     */
    private boolean onItemSelectClick(Image image) {
        //是否需要刷新界面
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            image.isSelected = false;
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                str = String.format(str, MAX_IMAGE_COUNT);
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelected = true;
                notifyRefresh = true;
            }
        }
        //通知外面数据改变
        if (notifyRefresh) {
            notifySelectChanged();
        }
        return true;
    }

    /**
     * 得到选中图片的全部地址
     * @return 地址数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 清空选中的图片
     */
    public void clear() {
        for (Image image : mSelectedImages) {
            image.isSelected = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通知图片选中状态改变
     */
    private void notifySelectChanged() {
        SelectedChangeListener listener = mListener;
        //通知监听器选中图片数量变化
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter刷新数据的方法
     * @param images 新的数据
     */
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }

    /**
     * 用于实际数据加载的Loader
     */
    private class LoaderCallBack implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID, // id
                MediaStore.Images.Media.DATA, //图片路径
                MediaStore.Images.Media.DATE_ADDED //图片创建时间
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //创建Loader时回调
            if (id == LOADER_ID) {
                //如果是我们自己的LOADER_ID就进行初始化
                return new CursorLoader(getContext()
                        , MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION
                        , null,
                        null
                        , IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //Loader加载完成时回调
            List<Image> images = new ArrayList<>();
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long date = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            continue;
                        } else {
                            Image image = new Image();
                            image.id = id;
                            image.path = path;
                            image.date = date;
                            images.add(image);
                        }
                    }while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //Loader被销毁或者重置时回调,界面清空
            updateSource(null);
        }
    }

    /**
     * 图片类
     */
    private static class Image {
        //图片id
        int id;
        //图片路径
        String path;
        //图片创建日期
        long date;
        //图片的选中状态
        boolean isSelected;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * RecyclerView Adapter
     * 继承自自定义Adapter
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleyView.ViewHolder(root);
        }
    }

    /**
     * Recycler ViewHolder
     * 继承自自定义ViewHolder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext()).load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
                    .centerCrop()
                    .placeholder(R.color.grey_200)//默认颜色
                    .into(mPic);
            mShade.setVisibility(image.isSelected ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelected);
            mSelected.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外监听器,监听选中图片的数量变化
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }
}
