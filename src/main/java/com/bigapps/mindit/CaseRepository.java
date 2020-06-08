package com.bigapps.mindit;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface CaseRepository extends CrudRepository<GhanaCase,Long> {
    boolean existsByDate(LocalDate date);

    GhanaCase findByDate(LocalDate date);

    GhanaCase findTopByOrderByIdDesc();
}
