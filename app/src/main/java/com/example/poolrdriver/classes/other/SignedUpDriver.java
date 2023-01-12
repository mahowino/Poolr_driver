package com.example.poolrdriver.classes.other;

import com.google.firebase.auth.FirebaseAuth;

public class SignedUpDriver {
    private static String username,names,email,phoneNumber,Networkname,homeLocation, workLocation;
    private FirebaseAuth mAuth;
    private static int profilePic;

    public static int getProfilePic() {
        return profilePic;
    }

    public static void setProfilePic(int profilePic) {
        profilePic = profilePic;
    }


    public SignedUpDriver(String username, String names, String email) {

        this.username=username;
        this.names=names;
        this.email=email;
        mAuth=FirebaseAuth.getInstance();
        username=mAuth.getCurrentUser().getUid();



    }

    public static String getHomeLocation() {
        return homeLocation;
    }

    public static void setHomeLocation(String homeLocation) {
        SignedUpDriver.homeLocation = homeLocation;
    }

    public static String getWorkLocation() {
        return workLocation;
    }

    public static void setWorkLocation(String workLocation) {
        SignedUpDriver.workLocation = workLocation;
    }

    public static void setEmail(String email) {
        SignedUpDriver.email = email;
    }

    public static String getNetworkname() {
        return Networkname;
    }

    public static void setNetworkname(String networkname) {
        Networkname = networkname;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public static void setNames(String names) {
        SignedUpDriver.names = names;
    }

    public static void setPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber;
    }
    public static void setUsername(String username) {username= username;
    }

    public static String getNetwork() {
        return Networkname;
    }

    public static void setNetwork(String network) {
        Networkname = network;
    }

    public  static String getUsername() {
        return username;
    }



    public  static String getNames() {
        return names;
    }



    public static String getEmail() {
        return email;
    }




    public  static String getPhoneNumber() {
        return phoneNumber;
    }
}
