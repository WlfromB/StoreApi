package com.example.demo.constant.classes;

public class NotFoundConstants {
    public static final String USER = "Not found user!";
    public static final String AUTHOR = "Not found author!";
    public static final String BOOK = "Not found book!";
    public static final String ROLE = "Not found role!";

    public static String setMany(String constant){
        return constant.replace("!", "s!");
    }
}
