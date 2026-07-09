package com.core.hostal.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HostalAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public HostalAuthenticationFailureHandler() {
        super("/login");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error", exception.getMessage());
            flashMap.put("loginEmail", request.getParameter("email"));
            RequestContextUtils.getFlashMapManager(request).saveOutputFlashMap(flashMap, request, response);
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
