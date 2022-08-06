package com.artistack.jwt;

import com.artistack.base.constant.Code;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {
        response.setHeader("content-type", "application/json");
        response.setStatus(401);

        Code errorcode = Code.EXPIRED_JWT;
        JSONObject params = new JSONObject();
        params.put("success", false);
        params.put("code", errorcode.getCode().toString());
        params.put("message", errorcode.getMessage());
        response.getWriter().write(params.toJSONString());
    }
}
