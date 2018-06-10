package net.confide.push;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import net.confide.common.app.Activity;
import net.confide.factory.persistence.Account;
import net.confide.push.activities.AccountActivity;
import net.confide.push.activities.MainActivity;
import net.confide.push.fragment.assist.PermissionFragment;
import net.qiujuer.genius.ui.compat.UiCompat;
import com.igexin.sdk.PushManager;

public class LaunchActivity extends Activity {

    //背景Drawable
    private ColorDrawable mBgDrawable;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //获取根布局
        View root = findViewById(R.id.activity_launch);
        //获取ColorDrawable
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        ColorDrawable drawable = new ColorDrawable(color);
        //将colorDrawable设置到根布局背景
        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();
        //注册IntentService
//        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
        //渐变动画到50%后,等待pushId的获取
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        });
    }

    /**
     * 等待个推框架对我们的pushId设置好值
     */
    private void waitPushReceiverId() {
        if (Account.isLogin()) {
            //已经处于登录状态,判断是否绑定
            if (Account.isBind()) {
                //已经处于绑定状态
                skip();
                return;
            }
        } else {
            //未登录
            String pushId = Account.getPushId();
            if (!TextUtils.isEmpty(Account.getPushId())) {
                //获取到了pushId值，跳转到主界面
                skip();
                return;
            }
        }
        //否则循环等待pushId
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        }, 500);
    }

    /**
     * 跳转到主界面,将背景渐变的剩下50%完成
     */
    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });
    }

    /**
     * 实际上进行跳转到主界面的方法
     */
    private void reallySkip() {
        if (PermissionFragment.haveAll(this, getSupportFragmentManager())) {
            //判断要跳转到主页还是登录界面
            if (Account.isLogin()) {
                MainActivity.show(this);
            } else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    /**
     * 为背景设置渐变动画
     * @param endProcess 动画结束进度
     * @param endCallback 动画结束回调
     */
    private void startAnim(float endProcess, final Runnable endCallback) {
        //渐变结束时的颜色
        int finalColor = UiCompat.getColor(getResources(), R.color.white);
        //计算当前渐变进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProcess, mBgDrawable.getColor(), finalColor);
        //属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(1500);
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //渐变动画结束时触发,跳转到主界面
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
}
