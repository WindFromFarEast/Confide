package net.confide.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.confide.common.app.Activity;
import net.confide.common.app.PresenterToolbarActivity;
import net.confide.common.app.ToolbarActivity;
import net.confide.common.widget.PortraitView;
import net.confide.factory.model.db.User;
import net.confide.factory.presenter.contact.PersonalContract;
import net.confide.factory.presenter.contact.PersonalPresenter;
import net.confide.push.R;
import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户详细信息显示界面
 */
public class PersonalActivity extends PresenterToolbarActivity<PersonalContract.Presenter> implements PersonalContract.View {

    private static final String BOUND_KEY_ID = "BOUND_KEY_ID";
    private String userId;

    @BindView(R.id.im_header)
    ImageView mHeader;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.txt_name)
    TextView mName;
    @BindView(R.id.txt_desc)
    TextView mDesc;
    @BindView(R.id.txt_follows)
    TextView mFollows;
    @BindView(R.id.txt_following)
    TextView mFollowing;
    @BindView(R.id.btn_say_hello)
    Button mSayHello;
    //菜单栏中的关注按钮
    private MenuItem mFollowItem;
    //对当前用户是否已经关注
    private boolean mIsFollowUser = false;

    /**
     *
     * @param context
     * @param userId 要显示信息的用户id
     */
    public static void show(Context context, String userId) {
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra(BOUND_KEY_ID, userId);
        context.startActivity(intent);
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        //初始化当前要显示的用户id
        userId = bundle.getString(BOUND_KEY_ID);
        return !TextUtils.isEmpty(userId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.personal, menu);
        mFollowItem = menu.findItem(R.id.action_follow);
        changeFollowItemStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_follow) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击发起聊天按钮的回调
     */
    @OnClick(R.id.btn_say_hello)
    void onSayHelloClick() {
        //通知Presenter层获取当前用户信息
        User user = mPresenter.getUserPersonal();
        if (user == null) {
            return;
        }
        //发起聊天
        MessageActivity.show(this, user);
    }

    /**
     * 更改菜单子项——关注按钮的状态
     */
    private void changeFollowItemStatus() {
        if (mFollowItem == null) {
            return;
        }
        //根据关注状态设置关注按钮的颜色
        Drawable drawable = mIsFollowUser
                ? getResources().getDrawable(R.drawable.ic_favorite)
                : getResources().getDrawable(R.drawable.ic_favorite_border);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Resource.Color.WHITE);
        mFollowItem.setIcon(drawable);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * Presenter加载用户数据成功后回调该方法
     * @param user
     */
    @Override
    public void onLoadDone(User user) {
        if (user == null)
            return;
        mPortrait.setup(Glide.with(this), user);
        mName.setText(user.getName());
        mDesc.setText(user.getDesc());
        mFollows.setText(String.format(getString(R.string.label_follows), user.getFollows()));
        mFollowing.setText(String.format(getString(R.string.label_following), user.getFollowing()));
        hideLoading();
    }

    /**
     * 是否允许发消息给用户
     * @param isAllow
     */
    @Override
    public void allowSayHello(boolean isAllow) {
        mSayHello.setVisibility(isAllow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setFollowStatus(boolean isFollow) {
        mIsFollowUser = isFollow;
        changeFollowItemStatus();
    }

    @Override
    protected PersonalContract.Presenter initPresenter() {
        return new PersonalPresenter(this);
    }
}
