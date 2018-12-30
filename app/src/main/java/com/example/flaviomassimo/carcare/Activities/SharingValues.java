package com.example.flaviomassimo.carcare.Activities;

import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.DataBase.Intervention;
import com.example.flaviomassimo.carcare.DataBase.User;
import com.example.flaviomassimo.carcare.DataBase.Rpm;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;

public class SharingValues {

    private static String FullName="";
    private static Car car;
    private static Intervention intervention;
    private static boolean logout=false;
    private static User DBuser=new User("","");
    private static FirebaseUser user;
    private static FirebaseAuth auth;
    private static GoogleSignInClient mGoogleSignInClient;
    private static LinkedList<Rpm> RpmList= new LinkedList<>();
    public static void setFullName(String name){

        FullName=name;
    }
    public static String getName(){
        return FullName;
    }

    public static void setDBUser(String Email,String Name){
        DBuser.setEmail(Email);
        DBuser.setName(Name);
    }
    public static User getDBUser(){
        User u=new User();
        u.setName(DBuser.getName());
        u.setEmail(DBuser.getEmail());
        return u;

    }
    public static void setIntervention(Intervention i){intervention=i;}
    public static Intervention getIntervention(){return intervention;}
    public static void setCar(Car c){car=c;}
    public static Car getCar(){return car;}
    public static void setGoogleSignInClient(GoogleSignInClient gsic){
        mGoogleSignInClient=gsic;
    }
    public static GoogleSignInClient getGoogleSignInClient(){
        return mGoogleSignInClient;
    }
    public static void setCurrentUser(FirebaseUser u){

        user=u;
    }


    public static FirebaseAuth getCurrentUserAuth(){

        return auth;
    }


public static void setLogOut(Boolean b){

        logout=b;
}

public static boolean getLogOut(){

        return logout;
    }
    public static void setCurrentUserAuth(FirebaseAuth u){

        auth=u;
    }


    public static FirebaseUser getCurrentUser(){

        return user;
    }
    public static LinkedList<Rpm> getRpmList(){


        return RpmList;
    }
    public static void setRpmList(LinkedList<Rpm> list){
        RpmList=list;
    }


}
