package com.nounou.times.repository;

import com.nounou.times.model.Report;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.list;

@ApplicationScoped
public class ReportRepository implements PanacheRepository<Report> {

    public List<Report> findByParentId(Long parentId) {
        return list("user.id", parentId);
    }
}