package net.web.confide.push.service;

import com.google.common.base.Strings;
import net.web.confide.push.bean.api.base.ResponseModel;
import net.web.confide.push.bean.api.user.UpdateInfoModel;
import net.web.confide.push.bean.card.UserCard;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * 用户信息处理Service
 */
//该类访问路径为:127.0.0.1/api/user
@Path("/user")
public class UserService extends BaseService {

    /**
     * 用户信息修改接口
     *
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
}
