package net.confide.factory.data.user;

import android.text.TextUtils;

import net.confide.factory.data.helper.DbHelper;
import net.confide.factory.model.card.UserCard;
import net.confide.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 */
public class UserDispatcher implements UserCenter {

    //当前类以单例模式提供
    private static volatile UserCenter instance;
    //单线程的线程池,用于对卡片消息的处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * 单例模式提供方法
     */
    public static UserCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null) {
                    instance = new UserDispatcher();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param cards
     */
    @Override
    public void dispatch(UserCard... cards) {
        if (cards == null || cards.length == 0) {
            return;
        }
        //放入单线程池中进行处理
        executor.execute(new UserCardHandler(cards));
    }

    /**
     * UserCard处理线程
     */
    private class UserCardHandler implements Runnable {

        private final UserCard[] cards;

        UserCardHandler(UserCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<User> users = new ArrayList<>();
            for (UserCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getId())) {
                    continue;
                }
                users.add(card.build());
            }
            //进行数据库存储并分发通知
            DbHelper.save(User.class, users.toArray(new User[0]));
        }
    }
}
