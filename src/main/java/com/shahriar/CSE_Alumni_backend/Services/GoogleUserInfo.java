package com.shahriar.CSE_Alumni_backend.Services;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleUserInfo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("picture")
    private String picture;




    public GoogleUserInfo() {
    }

    public GoogleUserInfo(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
