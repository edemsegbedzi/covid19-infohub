package com.bigapps.mindit;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface WorldCaseRepository extends CrudRepository<WorldCase,Long> {
    WorldCase findByDate(LocalDate date);

    WorldCase findTopByOrderByIdDesc();

}
