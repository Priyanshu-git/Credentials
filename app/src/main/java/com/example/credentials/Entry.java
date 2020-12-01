package com.example.credentials;

public class Entry {
    private String NAME, PASS, URL, DOC_ID;

    public Entry() {};

    public Entry(String NAME, String PASS, String URL, String DOC_ID) {
        this.NAME = NAME;
        this.PASS = PASS;
        this.URL = URL;
        this.DOC_ID=DOC_ID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getDOC_ID() {
        return DOC_ID;
    }

    public String getPASS() {
        return PASS;
    }

    public String getURL() {
        return URL;
    }


}