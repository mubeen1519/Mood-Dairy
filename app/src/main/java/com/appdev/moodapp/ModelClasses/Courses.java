package com.appdev.moodapp.ModelClasses;

public class Courses {
    String title, courses;

    public Courses() {
    }

    public Courses(String title, String courses) {
        this.title = title;
        this.courses = courses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }
}
