package com.ord.tutorial.service;

import com.ord.tutorial.dto.common.DropdownItemDto;
import com.ord.tutorial.repository.ProvinceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DropdownService {

    private final ProvinceRepository provinceRepository;

    public static final String CACHE_PROVINCES_DROPDOWN = "provinces_dropdown";

    @Cacheable(CACHE_PROVINCES_DROPDOWN)
    public List<DropdownItemDto> getProvincesDropdown() {
        return provinceRepository.findAll(Sort.by("name")).stream()
                .map(province -> new DropdownItemDto(province.getCode(), province.getName()))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = CACHE_PROVINCES_DROPDOWN, allEntries = true)
    public void clearProvincesDropdownCache() {
        log.info("---CACHE EVICT--- Đã xóa cache danh sách Tỉnh.");
    }
}