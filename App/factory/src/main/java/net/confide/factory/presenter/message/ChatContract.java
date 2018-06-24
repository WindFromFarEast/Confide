package net.confide.factory.presenter.message;

import net.confide.common.factory.presenter.BaseContract;
import net.confide.factory.model.db.Group;
import net.confide.factory.model.db.Message;
import net.confide.factory.model.db.User;

/**
 * 聊天功能MVP契约
 */
public interface ChatContract {

    interface Presenter extends BaseContract.Presenter {
        //发送文本
        void pushText(String content);
        //发送语音
        void pushAudio(String path);
        //发送图片
        void pushImage(String[] paths);
        //重新发送
        boolean rePush(Message message);
    }

    /**
     * View基类
     * @param <InitModel>
     */
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {
        //初始化
        void onInit(InitModel model);
    }

    /**
     * 和人聊天的界面
     */
    interface UserView extends View<User> {

    }

    /**
     * 和群聊天的界面
     */
    interface GroupView extends View<Group> {

    }
}
