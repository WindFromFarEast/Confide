package net.confide.factory.presenter.message;

import net.confide.factory.data.helper.UserHelper;
import net.confide.factory.data.message.MessageRepository;
import net.confide.factory.model.db.Message;
import net.confide.factory.model.db.User;

/**
 *
 */
public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView> implements ChatContract.Presenter{

    public ChatUserPresenter(ChatContract.UserView mView, String receiverId) {
        //数据源,View,接收者,接收者的类型
        super(new MessageRepository(receiverId), mView, receiverId, Message.RECEIVER_TYPE_NONE);

    }

    @Override
    public void start() {
        super.start();
        //从本地获取对面用户的信息
        User receiver = UserHelper.findFromLocal(mReceiverId);
        //初始化对面用户信息
        getView().onInit(receiver);
    }
}
