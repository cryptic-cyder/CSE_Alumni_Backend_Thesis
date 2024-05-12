package com.shahriar.CSE_Alumni_backend.Entities;

public class AdminRequest {

    String adminEmail;
    String adminPassword;

    public AdminRequest() {
    }


    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public AdminRequest(String email, String password) {
        this.adminEmail = email;
        this.adminPassword = password;
    }
}
