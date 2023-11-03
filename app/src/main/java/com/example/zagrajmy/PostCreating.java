package com.example.zagrajmy;


import java.time.LocalTime;
import java.util.Date;

import io.realm.RealmObject;

public class PostCreating extends RealmObject {
    private String sportType;
    private String cityName;
    private Date dateTime;

    private String skillLevel;
    // private LocalTime hourTime;
    private String additionalInfo;

    public PostCreating() {

    }

    public PostCreating(String sportType, String cityName, String additionalInfo, String skillLevel, Date dateTime, LocalTime hourTime) {
        this.sportType = sportType;
        this.cityName = cityName;
        this.additionalInfo = additionalInfo;
        this.skillLevel = skillLevel;
        this.dateTime = dateTime;
        // this.hourTime = hourTime;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }


}


