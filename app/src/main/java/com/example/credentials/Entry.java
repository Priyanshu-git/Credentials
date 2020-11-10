package com.example.credentials;

public class Entry {
    private String NAME, PASS, URL;

    public Entry(){};

    public Entry(String NAME, String PASS, String URL) {
        this.NAME = NAME;
        this.PASS = PASS;
        this.URL = URL;

    }

    public String getNAME() {
        return NAME;
    }

    public String getPASS() {
        return PASS;
    }

    public String getURL() {
        return URL;
    }


}