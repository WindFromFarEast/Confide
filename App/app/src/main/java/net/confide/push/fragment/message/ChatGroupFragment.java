package net.confide.push.fragment.message;

import net.confide.factory.model.db.Group;
import net.confide.factory.presenter.message.ChatContract;
import net.confide.push.R;

/**
 * 群聊界面
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {


    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}
