package com.nounou.times.repository;

import com.nounou.times.model.Child;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ChildRepository implements PanacheRepository<Child> {

    public List<Child> findByParentId(Long parentId) {
        return list("user.id", parentId);
    }
}