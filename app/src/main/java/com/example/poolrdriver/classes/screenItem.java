package com.example.poolrdriver.classes;


public class screenItem {
    String title,description;
    int Image;

    public screenItem(String title, String description, int image) {
        this.title = title;
        this.description = description;
        Image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }
}
