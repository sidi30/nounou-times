package com.nounou.times.repository;

import com.nounou.times.model.HeuresSupplementaires;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HeuresSupplementairesRepository implements PanacheRepository<HeuresSupplementaires> {
}
