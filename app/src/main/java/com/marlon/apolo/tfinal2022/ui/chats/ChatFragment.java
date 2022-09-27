package com.marlon.apolo.tfinal2022.ui.chats;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.admin.AdminViewModel;
import com.marlon.apolo.tfinal2022.databinding.FragmentChatBinding;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPocData;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private static final String TAG = ChatFragment.class.getSimpleName();
    private ChatViewModel chatViewModel;
    private FragmentChatBinding binding;
    private RecyclerView recyclerView;
    private Usuario usuarioFrom;
    private SharedPreferences myPreferences;
    private int usuario;
    private AdminViewModel adminViewModel;
    private EmpleadorViewModel empleadorViewModel;
    private TrabajadorViewModel trabajadorViewModel;
    private ChatListAdapter chatListAdapter;
    private ChatListAdapterPoc chatListAdapterPoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myPreferences = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        usuario = myPreferences.getInt("usuario", -1);


        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        empleadorViewModel = new ViewModelProvider(this).get(EmpleadorViewModel.class);
        trabajadorViewModel = new ViewModelProvider(this).get(TrabajadorViewModel.class);
        chatListAdapter = new ChatListAdapter(requireActivity());
        chatListAdapterPoc = new ChatListAdapterPoc(requireActivity());
        chatListAdapterPoc.setActivityInstance(requireActivity());


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            adminViewModel.getAdministradorLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), administrador -> {
                if (administrador != null) {
                    usuarioFrom = administrador;
                    chatListAdapter.setUsuarioFrom(usuarioFrom);
                }
            });

            empleadorViewModel.getOneEmpleador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), empleador -> {
                if (empleador != null) {
                    usuarioFrom = empleador;
                    chatListAdapter.setUsuarioFrom(usuarioFrom);

                }
            });

            trabajadorViewModel.getOneTrabajador(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(getViewLifecycleOwner(), trabajador -> {
                if (trabajador != null) {
                    usuarioFrom = trabajador;
                    chatListAdapter.setUsuarioFrom(usuarioFrom);
                }
            });
        }

        recyclerView = root.findViewById(R.id.fragHomeRecyclerViewChats);
        recyclerView.setAdapter(chatListAdapterPoc);
//        recyclerView.setAdapter(chatListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        chatListAdapterPoc.setRecyclerView(recyclerView);
        ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.getAllChats().observe(getViewLifecycleOwner(), chats -> {
            chatListAdapter.setChats(chats);
//
//            switch (usuario) {
//                case 0:/*admin*/
//                    break;
//                case 2:/*trabajador*/
//                    loadChatTrabajador((ArrayList<Chat>) chats, (ArrayList<Empleador>) chatListAdapter.getEmpleadors());
//                    break;
//                case 1:/*empleador*/
//                    loadChatEmpleador((ArrayList<Chat>) chats, (ArrayList<Trabajador>) chatListAdapter.getTrabajadors());
//                    break;
//            }
        });
