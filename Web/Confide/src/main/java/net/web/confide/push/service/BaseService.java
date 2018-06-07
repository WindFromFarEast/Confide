package net.web.confide.push.service;

import net.web.confide.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class BaseService {

    //添加上下文注解,该注解会给securityContext赋值,具体值为拦截器中锁返回的上下文
    @Context
    protected SecurityContext securityContext;

    /**
     * 从上下文中直接获取用户信息
     * @return
     */
    protected User getSelf() {
        if (securityContext == null) {
            return null;
        }
        return (User) securityContext.getUserPrincipal();
    }
}
