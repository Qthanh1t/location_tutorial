package com.ord.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.enums.CommonResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.ord.tutorial.service.AppMessageService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final AppMessageService messageService;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        CommonResultDto<?> result = CommonResultDto.fail(CommonResultCode.UNAUTHORIZED,
                messageService.getMessage("error.accessDenied"));

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}