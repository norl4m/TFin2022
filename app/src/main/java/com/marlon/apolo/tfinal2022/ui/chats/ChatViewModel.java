package com.marlon.apolo.tfinal2022.ui.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlon.apolo.tfinal2022.individualChat.repository.ChatRepository;
import com.marlon.apolo.tfinal2022.model.Chat;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private ChatRepository chatRepository;
    private LiveData<List<Chat>> allChats;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
    }

    public LiveData<List<Chat>> getAllChats() {
        allChats = chatRepository.getAllChats();
        return allChats;
    }
}