package com.gatisnau.gati;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.NonNull;

public class ScheduleObject {

    @SerializedName("schelude")
    @Expose
    private List<Schedule> schedule = null;

    public List<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }

    public class Schedule {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("type")
        @Expose
        private int type = -1;
        @SerializedName("date")
        @Expose
        private String date;

        public int getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "Schedule{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", image='" + image + '\'' +
                    ", type=" + type +
                    ", date='" + date + '\'' +
                    '}';
        }
    }
}