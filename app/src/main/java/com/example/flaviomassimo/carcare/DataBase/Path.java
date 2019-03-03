package com.example.flaviomassimo.carcare.DataBase;

import java.io.File;

public class Path {
    public String getFILEPATH() {
        return FILEPATH;
    }

    public void setFILEPATH(String FILEPATH) {
        this.FILEPATH = FILEPATH;
    }

    public String getPATHNAME() {
        return PATHNAME;
    }

    public void setPATHNAME(String PATHNAME) {
        this.PATHNAME = PATHNAME;
    }

    private String FILEPATH;
    private String PATHNAME;
    public Path(){}
    public Path( String file,String name){

        FILEPATH=file;
        PATHNAME=name;
    }


}
