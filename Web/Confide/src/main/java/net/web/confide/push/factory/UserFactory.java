package net.web.confide.push.factory;

import com.google.common.base.Strings;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.utils.Hib;
import net.web.confide.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

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
}
