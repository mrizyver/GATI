package com.izyver.gati.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleProcessing {

    private DateManager date;

    public ScheduleProcessing() {
        date = new DateManager();
    }

    public <T extends ComparableImage> Map<Integer, T> getActualImages(List<T> imageEntities){
        Map<Integer, T> map = new HashMap<>(imageEntities.size());

        for (T imageEntity : imageEntities) {
            int index = date.getDayOfWeek(imageEntity);
            if (map.containsKey(index)){
                T oldValue = map.get(index);
                int result = date.compare(oldValue, imageEntity);
                if (result > 0) continue;
            }
            map.put(index, imageEntity);
        }
        return map;
    }

    public  <T extends ComparableImage> boolean isExistSaturday(List<T> schedules) {
        for (T schedule : schedules) {
            if (date.isSaturday(schedule)){
                return true;
            }
        }
        return false;
    }
}
