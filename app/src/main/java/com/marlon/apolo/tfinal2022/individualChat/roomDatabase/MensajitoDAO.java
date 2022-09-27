package com.marlon.apolo.tfinal2022.individualChat.roomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.marlon.apolo.tfinal2022.individualChat.model.Mensajito;

import java.util.List;

@Dao
public interface MensajitoDAO {
    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
//    @Query("SELECT * from message_table ORDER BY idMessage ASC")
    @Query("SELECT * from message_table ORDER BY created_date ASC")
    LiveData<List<Mensajito>> getMessages();

    // Recupera los mensajes enviados y recibidos
    @Query("SELECT * from message_table WHERE `from`=:idFrom and `to`=:idTo or`from`=:idTo and `to`=:idFrom ORDER BY created_date ASC")
    LiveData<List<Mensajito>> getFilterMessages(String idFrom, String idTo);


    // Recupera los mensajes enviados y recibidos
    @Query("SELECT * from message_table WHERE `idChat`=:idChat ORDER BY created_date ASC")
    LiveData<List<Mensajito>> getFilterMessagesByChat(String idChat);

    // Recupera los mensajes enviados y recibidos
    @Query("SELECT * from message_table WHERE idMessageFirebase=:id")
    Mensajito getLocalMessage(String id);

    // eliminar los mensajes enviados y recibidos
    @Query("DELETE from message_table WHERE `from`=:idFrom and `to`=:idTo or`from`=:idTo and `to`=:idFrom")
    void deleteFilterMessages(String idFrom, String idTo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Mensajito mensajito);

    @Query("DELETE FROM message_table")
    void deleteAll();

    @Query("SELECT * from message_table LIMIT 1")
    Mensajito[] getAnyWord();

    @Delete
    void deleteMensajito(Mensajito word);

    //
    @Update
    void update(Mensajito... word);

//    @Query("UPDATE message_table SET readStatus = :mensajito WHERE idMessageFirebase =:mensajito")
//    void update(Mensajito...mensajito);

//    @Query("UPDATE message_table SET readStatus = :status WHERE idMessageFirebase =:id")
//    void update(boolean status,int id);
}
