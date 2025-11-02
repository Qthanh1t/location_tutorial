package com.ord.tutorial.api;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.tutorial.dto.common.DropdownItemDto;
import com.ord.tutorial.service.DropdownService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dropdowns")
@PreAuthorize("isAuthenticated()") // Yêu cầu đăng nhập để xem dropdown
public class DropdownApiResource {

    private final DropdownService dropdownService;

    @GetMapping("/provinces")
    public CommonResultDto<List<DropdownItemDto>> getProvincesDropdown() {
        List<DropdownItemDto> data = dropdownService.getProvincesDropdown();
        return CommonResultDto.success(data);
    }
}