package net.web.confide.push.provider;

import com.google.common.base.Strings;
import net.web.confide.push.bean.api.base.ResponseModel;
import net.web.confide.push.bean.db.User;
import net.web.confide.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * 用户所有请求的接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //检查是否是登陆注册接口
        String relativePath = ((ContainerRequest)requestContext).getPath(false);
        if (relativePath.startsWith("account/login")
                || relativePath.startsWith("account/register")) {
            //直接走正常的登录、注册流程
            return;
        }
        //从请求头中获取token
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)) {
            //使用token查询用户信息
            final User self = UserFactory.findByToken(token);
            if (self != null) {
                //查询到了信息
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return self;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return null;
                    }
                });
                return;
            }
        }
        //返回一个账户需要登录的model
        ResponseModel model = ResponseModel.buildAccountError();
        //停止一个请求的下发,调用该方法后直接返回请求
        Response response = Response.status(Response.Status.OK).entity(model).build();
        requestContext.abortWith(response);
    }
}
