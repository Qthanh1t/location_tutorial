package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.tutorial.dto.auth.LoginInputDto;
import com.ord.tutorial.dto.auth.LoginOutputDto;
import com.ord.tutorial.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiResource {
    private final AuthService authService;

    @PostMapping(path = "/login")
    public CommonResultDto<LoginOutputDto> login(@RequestBody @Valid LoginInputDto input) {
        var token = authService.login(input.getUsername(), input.getPassword());
        return CommonResultDto.success(LoginOutputDto.builder().token(token).build());

    }
}
