package com.and20roid.backend.common.auth;

import com.and20roid.backend.vo.AuthUserDTO;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthUserAnnotation = parameter.getParameterAnnotation(AuthUser.class) != null;
        boolean isAuthUserInfoType = parameter.getParameterType().equals(AuthUserDTO.class);

        return hasAuthUserAnnotation && isAuthUserInfoType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new AuthUserDTO(Long.parseLong(userDetails.getUsername()));
    }
}
