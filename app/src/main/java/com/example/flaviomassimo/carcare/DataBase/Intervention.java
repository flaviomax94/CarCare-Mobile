package com.example.flaviomassimo.carcare.DataBase;

public class Intervention {
    private static String PLATE,TITLE,DESCRIPTION;
    private static Double KM;

    public Intervention(String TITLE,String PLATE,Double KM){
        Intervention.TITLE=TITLE;
        Intervention.PLATE=PLATE;
        Intervention.KM=KM;
    }
    public static String getPLATE() {
        return PLATE;
    }

    public static void setPLATE(String PLATE) {
        Intervention.PLATE = PLATE;
    }

    public static String getTITLE() {
        return TITLE;
    }

    public static void setTITLE(String TITLE) {
        Intervention.TITLE = TITLE;
    }

    public static String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public static void setDESCRIPTION(String DESCRIPTION) {
        Intervention.DESCRIPTION = DESCRIPTION;
    }

    public static Double getKM() {
        return KM;
    }

    public static void setKM(Double KM) {
        Intervention.KM = KM;
    }
}
