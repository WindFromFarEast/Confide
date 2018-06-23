package net.web.confide.push.factory;

import net.web.confide.push.bean.db.Group;
import net.web.confide.push.bean.db.GroupMember;
import net.web.confide.push.bean.db.User;

import java.util.Set;

/**
 * 群数据库处理类
 */
public class GroupFactory {

    public static Group findById(String groupId) {
        //TODO
        return null;
    }


    public static Group findById(User user, String receiverId) {
        //TODO 查询一个群,同时user必须为群成员
        return null;
    }

    public static Set<GroupMember> getMembers(Group group) {
        //TODO
        return null;
    }
}
