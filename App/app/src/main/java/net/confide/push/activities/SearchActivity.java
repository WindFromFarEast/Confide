package net.confide.push.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.confide.common.app.Activity;
import net.confide.common.app.Fragment;
import net.confide.common.app.ToolbarActivity;
import net.confide.push.R;
import net.confide.push.fragment.search.SearchGroupFragment;
import net.confide.push.fragment.search.SearchUserFragment;

/**
 * 搜索界面
 */
public class SearchActivity extends ToolbarActivity {

    //外界调用搜索界面的show接口携带的额外参数的key
    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    //具体需要显示的Fragment类型的参考值
    private int type;
    //搜索用户
    public static final int TYPE_USER = 1;
    //搜索群
    public static final int TYPE_GROUP = 2;
    //搜索界面下的继承自搜索接口SearchFragment的Fragment
    private SearchFragment mSearchFragment;

    /**
     * 跳转到搜索界面的接口
     * @param context
     * @param type 携带的额外数据,此处是指当前搜索界面需要显示的是用户搜索Fragment还是群搜索Fragment
     */
    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        //获取外界传过来的需要显示的Fragment的类型
        type = bundle.getInt(EXTRA_TYPE);
        //判断参数是否正确
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //显示对应的Fragment（用户搜索、群搜索）
        Fragment fragment;//要显示的Fragment
        if (type == TYPE_USER) {
            SearchUserFragment searchUserFragment = new SearchUserFragment();
            fragment = searchUserFragment;
            mSearchFragment = searchUserFragment;
        } else {
            SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
            fragment = searchGroupFragment;
            mSearchFragment = searchGroupFragment;
        }
        //显示Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.lay_container, fragment).commit();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //初始化菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        //找到搜索子项
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //获取搜索子项中的ActionView
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            // 拿到一个搜索管理器
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            // 添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // 当点击了提交按钮的时候
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // 当文字改变的时候，不会及时搜索，只在为null的情况下进行搜索
                    if (TextUtils.isEmpty(s)) {
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 真实的搜索接口
     * @param query 搜索内容
     */
    private void search(String query) {
        if (mSearchFragment != null) {
            mSearchFragment.search(query);
        }
    }

    /**
     * 搜索的Fragment必须实现这个接口
     */
    public interface SearchFragment {
        void search(String content);
    }
}
