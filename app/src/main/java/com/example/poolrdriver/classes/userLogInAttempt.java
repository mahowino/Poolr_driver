package com.example.poolrdriver.classes;


import com.example.poolrdriver.Firebase.User;

public  class userLogInAttempt {
    public static String name,email,password;
    public static User user;

    public userLogInAttempt(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        userLogInAttempt.name = name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        userLogInAttempt.email = email;
    }


}
