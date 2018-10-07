package com.example.flaviomassimo.carcare.DataBase;

import java.util.*;

public class Car {
    private String LICENSE_PLATE;
    private String MAKE;
    private String MODEL;

    public String getFUEL_TYPE() {
        return FUEL_TYPE;
    }

    public void setFUEL_TYPE(String FUEL_TYPE) {
        this.FUEL_TYPE = FUEL_TYPE;
    }

    private String FUEL_TYPE;
    private double KM;

    public String getLICENSE_PLATE() {
        return LICENSE_PLATE;
    }

    public void setLICENSE_PLATE(String LICENSE_PLATE) {
        this.LICENSE_PLATE = LICENSE_PLATE;
    }

    public String getMAKE() {
        return MAKE;
    }

    public void setMAKE(String MAKE) {
        this.MAKE = MAKE;
    }

    public String getMODEL() {
        return MODEL;
    }

    public void setMODEL(String MODEL) {
        this.MODEL = MODEL;
    }

    public double getKM() {
        return KM;
    }

    public void setKM(double KM) {
        this.KM = KM;
    }

    public Car() {

    }
    public Car(String LicensePlate) {
        LICENSE_PLATE=LicensePlate;
    }
    public Car(String LicensePlate, String Make,String Model,double Km){

        LICENSE_PLATE=LicensePlate;
        MAKE=Make;
        MODEL=Model;
        KM=Km;
    }
}
