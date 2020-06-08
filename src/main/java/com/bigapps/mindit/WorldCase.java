package com.bigapps.mindit;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
public class WorldCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Integer deathCount,recoveredCount,number,recent;

    @Column(unique = true)
    LocalDate date;

    @CreationTimestamp
    Timestamp createdAt;

    @UpdateTimestamp
    Timestamp updatedAt;

    public WorldCase(){}

    public WorldCase(Integer deathCount, Integer recoveredCount, Integer number, Integer recent, LocalDate date) {
        this.deathCount = deathCount;
        this.recoveredCount = recoveredCount;
        this.number = number;
        this.recent = recent;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(Integer deathCount) {
        this.deathCount = deathCount;
    }

    public Integer getRecoveredCount() {
        return recoveredCount;
    }

    public void setRecoveredCount(Integer recoveredCount) {
        this.recoveredCount = recoveredCount;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getRecent() {
        return recent;
    }

    public void setRecent(Integer recent) {
        this.recent = recent;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}

