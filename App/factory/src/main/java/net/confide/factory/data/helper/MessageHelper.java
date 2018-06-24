package net.confide.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.confide.factory.Factory;
import net.confide.factory.model.api.RspModel;
import net.confide.factory.model.api.message.MsgCreateModel;
import net.confide.factory.model.card.MessageCard;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.db.Message;
import net.confide.factory.model.db.Message_Table;
import net.confide.factory.net.NetWork;
import net.confide.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息工具类
 */
public class MessageHelper {

    /**
     * 从本地获取信息
     * @param id
     * @return
     */
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 网络消息发送
     * @param model
     */
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsyn(new Runnable() {
            @Override
            public void run() {
                //如果是已经发送过的消息,不能重新发送
                Message message = findFromLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED) {
                    return;
                }
                //如果是文件类型的,需要先上传再发送

                //刷新界面
                final MessageCard card = model.buildCard();
                Factory.getMessageCenter().dispatch(card);
                //正常消息直接发送
                RemoteService service = NetWork.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel != null && rspModel.success()) {
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard != null) {
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        } else {
                            Factory.decodeRspCode(rspModel, null);
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
        return;
    }
}
