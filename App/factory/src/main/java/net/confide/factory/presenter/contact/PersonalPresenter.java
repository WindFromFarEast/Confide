package net.confide.factory.presenter.contact;


import net.confide.common.factory.presenter.BasePresenter;
import net.confide.factory.Factory;
import net.confide.factory.data.helper.UserHelper;
import net.confide.factory.model.db.User;
import net.confide.factory.persistence.Account;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 查看个人详细信息功能MVP——Presenter
 */
public class PersonalPresenter extends BasePresenter<PersonalContract.View> implements PersonalContract.Presenter{

    private String id;
    private User user;

    public PersonalPresenter(PersonalContract.View mView) {
        super(mView);
    }

    @Override
    public void start() {
        super.start();
        //个人界面用户数据优先从网络获取
        Factory.runOnAsyn(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if (view != null) {
                    //先从View层获取要查询的用户id
                    String id = getView().getUserId();
                    User user = UserHelper.searchFirstOfNet(id);
                    //通知View显示用户信息
                    onLoaded(user);
                }
            }
        });
    }

    /**
     * 通知View层显示Presenter获取到的用户信息
     * @param user
     */
    private void onLoaded(final User user) {
        this.user = user;
        //是否是我自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        //是否已经关注
        final boolean isFollow = isSelf || user.isFollow();
        //是否允许发消息
        final boolean allowSayHello = isFollow && !isSelf;
        //切换到UI线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                final PersonalContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.onLoadDone(user);
                view.setFollowStatus(isFollow);
                view.allowSayHello(allowSayHello);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
