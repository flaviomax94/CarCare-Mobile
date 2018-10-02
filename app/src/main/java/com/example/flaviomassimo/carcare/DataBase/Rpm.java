package com.example.flaviomassimo.carcare.DataBase;

public class Rpm {
    private long id;

    private String rpmValue;

    private String fuelType;

    private String speed;

    private String position;

    private String date;



    public long getId(){ return id;}

    public void setId(long val){
        this.id=val;
    }




    public String getRpmValue(){
        return rpmValue;
    }

    public void setRpmValue(String val){
        this.rpmValue=val;
    }




    public String getFuelType(){
        return fuelType;
    }

    public void setFuelType(String val){
        this.fuelType=val;
    }





    public String getSpeed(){
        return speed;
    }

    public void setSpeed(String val){
        this.speed=val;
    }




    public String getDate(){
        return date;
    }

    public void setDate(String d){
        this.date=d;
    }



    public String getPosition(){
        return position;
    }

    public void setPosition(String pos){
        this.position=pos;
    }


}

