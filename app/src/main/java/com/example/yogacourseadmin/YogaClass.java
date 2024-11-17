package com.example.yogacourseadmin;

public class YogaClass {
    private int classId;
    private String teacher;
    private String date;
    private String comments;
    private int courseId;

    public YogaClass(int classId, String teacher, String date, String comments, int courseId) {
        this.classId = classId;
        this.teacher = teacher;
        this.date = date;
        this.comments = comments;
        this.courseId = courseId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
