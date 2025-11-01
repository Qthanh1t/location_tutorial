package com.ord.tutorial.dao;

import com.ord.core.util.StringUtils;
import com.ord.tutorial.dto.master_data.WardDto;
import com.ord.tutorial.dto.master_data.WardPagedInput;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WardDao {

    @PersistenceContext
    private EntityManager em;

    public Integer getPageCount(WardPagedInput input) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        sb.append("SELECT COUNT(w.id) ");
        sb.append("FROM WardEntity w ");
        sb.append("LEFT JOIN ProvinceEntity p ON w.provinceCode = p.code ");

        buildWhereClause(sb, params, input);

        Query query = em.createQuery(sb.toString());
        params.forEach(query::setParameter);

        return ((Number) query.getSingleResult()).intValue();
    }

    public List<WardDto> getPageItems(WardPagedInput input) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        sb.append("SELECT NEW com.ord.tutorial.dto.master_data.WardDto( ");
        sb.append("   w.id, w.code, w.name, w.provinceCode, p.name, ");
        sb.append("   w.createdBy, w.createdDate, w.lastModifiedBy, w.lastModifiedDate ");
        sb.append(") ");

        sb.append("FROM WardEntity w ");
        sb.append("LEFT JOIN ProvinceEntity p ON w.provinceCode = p.code ");

        buildWhereClause(sb, params, input);

        applySorting(sb, input.getSort());

        TypedQuery<WardDto> query = em.createQuery(sb.toString(), WardDto.class);
        params.forEach(query::setParameter);

        query.setFirstResult(input.getSkipCount());
        query.setMaxResults(input.getMaxResultCount());

        return query.getResultList();
    }

    private void buildWhereClause(StringBuilder sb, Map<String, Object> params, WardPagedInput input) {
        sb.append("WHERE 1=1 ");

        // Lọc theo provinceCode
        if (!StringUtils.isNullOrBlank(input.getProvinceCode())) {
            sb.append("AND w.provinceCode = :provinceCode ");
            params.put("provinceCode", input.getProvinceCode());
        }

        // Lọc theo fts
        String fts = input.getFts();
        if (!StringUtils.isNullOrBlank(fts)) {
            sb.append("AND (lower(w.code) LIKE :fts OR ");
            sb.append("     lower(w.name) LIKE :fts OR ");
            sb.append("     lower(p.name) LIKE :fts) ");
            params.put("fts", "%" + fts + "%");
        }
    }

    private void applySorting(StringBuilder sb, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            sb.append(" ORDER BY w.id ASC ");
            return;
        }

        sb.append(" ORDER BY ");
        int i = 0;
        for (Sort.Order order : sort) {
            if (i > 0) {
                sb.append(", ");
            }

            String property = order.getProperty();
            String sortField;

            switch (property) {
                case "code":
                    sortField = "w.code";
                    break;
                case "name":
                    sortField = "w.name";
                    break;
                case "provinceName":
                    sortField = "p.name";
                    break;
                case "createdDate":
                    sortField = "w.createdDate";
                    break;
                default:
                    continue;
            }

            sb.append(sortField).append(" ").append(order.isAscending() ? "ASC" : "DESC");
            i++;
        }

        if (i == 0) {
            sb.append(" w.id ASC ");
        }
    }
}