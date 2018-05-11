package net.confide.push.fragment.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.confide.common.app.Application;
import net.confide.common.widget.PortraitView;
import net.confide.push.R;
import net.confide.push.fragment.media.GalleryFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 更新用户信息的界面
 */
public class UpdateInfoFragment extends net.confide.common.app.Fragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    public UpdateInfoFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    /**
     * 点击头像后的回调：选择图片
     */
    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        //开始剪切图片
                        UCrop.Options options = new UCrop.Options();
                        //设置图片处理后的格式
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //设置压缩后图片的质量
                        options.setCompressionQuality(96);
                        //得到剪切后图片的缓存地址
                        File dPath = Application.getPortraitTmpFile();
                        //正式开始剪切
                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1) //比例为1比1
                                .withMaxResultSize(520, 520) //剪切后的最大尺寸
                                .withOptions(options) //相关参数
                                .start(getActivity()); //开始剪切
                    }
                }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    /**
     * 图片剪切完成后的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * 将剪切的图片加载到头像上
     */
    private void loadPortrait(Uri uri) {
        Glide.with(this).load(uri).asBitmap().centerCrop().into(mPortrait);
    }
}
