package net.confide.push.fragment.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.confide.common.app.Application;
import net.confide.common.app.PresenterFragment;
import net.confide.common.widget.PortraitView;
import net.confide.factory.Factory;
import net.confide.factory.net.UploadHelper;
import net.confide.factory.presenter.user.UpdateInfoContract;
import net.confide.factory.presenter.user.UpdateInfoPresenter;
import net.confide.push.R;
import net.confide.push.activities.MainActivity;
import net.confide.push.fragment.media.GalleryFragment;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 更新用户信息的界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter> implements UpdateInfoContract.View {

    @BindView(R.id.im_sex)
    ImageView mSex;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    //头像本地路径
    private String mPortraitPath;
    //
    private boolean isMan = true;

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
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    /**
     * 将剪切的图片加载到头像上,并上传至阿里云OSS
     */
    private void loadPortrait(Uri uri) {
        //得到头像本地地址
        mPortraitPath = uri.getPath();
        Glide.with(this).load(uri).asBitmap().centerCrop().into(mPortrait);
        //获取图片本地地址
        final String localPath = uri.getPath();
    }

    /**
     * 点击性别图标后触发,切换性别
     */
    @OnClick(R.id.im_sex)
    void onSexClick() {
        isMan = !isMan;
        Drawable drawable = getResources().getDrawable(isMan?
        R.drawable.ic_sex_man : R.drawable.ic_sex_woman);
        //修改性别图片
        mSex.setImageDrawable(drawable);
        //设置性别图片的背景层级
        mSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    /**
     * 点击按钮后回调
     */
    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String desc = mDesc.getText().toString();
        //调用Presenter进行
        mPresenter.update(mPortraitPath, desc, isMan);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        //当提示需要显示错误时触发
        mLoading.stop();//停止Loading
        //使能输入控件
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        //注册按钮可以再次点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //正在注册时回调的方法
        //
        mLoading.start();
        //不使能输入控件
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        //不允许再次点击注册按钮
        mSubmit.setEnabled(false);
    }

    @Override
    public void updateSuccess() {
        //更新成功,跳转到MainActivity
        MainActivity.show(getContext());
        //注销当前界面
        getActivity().finish();
    }

    /**
     * 初始化Presenter
     * @return
     */
    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }
}