//        final TextView textView = binding.textViewChats;
//        chatViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//
//        FirebaseDatabase.getInstance().getReference().child("crazyChats")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<ChatPoc> chatPocArrayList = new ArrayList<>();
//                        for (DataSnapshot data : snapshot.getChildren()) {
//                            ChatPoc chatPoc = data.getValue(ChatPoc.class);
//                            //Log.d(TAG, chatPoc.toString());
//                            chatPocArrayList.add(chatPoc);
//                        }
//
//                        //filterByUsers(chatPocArrayList);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        ArrayList<ChatPoc> chatPocsLocos = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("crazyChats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildAdded");
                        ChatPoc chatPoc = snapshot.getValue(ChatPoc.class);
                        //Log.d(TAG, chatPoc.toString());
                        chatPocsLocos.add(chatPoc);
                        filterByUsers(chatPocsLocos);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildChanged");
                        ChatPoc chatPocChanged = snapshot.getValue(ChatPoc.class);
                        for (ChatPoc c : chatPocsLocos) {
                            if (chatPocChanged.getIdRemoteUser().equals(c.getIdRemoteUser())) {
                                updateChat(chatPocChanged);
                                break;
                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        ChatPoc chatPocRemoved = snapshot.getValue(ChatPoc.class);
                        for (ChatPoc c : chatPocsLocos) {
                            if (chatPocRemoved.getIdRemoteUser().equals(c.getIdRemoteUser())) {
                                removeChat(chatPocRemoved);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return root;
    }

    private void removeChat(ChatPoc chatPocRemoved) {
        Log.d(TAG, "removeChat");
//        Log.d(TAG, chatPocChanged.toString());
        int index = 0;
        for (ChatPocData cx : chatListAdapterPoc.getChats()) {
            Log.d(TAG, cx.toString());
            Log.d(TAG, cx.getIdRemoteUser().toString());
            Log.d(TAG, chatPocRemoved.getIdRemoteUser().toString());
            if (cx.getIdRemoteUser().equals(chatPocRemoved.getIdRemoteUser())) {
                chatListAdapterPoc.removeChat(index);
                break;
            }
            index++;
        }
    }

    private void updateChat(ChatPoc chatPocChanged) {
        Log.d(TAG, "updateChat");
//        Log.d(TAG, chatPocChanged.toString());
        int index = 0;
        for (ChatPocData cx : chatListAdapterPoc.getChats()) {
            Log.d(TAG, cx.toString());
            Log.d(TAG, cx.getIdRemoteUser().toString());
            Log.d(TAG, chatPocChanged.getIdRemoteUser().toString());
            if (cx.getIdRemoteUser().equals(chatPocChanged.getIdRemoteUser())) {
                cx.setLastMessageCloudPoc(chatPocChanged.getLastMessageCloudPoc());
                Log.d(TAG, "AAAAAAAA");
                Log.d(TAG, cx.toString());
                chatListAdapterPoc.updateChat(index, cx);
                break;
            }
            index++;
        }
    }

    private void filterByUsers(ArrayList<ChatPoc> chatPocArrayList) {
        chatListAdapterPoc.setChats(new ArrayList<>());
        ArrayList<ChatPocData> chatPocs = new ArrayList<>();
        for (ChatPoc cp : chatPocArrayList) {

            if (cp.getStateRemoteUser() != null) {
                if (cp.getStateRemoteUser().equals("eliminado")) {

                    ChatPocData chatPocData = new ChatPocData();
                    chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                    chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                    chatPocData.setStateRemoteUser(cp.getStateRemoteUser());
                    chatPocData.setName("Usuario" + " no " + "disponible");
                    chatPocData.setFoto(null);

//                    chatListAdapterPoc.addChat(chatPocData);

                    chatPocs.add(chatPocData);
                    chatListAdapterPoc.setChats(chatPocs);

                }
            } else {
                FirebaseDatabase.getInstance().getReference()
                        .child("administrador")
                        .child(cp.getIdRemoteUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Administrador administrador = snapshot.getValue(Administrador.class);
                                ChatPocData chatPocData = new ChatPocData();
                                chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                                chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                                chatPocData.setStateRemoteUser(cp.getStateRemoteUser());
                                if (administrador != null) {
                                    chatPocData.setName(administrador.getNombre() + " " + administrador.getApellido());
                                    chatPocData.setFoto(administrador.getFotoPerfil());
                                    Log.d(TAG, chatPocData.toString());
//                                    chatListAdapterPoc.addChat(chatPocData);
                                    chatPocs.add(chatPocData);
                                    chatListAdapterPoc.setChats(chatPocs);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                FirebaseDatabase.getInstance().getReference()
                        .child("trabajadores")
                        .child(cp.getIdRemoteUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Trabajador trabajador = snapshot.getValue(Trabajador.class);
                                ChatPocData chatPocData = new ChatPocData();
                                chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                                chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                                chatPocData.setStateRemoteUser(cp.getStateRemoteUser());
                                if (trabajador != null) {
                                    chatPocData.setName(trabajador.getNombre() + " " + trabajador.getApellido());
                                    chatPocData.setFoto(trabajador.getFotoPerfil());
                                    Log.d(TAG, chatPocData.toString());
//                                    chatListAdapterPoc.addChat(chatPocData);
                                    chatPocs.add(chatPocData);
                                    chatListAdapterPoc.setChats(chatPocs);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                FirebaseDatabase.getInstance().getReference()
                        .child("empleadores")
                        .child(cp.getIdRemoteUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Empleador empleador = snapshot.getValue(Empleador.class);
                                ChatPocData chatPocData = new ChatPocData();
                                chatPocData.setIdRemoteUser(cp.getIdRemoteUser());
                                chatPocData.setLastMessageCloudPoc(cp.getLastMessageCloudPoc());
                                chatPocData.setStateRemoteUser(cp.getStateRemoteUser());

                                if (empleador != null) {
                                    chatPocData.setName(empleador.getNombre() + " " + empleador.getApellido());
                                    chatPocData.setFoto(empleador.getFotoPerfil());
                                    Log.d(TAG, chatPocData.toString());
//                                    chatListAdapterPoc.addChat(chatPocData);
                                    chatPocs.add(chatPocData);
                                    chatListAdapterPoc.setChats(chatPocs);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        // requireActivity().startActivity(new Intent(requireActivity(), IndividualChatActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}