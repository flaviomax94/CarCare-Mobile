package com.example.flaviomassimo.carcare.DataBase;

import java.io.File;

public class Path {
    public File getFILEPATH() {
        return FILEPATH;
    }

    public void setFILEPATH(File FILEPATH) {
        this.FILEPATH = FILEPATH;
    }

    public String getPATHNAME() {
        return PATHNAME;
    }

    public void setPATHNAME(String PATHNAME) {
        this.PATHNAME = PATHNAME;
    }

    private File FILEPATH;
    private String PATHNAME;
    public Path(){}
    public Path( File file,String name){

        FILEPATH=file;
        PATHNAME=name;
    }


}
