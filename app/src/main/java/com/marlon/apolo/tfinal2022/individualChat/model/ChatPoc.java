package com.marlon.apolo.tfinal2022.individualChat.model;

public class ChatPoc {
    private String idRemoteUser;
    private MessageCloudPoc lastMessageCloudPoc;
    private String stateRemoteUser;
    private String nameDeleteUser;


    public String getIdRemoteUser() {
        return idRemoteUser;
    }

    public void setIdRemoteUser(String idRemoteUser) {
        this.idRemoteUser = idRemoteUser;
    }

    public MessageCloudPoc getLastMessageCloudPoc() {
        return lastMessageCloudPoc;
    }

    public void setLastMessageCloudPoc(MessageCloudPoc lastMessageCloudPoc) {
        this.lastMessageCloudPoc = lastMessageCloudPoc;
    }

    public String getStateRemoteUser() {
        return stateRemoteUser;
    }

    public void setStateRemoteUser(String stateRemoteUser) {
        this.stateRemoteUser = stateRemoteUser;
    }

    @Override
    public String toString() {
        return "ChatPoc{" +
                "idRemoteUser='" + idRemoteUser + '\'' +
                ", lastMessageCloudPoc=" + lastMessageCloudPoc +
                ", stateRemoteUser='" + stateRemoteUser + '\'' +
                '}';
    }
}
