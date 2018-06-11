package net.confide.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.confide.common.R;

/**
 * 带有Toolbar的Activity
 */
public abstract class ToolbarActivity extends Activity {

    protected Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    /**
     * 初始化Toolbar
     * @param toolbar
     */
    public void initToolbar(Toolbar toolbar) {
        this.mToolbar = toolbar;
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        initTitleNeedBack();
    }

    /**
     * 初始化ActionBar的一些效果,例如点击返回的效果
     */
    protected void initTitleNeedBack() {
        //设置左上角的返回按钮为实际的返回效果
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
