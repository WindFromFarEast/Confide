package net.web.confide.push.service;

import net.web.confide.push.bean.api.account.RegisterModel;
import net.web.confide.push.bean.card.UserCard;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *
 */
//该类访问路径为:127.0.0.1/api/account
@Path("/account")
public class AccountService {

    /**
     * 后台用户注册接口
     * @param model
     * @return
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserCard register(RegisterModel model) {
        //首先查询是否已经注册过
        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null) {
            UserCard card = new UserCard();
            card.setName("该手机号已经注册过了");
            return card;
        }
        //再通过名称查询是否已经注册过
        user = UserFactory.findByName(model.getName().trim());
        if (user != null) {
            UserCard card = new UserCard();
            card.setName("该名称已经注册过了");
            return card;
        }
        //没有注册过,那就进行注册
        user = UserFactory.register(model.getAccount(), model.getPassword(), model.getName());
        if (user != null) {
            UserCard card = new UserCard();
            card.setName(user.getName());
            card.setPhone(user.getPhone());
            card.setSex(user.getSex());
            card.setModifyAt(user.getUpdateAt());
            return card;
        }
        return null;
    }
}
