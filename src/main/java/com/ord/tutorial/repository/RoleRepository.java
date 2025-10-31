package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.RoleEntity;

import java.util.List;

public interface RoleRepository extends OrdEntityRepository<RoleEntity, Long> {
    public List<RoleEntity> findByNameIn(List<String> name);
}
