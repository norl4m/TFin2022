package com.marlon.apolo.tfinal2022.model;

public class UsuarioFirebaseAuth {
    private String uid;
    private String email;
    private String phoneNumber;
    private String password;
    private String extraLol;
    private Usuario extraUsuarioLol;


    public UsuarioFirebaseAuth() {
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExtraLol() {
        return extraLol;
    }

    public void setExtraLol(String extraLol) {
        this.extraLol = extraLol;
    }

    public Usuario getExtraUsuarioLol() {
        return extraUsuarioLol;
    }

    public void setExtraUsuarioLol(Usuario extraUsuarioLol) {
        this.extraUsuarioLol = extraUsuarioLol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
