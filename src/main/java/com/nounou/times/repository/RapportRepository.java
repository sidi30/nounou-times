package com.nounou.times.repository;

import com.nounou.times.model.RapportMensuel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.list;

@ApplicationScoped
public class RapportRepository {
    @Inject
    EntityManager entityManager;

    public RapportMensuel findById(Long id) {
        return entityManager.find(RapportMensuel.class, id);
    }

    public List<RapportMensuel> findAll() {
        return entityManager.createQuery("SELECT r FROM RapportMensuel r", RapportMensuel.class).getResultList();
    }

    @Transactional
    public void save(RapportMensuel rapportMensuel) {
        entityManager.persist(rapportMensuel);
    }

    @Transactional
    public void update(RapportMensuel rapportMensuel) {
        entityManager.merge(rapportMensuel);
    }

    @Transactional
    public void delete(Long id) {
        RapportMensuel rapportMensuel = findById(id);
        if (rapportMensuel != null) {
            entityManager.remove(rapportMensuel);
        }
    }
}
