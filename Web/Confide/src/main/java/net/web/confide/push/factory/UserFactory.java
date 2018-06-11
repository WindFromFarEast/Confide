package net.web.confide.push.factory;

import com.google.common.base.Strings;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.bean.db.UserFollow;
import net.web.confide.push.utils.Hib;
import net.web.confide.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 对用户进行一系列操作的工具类
 * 登录、注册、绑定pushId、搜索、查找、关注等
 */
public class UserFactory {

    /**
     * 通过token查询用户信息
     * @param token
     * @return
     */
    public static User findByToken(String token) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User where token=:token")
                    .setParameter("token", token)
                    .uniqueResult();
            return user;
        });
    }

    /**
     * 通过手机号查询用户是否已经注册过
     * @param phone
     * @return
     */
    public static User findByPhone(String phone) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User where phone=:inPhone")
                    .setParameter("inPhone", phone)
                    .uniqueResult();
            return user;
        });
    }

    /**
     * 使用账户和密码进行登录
     * @param account
     * @param password
     * @return
     */
    public static User login(String account, String password) {
        final String accountStr = account.trim();
        //对密码进行加密
        final String encodePassword = encodePassword(password);
        //查询该用户是否存在
        User user = Hib.query((Session session) -> (User) session.createQuery("from User where phone=:phone and password=:password")
                .setParameter("phone", accountStr)
                .setParameter("password", encodePassword)
                .uniqueResult());
        if (user != null) {
            //用户存在,进行登录操作
            user = login(user);
        }
        return user;
    }

    /**
     * 通过name查询用户是否已经注册过
     * @param name
     * @return
     */
    public static User findByName(String name) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User where name=:name")
                    .setParameter("name", name)
                    .uniqueResult();
            return user;
        });
    }

    /**
     * 通过id查询用户
     * @return
     */
    public static User findById(String id) {
        return Hib.query(session -> session.get(User.class, id));
    }

    /**
     * 更新User信息到数据库
     * @param user
     * @return
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 给当前账户绑定设备pushId
     * @param user
     * @param pushId
     * @return
     */
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId)) {
            return null;
        }
        //首先查询是否有其他设备绑定了该pushId
        //取消绑定,避免推送混乱
        Hib.queryOnly(session -> {
            @SuppressWarnings("unchecked")
            List<User> userList = session.createQuery("from User where lower(pushId)=:pushId and id!=:userId")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId())
                    .list();
            //将pushId更新为null
            for (User u : userList) {
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });

        if (pushId.equalsIgnoreCase(user.getPushId())) {
            return user;
        } else {
            //如果当前用户之前设备pushId和需要绑定的不同
            //单点登录,让之前设备退出账户,给之前设备推送一条退出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                //TODO 推送退出消息
            }
            user.setPushId(pushId);
            //更新到数据库
            return update(user);
        }
    }

    /**
     * 用户注册
     * 将用户写入数据库,返回User信息
     *
     * @param account
     * @param password
     * @param name
     * @return
     */
    public static User register(String account, String password, String name) {
        //去除首尾空格
        account = account.trim();
        //对密码进行加密
        password = encodePassword(password);
        //创建用户
        User user = createUser(account, password, name);
        if (user != null) {
            //登录
            user = login(user);
        }
        return user;
    }

    /**
     * 用户注册时中的创建用户并存储到数据库的逻辑
     * @param account
     * @param password
     * @param name
     * @return
     */
    private static User createUser(String account, String password, String name) {
        //创建User
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);
        //数据库存储
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }

    /**
     * 用户登录
     * 本质是对token进行操作
     */
    private static User login(User user) {
        //使用随机UUID来作为token
        String newToken = UUID.randomUUID().toString();
        //对UUID进行Base64加密
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        //更新到数据库
        return update(user);
    }

    /**
     * 对密码进行加密
     * @param password
     * @return
     */
    private static String encodePassword(String password) {
        //去空格
        password = password.trim();
        //MD5加密
        password = TextUtil.getMD5(password);
        //再进行一次对称的Base64加密
        return TextUtil.encodeBase64(password);
    }

    /**
     * 获取User的关注人列表
     * @param self
     * @return
     */
    public static List<User> contacts(User self) {
        return Hib.query(session -> {
            //重新加载用户信息到self中,和当前session绑定
            session.load(self, self.getId());
            //获取关注的人
            Set<UserFollow> flows = self.getFollowing();
            return flows.stream().map(UserFollow::getTarget).collect(Collectors.toList());
        });
    }

    /**
     * 关注其他用户的接口
     * @param origin
     * @param target 被关注的人
     * @param alias 备注名
     * @return 被关注的User
     */
    public static User follow(final User origin, final User target, final String alias) {
        //首先
        UserFollow follow = getUserFollow(origin, target);
        if (follow != null) {
            //已关注,直接返回被关注者
            return follow.getTarget();
        }
        //未关注,再进行关注操作
        return Hib.query(session -> {
            //想操作懒加载的数据,需要重新加载
            session.load(origin, origin.getId());
            session.load(target, target.getId());
            //关注他人的时候,同时他人也关注我,需要添加两条UserFollow
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            originFollow.setAlias(alias);
            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);
            //保存到数据库
            session.save(originFollow);
            session.save(targetFollow);
            //
            return target;
        });
    }

    /**
     * 查询两个人是否已经有关注关系
     * @param origin
     * @param target 被关注人
     * @return 返回中间类UserFollow
     */
    public static UserFollow getUserFollow(final User origin, final User target) {
        return Hib.query(session -> (UserFollow) session.createQuery("from UserFollow where originId = :originId and targetId = :targetId")
                .setParameter("originId", origin.getId())
                .setParameter("targetId", target.getId())
                .setMaxResults(1)
                .uniqueResult());
    }

    /**
     * 搜索联系人
     * @param name 查询用户的name,允许为空
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name)) {
            name = "";//保证name不为null
        }
        final String searchName = "%" + name + "%";//模糊匹配
        return Hib.query(session -> (List<User>) session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null")
        .setParameter("name", searchName)
        .setMaxResults(20)
        .list());
    }
}
