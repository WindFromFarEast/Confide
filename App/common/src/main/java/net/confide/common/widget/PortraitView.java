package net.confide.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;

import net.confide.common.R;
import net.confide.common.factory.model.Author;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 圆形头像控件的封装类
 */
public class PortraitView extends CircleImageView{

    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager manager, Author author) {
        if (author == null) {
            return;
        }
        //用户不为空则显示用户头像
        setup(manager, author.getPortrait());
    }

    /**
     * 使用Glide框架将指定路径的图片加载进来
     * @param manager
     * @param url
     */
    public void setup(RequestManager manager, String url) {
        setup(manager, R.drawable.default_portrait, url);
    }

    /**
     * 用Glide框架将指定路径的图片加载进来
     * @param manager
     * @param resourceId
     * @param url
     */
    public void setup(RequestManager manager, int resourceId, String url) {
        if (url == null) {
            url = "";
        }
        manager.load(url)
                .placeholder(resourceId)
                .centerCrop()
                .dontAnimate() //不能使用渐变动画
                .into(this);
    }
}
