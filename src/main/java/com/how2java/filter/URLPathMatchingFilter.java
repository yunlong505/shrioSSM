package com.how2java.filter;

import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.how2java.service.PermissionService;

public class URLPathMatchingFilter extends PathMatchingFilter {
    @Autowired
    PermissionService permissionService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        String requestURI = getPathWithinApplication(request);

        Subject subject = SecurityUtils.getSubject();
        // 如果没有登录，就跳转到登录页面
        if (!subject.isAuthenticated()) {
            WebUtils.issueRedirect(request, response, "/login");
            return false;
        }

        // 看看这个路径权限里有没有维护，如果没有维护，一律放行(也可以改为一律不放行)
        boolean needInterceptor = permissionService.needInterceptor(requestURI);
        if (!needInterceptor) {
            return true;
        } else {
            //设置是否有权限的标志
            boolean hasPermission = false;
            String userName = subject.getPrincipal().toString();

            Set<String> permissionUrls = permissionService.listPermissionURLs(userName);
            for (String url : permissionUrls) {

                // 有这个路径权限的话就把这个标志设为true
                if (url.equals(requestURI)) {
                    hasPermission = true;
                    break;
                }
            }
            if (hasPermission)
                //有这个路径的权限就返回true,过滤通过
                return true;
            else {
                UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径 " + requestURI + " 的权限");
                subject.getSession().setAttribute("ex", ex);

                WebUtils.issueRedirect(request, response, "/unauthorized");
                return false;
            }

        }

    }
}