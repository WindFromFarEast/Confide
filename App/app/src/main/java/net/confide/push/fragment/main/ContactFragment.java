package net.confide.push.fragment.main;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.confide.common.app.Fragment;
import net.confide.common.app.PresenterFragment;
import net.confide.common.widget.EmptyView;
import net.confide.common.widget.PortraitView;
import net.confide.common.widget.recycler.RecyclerAdapter;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.db.User;
import net.confide.factory.presenter.contact.ContactContract;
import net.confide.factory.presenter.contact.ContactPresenter;
import net.confide.push.R;
import net.confide.push.activities.MessageActivity;
import net.confide.push.activities.PersonalActivity;
import net.confide.push.fragment.search.SearchUserFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 联系人Fragment
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter> implements ContactContract.View {

    //占位控件
    @BindView(R.id.empty)
    EmptyView mEmptyView;
    //联系人列表
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //初始化RecyclerView
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {

            @Override
            protected int getItemViewType(int position, User user) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });
        //为联系人列表设置点击事件监听
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                //跳转和指定用户的到聊天界面
                MessageActivity.show(getContext(), user);
            }
        });
        //将RecyclerView和占位控件EmptyView绑定
        mEmptyView.bind(mRecycler);
        //为当前Fragment设置占位控件
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        //进行一次数据加载
        mPresenter.start();
    }

    /**
     * 初始化Presenter
     * @return
     */
    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    /**
     * RecyclerView列表数据源发生改变进行的回调
     */
    @Override
    public void onAdapterDataChange() {
        //数据源的数量大于0就不再显示占位控件
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    public void replace(List<User> tResult) {
        mAdapter.replace(tResult);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            //加载用户头像
            mPortraitView.setup(Glide.with(ContactFragment.this), user);
            //...
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        /**
         * 点击头像
         */
        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            //打开个人信息界面
            PersonalActivity.show(getContext(), mData.getId());
        }
    }

}
