package net.web.confide.push.factory;

import net.web.confide.push.bean.db.User;
import net.web.confide.push.utils.Hib;
import net.web.confide.push.utils.TextUtil;
import org.hibernate.Session;

public class UserFactory {

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
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);
        //将用户保存到数据库中
        Session session = Hib.session();
        session.beginTransaction();
        try {
            session.save(user);
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            //失败情况下回滚
            session.getTransaction().rollback();
            return null;
        }
    }

    //对密码进行加密
    private static String encodePassword(String password) {
        //去空格
        password = password.trim();
        //MD5加密
        password = TextUtil.getMD5(password);
        //再进行一次对称的Base64加密
        return TextUtil.encodeBase64(password);
    }
}
