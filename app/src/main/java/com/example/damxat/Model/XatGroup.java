package com.example.damxat.Model;

import java.util.ArrayList;

public class XatGroup {
    //Aquesta clase serveix per definir l'objecte XatGroup per poder utilitzarho despres
    String name;
    ArrayList<String> users;
    ArrayList<Xat> xats;


    public XatGroup(String name) {
        this.name = name;

    }

    public XatGroup() {
    }


    public String getName() {
        return name;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<Xat> getXats() {
        return xats;
    }
}
