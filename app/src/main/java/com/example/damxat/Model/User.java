package com.example.damxat.Model;

public class User {
    //Aquesta clase serveix per definir l'objecte user per poder utilitzarho despres
    String id;
    String username;
    String status;

    public User(String id, String username, String status) {
        this.id = id;
        this.username = username;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
