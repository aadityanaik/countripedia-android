package com.halfwitdevs.countripedia;

public class BookmarkObject {
    private String alpha2code;
    private String name;

    public BookmarkObject() {

    }

    public BookmarkObject(String alpha2code, String name) {

        this.alpha2code = alpha2code;
        this.name = name;
    }

    public String getAlpha2code() {
        return alpha2code;
    }

    public void setAlpha2code(String alpha2code) {
        this.alpha2code = alpha2code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
