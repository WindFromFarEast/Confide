package net.confide.push.fragment.search;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.confide.common.app.PresenterFragment;
import net.confide.common.widget.EmptyView;
import net.confide.common.widget.PortraitView;
import net.confide.common.widget.recycler.RecyclerAdapter;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.presenter.contact.FollowContract;
import net.confide.factory.presenter.contact.FollowPresenter;
import net.confide.factory.presenter.search.SearchContract;
import net.confide.factory.presenter.search.SearchUserPresenter;
import net.confide.push.R;
import net.confide.push.activities.SearchActivity;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索界面中的用户搜索Fragment
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter> implements SearchActivity.SearchFragment, SearchContract.UserView {

    //占位控件
    @BindView(R.id.empty)
    EmptyView mEmptyView;
    //列表
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() { }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //初始化RecyclerView
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {

            //返回cell的布局id
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });
        //将RecyclerView和占位控件EmptyView绑定
        mEmptyView.bind(mRecycler);
        //为当前Fragment设置占位控件
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        //发起首次搜索
        search("");
    }

    /**
     * 注意这个不是Presenter中的search,而是SearchFragment接口中定义的search
     * 在这里需要调用Presenter中的Fragment
     * @param content
     */
    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    /**
     * 搜索成功时Presenter回调的方法
     * @param userCards
     */
    @Override
    public void onSearchDone(List<UserCard> userCards) {
        mAdapter.replace(userCards);
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        //初始化当前View的Presenter
        return new SearchUserPresenter(this);
    }

    /**
     * RecyclerView的ViewHolder,继承自我们自己写的ViewHolder
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard> implements FollowContract.View {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_follow)
        ImageView mFollow;
        //关注功能的Presenter
        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            //将这个View和Presenter绑定
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            Glide.with(SearchUserFragment.this)
                    .load(userCard.getPortrait())
                    .centerCrop()
                    .into(mPortraitView);

            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        /**
         * 发起关注
         */
        @OnClick(R.id.im_follow)
        void onFollowClick() {
            mPresenter.follow(mData.getId());
        }

        @Override
        public void showError(int str) {
            //关注失败,停止动画,显示一个圆圈
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                LoadingDrawable drawable = ((LoadingDrawable) mFollow.getDrawable());
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            //初始化一个圆形动画Drawable
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);
            int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            drawable.setForegroundColor(color);
            //启动点击动画
            mFollow.setImageDrawable(drawable);
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            //关注成功的情况下，关闭点击动画
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
                updateData(userCard);
            }
        }
    }
}
