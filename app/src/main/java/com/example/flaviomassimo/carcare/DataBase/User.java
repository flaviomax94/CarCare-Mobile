package com.example.flaviomassimo.carcare.DataBase;

public class User {
    private String Name;
    private String Email;
    public User(){}

    public User(String n,String e){
        Email=e;
        Name=n;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
