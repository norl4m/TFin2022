package com.marlon.apolo.tfinal2022.individualChat.model;

public class ChatPocData extends ChatPoc {
    private String name;
    private String foto;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "ChatPocData{" +
                "name='" + name + '\'' +
                ", foto='" + foto + '\'' +
                "} " + super.toString();
    }
}
