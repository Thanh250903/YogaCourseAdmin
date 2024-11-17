package com.example.yogacourseadmin;

public class Course {
    private int idcourse;
    private String dateoftheweek;
    private String timeofcourse;
    private int capacity;
    private int duration;
    private double priceperclass;
    private String typeclass;
    private String description;
    private String imageUrl;

    // Constructor để khởi tạo đối tượng Course
    public Course(int idcourse,String dateoftheweek, String timeofcourse, int capacity, int duration, double priceperclass, String typeclass, String description, String ImageUrl) {
        this.idcourse = idcourse;
        this.dateoftheweek = dateoftheweek;
        this.timeofcourse = timeofcourse;
        this.capacity = capacity;
        this.duration = duration;
        this.priceperclass = priceperclass;
        this.typeclass = typeclass;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter cho các trường dữ liệu
    public int getIdCourse() {
        return idcourse;
    }

    public void setIdCourse(int idcourse) {
        this.idcourse = idcourse;
    }

    public String getDateoftheweek() {
        return dateoftheweek;
    }

    public void setDateoftheweek(String dateoftheweek) {
        this.dateoftheweek = dateoftheweek;
    }

    public String getTimeofcourse() {
        return timeofcourse;
    }

    public void setTimeofcourse(String timeofcourse) {
        this.timeofcourse = timeofcourse;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPriceperclass() {
        return priceperclass;
    }

    public void setPriceperclass(double priceperclass) {
        this.priceperclass = priceperclass;
    }

    public String getTypeclass() {
        return typeclass;
    }

    public void setTypeclass(String typeclass) {
        this.typeclass = typeclass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
