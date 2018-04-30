package net.web.confide.push.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
//该类访问路径为:127.0.0.1/api/account
@Path("/account")
public class AccountService {

    @GET //只有get请求才能触发这个方法
    @Path("/login") //127.0.0.1/api/account/login
    public String get() {
        return "you get the login";
    }
}
