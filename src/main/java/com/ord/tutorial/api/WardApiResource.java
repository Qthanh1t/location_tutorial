package com.ord.tutorial.api;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.crud.service.SimpleCrudAppService;
import com.ord.tutorial.dao.WardDao;
import com.ord.tutorial.dto.master_data.WardDto;
import com.ord.tutorial.dto.master_data.WardPagedInput;
import com.ord.tutorial.entity.WardEntity;
import com.ord.tutorial.repository.ProvinceRepository;
import com.ord.tutorial.repository.WardRepository;
import com.ord.core.crud.repository.spec.SpecificationBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/wards")
public class WardApiResource extends SimpleCrudAppService<
        WardEntity,
        Integer,
        WardDto,
        WardPagedInput> {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final WardDao wardDao;

    @Override
    protected OrdEntityRepository<WardEntity, Integer> getRepository() {
        return wardRepository;
    }

//    @Override
//    protected Specification<WardEntity> buildSpecificationForPaging(WardPagedInput input) {
//        return SpecificationBuilder.<WardEntity>builder()
//                .withEqIfNotNull("provinceCode", input.getProvinceCode())
//                .withLikeFts(input.getFts(), "code", "name")
//                .build();
//    }
    @Override
    protected Integer getTotalCount(WardPagedInput input) {
        return wardDao.getPageCount(input);
    }

    @Override
    protected List<WardDto> fetchPagedItems(WardPagedInput input) {
        return wardDao.getPageItems(input);
    }

    @Override
    protected String getGetPagedListPolicy() {
        return getPolicyForAction("get-paged");
    }

    @Override
    protected String getCreatePolicy() {
        return getPolicyForAction("create");
    }

    @Override
    protected String getUpdatePolicy() {
        return getPolicyForAction("update");
    }

    @Override
    protected String getRemovePolicy() {
        return getPolicyForAction("remove");
    }

    @Override
    protected void validationBeforeCreate(WardDto wardDto) {
        checkProvinceCode(wardDto.getProvinceCode());
        if (wardRepository.existsByCode(wardDto.getCode())) {
            throwBusiness("Mã xã đã tồn tại");
        }
        super.validationBeforeCreate(wardDto);
    }

    @Override
    protected void validationBeforeUpdate(WardDto wardDto, WardEntity entityToUpdate) {
        checkProvinceCode(wardDto.getProvinceCode());
        if (wardRepository.existsByCodeAndIdNot(wardDto.getCode(), entityToUpdate.getId())) {
            throwBusiness("Mã xã đã tồn tại trong hệ thống");
        }
        super.validationBeforeUpdate(wardDto, entityToUpdate);
    }

    private void checkProvinceCode(String provinceCode) {
        if (!provinceRepository.existsByCode(provinceCode)) {
            throwBusiness("Mã tỉnh không tồn tại");
        }
    }

    @Override
    protected void validationBeforeRemove(WardEntity entityToRemove) {
        // super.validationBeforeRemove(entityToRemove);
    }

    @Override
    protected String getEntityName() {
        return "ward";
    }
}
