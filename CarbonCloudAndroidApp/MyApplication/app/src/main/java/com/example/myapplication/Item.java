package com.example.myapplication;

public class Item {
    private String Name;
    private int image_id;
    public Item(String name,int id)
    {
        Name=name;
        image_id=id;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
