package net.confide.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import net.confide.factory.data.helper.MessageHelper;
import net.confide.factory.data.message.MessageDataSource;
import net.confide.factory.model.api.message.MsgCreateModel;
import net.confide.factory.model.card.MessageCard;
import net.confide.factory.model.db.Message;
import net.confide.factory.persistence.Account;
import net.confide.factory.presenter.BaseSourcePresenter;
import net.confide.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 聊天Presenter的基类
 */
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter{

    //接收者id
    protected String mReceiverId;
    //接收者类型
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View mView, String receiverId, int receiverType) {
        super(source, mView);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }


    @Override
    public void pushText(String content) {
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();
        //进行网络发送消息
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        //TODO
    }

    @Override
    public void pushImage(String[] paths) {
        //TODO
    }

    /**
     * 重新发送消息
     * @param message
     * @return
     */
    @Override
    public boolean rePush(Message message) {
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {
            message.setStatus(Message.STATUS_CREATED);
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if (view == null) {
            return;
        }
        //获取旧数据
        List<Message> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //界面刷新
        refreshData(result, messages);
    }
}
