package net.web.confide.push.service;

import com.google.common.base.Strings;
import net.web.confide.push.bean.api.base.PushModel;
import net.web.confide.push.bean.api.base.ResponseModel;
import net.web.confide.push.bean.api.user.UpdateInfoModel;
import net.web.confide.push.bean.card.UserCard;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.bean.db.UserFollow;
import net.web.confide.push.factory.UserFactory;
import net.web.confide.push.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息处理Service
 */
//该类访问路径为:127.0.0.1/api/user
@Path("/user")
public class UserService extends BaseService {

    /**
     * 用户信息修改接口
     * @param model
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        //校验
        if (!UpdateInfoModel.check(model)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //更新用户信息
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        UserCard card = new UserCard(self, true);
        return ResponseModel.buildOk(card);
    }

    /**
     * 拉取联系人
     * @return
     */
    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        //获取用户关注人
        List<User> users = UserFactory.contacts(self);
        List<UserCard> userCards = users.stream().
                map(user -> new UserCard(user, true))
                .collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }

    /**
     * 关注人
     * @param followId
     * @return
     */
    @PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();
        //自己不能关注自己
        if (self.getId().equals(followId) || Strings.isNullOrEmpty(followId)) {
            //直接返回参数错误
            return ResponseModel.buildParameterError();
        }
        //根据id获取关注人
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            //关注人不存在,返回错误
            return ResponseModel.buildNotFoundUserError(null);
        }
        //开始关注,默认没有备注
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            //关注失败,返回服务器异常错误
            return ResponseModel.buildServiceError();
        }
        //TODO 通知被关注者已被其他人关注
        //关注成功
        return ResponseModel.buildOk(new UserCard(followUser, true));
    }

    //获取某人信息
    @GET
    @Path("{id}") //http://127.0.0.1/api/user/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        //首先鲁棒性检查
        if (Strings.isNullOrEmpty(id)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //关注人是自己直接返回
        if (self.getId().equalsIgnoreCase(id)) {
            return ResponseModel.buildOk(new UserCard(self, true));
        }
        User user = UserFactory.findById(id);
        if (user == null) {
            //未找到需要关注的用户
            return ResponseModel.buildNotFoundUserError(null);
        }
        //查询两人的关注关系
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    /**
     * 搜索用户的接口
     * @param name 用户name
     * @return
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();
        //开始搜索
        List<User> searchUsers = UserFactory.search(name);
        //把查询的用户封装为UserCard
        //首先获取当前用户的联系人列表
        final List<User> contacts = UserFactory.contacts(self);
        //把User转化为UserCard
        List<UserCard> userCards = searchUsers.stream().map(user -> {
            //判断要搜索的人是否是当前用户或者是当前用户联系人列表中的人
            boolean isFollow = user.getId().equalsIgnoreCase(self.getId()) || contacts.stream().anyMatch(
                    contactUser -> contactUser.getId().equalsIgnoreCase(user.getId())
            );
            return new UserCard(user, isFollow);
        }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }
}
