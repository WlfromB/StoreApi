package com.example.demo.constant.classes;

public class URIStartWith {
    public static final String AUTH="/auth";
    public static final String AUTHOR="/author";
    public static final String BOOK="/book";
    public static final String DISCOUNT="/discount";
    public static final String MAIL="/mail";
    public static final String USER="/user";

    public static String getAllAuthorities(String uri) {
        return uri + "/**";
    }

    public static String getAddedIdParam(String uri) {
        return uri + "/{id}";
    }
}
