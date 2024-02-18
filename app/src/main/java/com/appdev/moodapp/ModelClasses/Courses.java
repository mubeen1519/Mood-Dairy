package com.appdev.moodapp.ModelClasses;

public class Courses {
    String title, courses , url;

    public Courses() {
    }

    public Courses(String title, String courses, String url) {
        this.title = title;
        this.courses = courses;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() { return  url;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }
}
