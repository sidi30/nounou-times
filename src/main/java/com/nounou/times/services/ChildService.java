package com.nounou.times.services;

import com.nounou.times.model.Child;
import com.nounou.times.model.User;
import com.nounou.times.repository.ChildRepository;
import com.nounou.times.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ChildService {

    @Inject
    ChildRepository childRepository;

    @Inject
    UserRepository userRepository;

    public List<Child> getAllChildrenByParent(Long parentId) {
        return childRepository.findByParentId(parentId);
    }

    public Child getChildById(Long id) {
        return childRepository.findById(id);
    }

    @Transactional
    public Child createChild(Long parentId, Child child) {
        User parent = userRepository.findById(parentId);
        if (parent != null) {
            child.setParent(parent);
            childRepository.persist(child);
            return child;
        }
        return null;
    }

    @Transactional
    public Child updateChild(Long id, Child child) {
        Child existingChild = childRepository.findById(id);
        if (existingChild != null) {
            existingChild.setName(child.getName());
            return existingChild;
        }
        return null;
    }

    @Transactional
    public boolean deleteChild(Long id) {
        return childRepository.deleteById(id);
    }
}
