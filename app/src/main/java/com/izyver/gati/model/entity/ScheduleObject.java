package com.izyver.gati.model.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.izyver.gati.model.ComparableImage;

import java.util.List;

public class ScheduleObject {

    @SerializedName("day")
    @Expose
    private List<Schedule> day = null;
    @SerializedName("zao")
    @Expose
    private List<Schedule> zao = null;

    public List<Schedule> getDay() {
        return day;
    }

    public void setDay(List<Schedule> day) {
        this.day = day;
    }

    public List<Schedule> getZao() {
        return zao;
    }

    public void setZao(List<Schedule> zao) {
        this.zao = zao;
    }

    public class Schedule implements ComparableImage {
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
        private int type;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("day_week")
        @Expose
        private String dayWeek;

        public Integer getId() {
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

        public void setType(Integer type) {
            this.type = type;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public String getDayOfWeek() {
            return dayWeek;
        }

        @Override
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDayWeek() {
            return dayWeek;
        }

        public void setDayWeek(String dayWeek) {
            this.dayWeek = dayWeek;
        }

        @Override
        public String toString() {
            return "Schedule{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", image='" + image + '\'' +
                    ", type=" + type +
                    ", date='" + date + '\'' +
                    ", dayWeek='" + dayWeek + '\'' +
                    '}';
        }
    }
}