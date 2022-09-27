package com.marlon.apolo.tfinal2022.individualChat.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import com.marlon.apolo.tfinal2022.individualChat.DateConverter;

import java.util.Date;

/**
 * A basic class representing an entity that is a row in a one-column database table.
 *
 * @ Entity - You must annotate the class as an entity and supply a table name if not class name.
 * @ PrimaryKey - You must identify the primary key.
 * @ ColumnInfo - You must supply the column name if it is different from the variable name.
 * <p>
 * See the documentation for the full rich set of annotations.
 * https://developer.android.com/topic/libraries/architecture/room.html
 */

@Entity(tableName = "message_table")
public class Mensajito {

    //    @PrimaryKey(autoGenerate = true)
//    private int idMessage;
//
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "idMessageFirebase")
    private String idMessageFirebase;

    @ColumnInfo(name = "idChat")
    private String idChat;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    @NonNull
    @ColumnInfo(name = "typeContent")
    private int typeContent;

    //    @ColumnInfo(name = "date")
//    private Calendar date;
    @NonNull
    @ColumnInfo(name = "from")
    private String from;

    @NonNull
    @ColumnInfo(name = "to")
    private String to;

    @ColumnInfo(name = "created_date")
    @TypeConverters({DateConverter.class})
    public Date createDate;

    @ColumnInfo(name = "readStatus")
    private boolean readStatus;


    public Mensajito(@NonNull String content) {
        this.content = content;
    }

    @Ignore
    public Mensajito(String id, @NonNull String content) {
//        this.idMessage = id;
        this.idMessageFirebase = id;
        this.content = content;
    }

    @Ignore
    public Mensajito(String id, @NonNull String content, @NonNull Date date) {
        this.createDate = date;
//        this.idMessage = id;
        this.idMessageFirebase = id;
        this.content = content;
    }
//    public int getIdMessage(){
//        return this.idMessage;
//    }
//
//    public void setIdMessage(int id) {
//        this.idMessage = id;
//    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public int getTypeContent() {
        return typeContent;
    }

    public void setTypeContent(int typeContent) {
        this.typeContent = typeContent;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getIdMessageFirebase() {
        return idMessageFirebase;
    }

    public void setIdMessageFirebase(String idMessageFirebase) {
        this.idMessageFirebase = idMessageFirebase;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    @Override
    public String toString() {
        return "Mensajito{" +
                "idMessageFirebase='" + idMessageFirebase + '\'' +
                ", idChat='" + idChat + '\'' +
                ", content='" + content + '\'' +
                ", typeContent=" + typeContent +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", createDate=" + createDate +
                ", readStatus=" + readStatus +
                '}';
    }
}
