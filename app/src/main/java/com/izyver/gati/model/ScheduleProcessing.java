package com.izyver.gati.model;

import com.izyver.gati.model.db.ImageEntity;

import java.util.List;

public class ScheduleProcessing {
    private List<ImageEntity> imageEntities;

    public ScheduleProcessing(List<ImageEntity> imageEntities) {
        this.imageEntities = imageEntities;
    }

    public List<ImageEntity> getActualImages(){
        return null;
    }
}
