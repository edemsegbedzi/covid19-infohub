package com.bigapps.mindit;


import java.time.LocalDate;

public class Data {
    Integer deathCount,recoveredCount,confirmedCount,recentCount;


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

    public Integer getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(Integer confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public Integer getRecentCount() {
        return recentCount;
    }

    public void setRecentCount(Integer recentCount) {
        this.recentCount = recentCount;
    }
}
