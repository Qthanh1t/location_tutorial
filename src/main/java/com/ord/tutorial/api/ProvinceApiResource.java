package com.ord.tutorial.api;

import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import com.ord.core.crud.service.SimpleCrudAppService;
import com.ord.tutorial.dao.ProvinceDao;
import com.ord.tutorial.dto.master_data.ProvinceDto;
import com.ord.tutorial.entity.ProvinceEntity;
import com.ord.tutorial.repository.ProvinceRepository;
import com.ord.tutorial.repository.WardRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/provinces")
public class ProvinceApiResource extends SimpleCrudAppService<
        ProvinceEntity,
        Integer,
        ProvinceDto,
        PagedResultRequestDto
        > {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final ProvinceDao provinceDao;

//    @Override
//    protected String getGetPagedListPolicy() {
//        return getPolicyForAction("paged");
//    }

//    @Override
//    protected Specification<ProvinceEntity> buildSpecificationForPaging(PagedResultRequestDto pagedResultRequestDto) {
//        return SpecificationBuilder.<ProvinceEntity>builder()
//                .withLikeFts(pagedResultRequestDto.getFts(), "code", "name")
//                .build();
//    }
        @Override
    protected Integer getTotalCount(PagedResultRequestDto pagedResultRequestDto) {
        return provinceDao.getPageCount(pagedResultRequestDto);
    }

    @Override
    protected List<ProvinceDto> fetchPagedItems(PagedResultRequestDto pagedResultRequestDto) {
        return provinceDao.getPageItems(pagedResultRequestDto);
    }

    @Override
    protected void validationBeforeCreate(ProvinceDto provinceDto) {
        if (provinceRepository.existsByCode(provinceDto.getCode())) {
            throwBusiness("Mã tỉnh đã tồn tại");
        }
    }

    @Override
    protected void validationBeforeUpdate(ProvinceDto provinceDto, ProvinceEntity entityToUpdate) {
        if (provinceRepository.existsByCodeAndIdNot(provinceDto.getCode(), entityToUpdate.getId())) {
            throwBusiness("Mã tỉnh đã tồn tại trong hệ thống");
        }
    }

    @Override
    protected void validationBeforeRemove(ProvinceEntity entityToRemove) {
        if (wardRepository.existsByProvinceCode(entityToRemove.getCode())) {
            throwBusiness("Tỉnh đã được sử dụng, không thể xóa");
        }
    }

    @Override
    protected OrdEntityRepository<ProvinceEntity, Integer> getRepository() {
        return provinceRepository;
    }


    @Override
    protected String getEntityName() {
        return "province";
    }
}
