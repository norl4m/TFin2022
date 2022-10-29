package com.marlon.apolo.tfinal2022.ui.trabajadores;

import static com.google.common.net.HttpHeaders.USER_AGENT;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coloros.ocs.base.a.e;
import com.google.firebase.auth.FirebaseAuth;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.DeleteAsyncTask;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.GetAsyncTask;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.PublicKeyAsyncTask;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioTrabajadorVistaListAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;

public class TrabajadorCRUDListAdapter extends RecyclerView.Adapter<TrabajadorCRUDListAdapter.TrabajadorViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Trabajador> trabajadors;
    private List<Oficio> oficioList;
    private String TAG;
    private AlertDialog alertDialogVar;

    public TrabajadorCRUDListAdapter(Context contextVar) {
        context = contextVar;
        inflater = LayoutInflater.from(context);
    }

    public TrabajadorCRUDListAdapter(Context contextVar, ArrayList<Oficio> oficioArrayList) {
        context = contextVar;
        inflater = LayoutInflater.from(context);
        oficioList = oficioArrayList;
    }

    public List<Oficio> getOficioList() {
        return oficioList;
    }

    @NonNull
    @Override
    public TrabajadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_crud_trabajador, parent, false);
        return new TrabajadorCRUDListAdapter.TrabajadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrabajadorViewHolder holder, int position) {
        Trabajador current = trabajadors.get(position);
        holder.textViewNombre.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
        Log.d(TAG, current.toString());
        if (current.getFotoPerfil() != null) {
            Glide.with(context)
                    .load(current.getFotoPerfil())
                    .apply(new RequestOptions().override(150, 150))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .circleCrop()
                    .into(holder.imageViewTrabajador);
        } else {
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24)).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewTrabajador);
        }


        ArrayList<Oficio> oficiosFiltrados = new ArrayList<>();
        for (Oficio o : oficioList) {
            if (current.getIdOficios().contains(o.getIdOficio())) {
//                ArrayList<Habilidad> habilidadsFiltradas = new ArrayList<>();
//                ArrayList<Habilidad> habilidads = new ArrayList<>();
//                habilidads = o.getHabilidadArrayList();
//                o.setHabilidadArrayList(new ArrayList<>());
//                try {
//                    for (Habilidad h : habilidads) {
//                        if (current.getIdHabilidades().contains(h.getIdHabilidad())) {
//                            habilidadsFiltradas.add(h);
//                        }
//                    }
//                    o.setHabilidadArrayList(habilidadsFiltradas);
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
                oficiosFiltrados.add(o);
            }
        }


        OficioTrabajadorVistaListAdapter oficioTrabajadorVistaListAdapter = new OficioTrabajadorVistaListAdapter(context);
        holder.recyclerViewOficios.setAdapter(oficioTrabajadorVistaListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        holder.recyclerViewOficios.setLayoutManager(layoutManager);
        oficioTrabajadorVistaListAdapter.setOficios(oficiosFiltrados);
//        oficioTrabajadorVistaListAdapter.setOficios(oficioList);
//        oficioViewModel.getAllOficios().observe((LifecycleOwner) context, oficioRegistroListAdapter::setOficios);

        holder.textViewCalif.setText(String.format("Calificación: %.1f " + "/ 5.0", current.getCalificacion()));
        holder.ratingBar.setRating((float) current.getCalificacion());

        if (current.getEmail() != null) {
            holder.textViewContacto.setText(current.getEmail());
        }
        if (current.getCelular() != null) {
            holder.textViewContacto.setText(current.getCelular());
        }

    }

    @Override
    public int getItemCount() {
        if (trabajadors != null)
            return trabajadors.size();
        else return 0;
    }

    public List<Trabajador> getTrabajadors() {
        return trabajadors;
    }

    public void setTrabajadores(List<Trabajador> trabajadorsVar) {
        trabajadors = trabajadorsVar;
        notifyDataSetChanged();
    }

    public String cipherMessageWithRSA2048(String publicKeyString, String message) {
        try {
            // 4. get public key
//            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

            Log.d(TAG, "*********************************************");
            Log.d(TAG, "Iniciando proceso de encriptación");
            Log.d(TAG, "Mensaje: " + message);
            X509EncodedKeySpec publicSpec = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                publicSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
            }
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicSpec);
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            Log.d(TAG, "Encriptando mensaje...");
            byte[] encryptedMessageBytes = encryptCipher.doFinal(messageBytes);
            Log.d(TAG, "Mensaje encriptado: " + new String(encryptedMessageBytes, StandardCharsets.UTF_8));
            String encodedMessage = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
                Log.d(TAG, "Mensaje Base64: " + encodedMessage);
            }


            Log.d(TAG, "Message: " + message);
            Log.d(TAG, "Encode Base64 Message: " + encodedMessage);
            //System.out.println("Decrypted Message: " + decryptedMessage);
            Log.d(TAG, "*********************************************");
            return encodedMessage;
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            Log.d(TAG, "*********************************************");
            return "Error: " + e.toString();

        }
    }

    private static void sendGET(String message) throws IOException {
        Log.d("TAG", "Send GET REQUEST");
        String urlBase = "https://authwitouthauth.herokuapp.com";
        String GET_URL = urlBase + "/damin/cipher";

        /*URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }*/

        URL url = new URL(GET_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
//        conn.setRequestMethod("POST");
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("message", "Hello world of cryptography")
//                .appendQueryParameter("secondParam", paramValue2)
//                .appendQueryParameter("thirdParam", paramValue3)
                ;
        String query = builder.build().getEncodedQuery();

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();

        conn.connect();

        InputStream in = conn.getInputStream();
        // 3. Download and decode the string response using builder
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            Log.d("TAG", line);
        }

    }

    public class TrabajadorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewCalif;
        private final RecyclerView recyclerViewOficios;
        private final ImageView imageViewTrabajador;
        private RatingBar ratingBar;
        private final TextView textViewContacto;
        private ImageButton imageButtonEdit;
        private ImageButton imageButtonDelete;


        public TrabajadorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCalif = itemView.findViewById(R.id.textViewCalificacion);
            recyclerViewOficios = itemView.findViewById(R.id.recyclerViewOficios);
            imageViewTrabajador = itemView.findViewById(R.id.imageViewTrabajador);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);

            imageButtonEdit = itemView.findViewById(R.id.imageButtonEdit);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);

            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Editar", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, EditarDataActivity.class);
                    intent.putExtra("trabajador", trabajadors.get(getAdapterPosition()));
                    intent.putExtra("usuario", 2);
                    context.startActivity(intent);
                }
            });

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "Eliminar", Toast.LENGTH_LONG).show();
                    alertDialogConfirmar(trabajadors.get(getAdapterPosition())).show();

//                    PublicKeyAsyncTask publicKeyAsyncTask = new PublicKeyAsyncTask();
//                    publicKeyAsyncTask.execute();
//                    publicKeyAsyncTask.setOnListenerAsyncTask(publicKey -> {
//                        Log.d(TAG, "PUBLIC KEY: " + publicKey);
//                        String plainText = "Hello world of cryptography";
//                        String encodeMessage = cipherMessageWithRSA2048(publicKey, plainText);
////                        cipherMessageWithRSA2048(publicKey, plainText);
//
////                        GetAsyncTask getAsyncTask = new GetAsyncTask(plainText);
//                        GetAsyncTask getAsyncTask = new GetAsyncTask(encodeMessage);
//                        getAsyncTask.execute();
////                        try {
////                            sendGET("Message");
////                        } catch (Exception e) {
////                            Log.d(TAG, e.toString());
////                        }
//
//
//                    });


                    /*String text = "Hola mundo de la criptografìa";
                    byte[] plaintext = text.getBytes();
                    try {
                        KeyGenerator keygen = KeyGenerator.getInstance("AES");
                        keygen.init(256);
                        SecretKey key = keygen.generateKey();
                        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                        byte[] ciphertext = cipher.doFinal(plaintext);
                        byte[] iv = cipher.getIV();
                        Log.d(TAG, "plain text: " + new String(plaintext, "UTF8"));
                        Log.d(TAG, "cipher text: " + new String(ciphertext, "UTF8"));
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }*/

                    /*String getToken = urlBase + "/rtc/" + channelNameShare + "/publisher/uid/" + uid + "/";

                    try {
                        // 1. Declare a URL Connection
                        URL url = new URL(getToken);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        // 2. Open InputStream to connection
                        conn.connect();
                        InputStream in = conn.getInputStream();
                        // 3. Download and decode the string response using builder
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            //Log.d(TAG, line);
                        }
                        tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2, stringBuilder.length() - 2);
//            tokenLocal = stringBuilder.substring(stringBuilder.indexOf(":") + 2);
//            tokenLocal = stringBuilder.toString();
                        Log.d(TAG, tokenLocal);
                        Log.d(TAG, String.valueOf(uidLocal));
                        Log.d(TAG, uid);
//            this.channelNameShare =tokenLocal;
                        //joinChannel();

                    } catch (Exception e) {

                    }*/


                }
            });
        }
    }


    public android.app.AlertDialog alertDialogConfirmar(Trabajador trabajador) {

        return new android.app.AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Eliminar trabajador:")
                .setMessage("¿Está seguro que desea eliminar la información del trabajador?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*aSYNC operation*/
                        String title = "Por favor espere ";
//        message = "Cachuelito se encuentra verificando su información personal..." + "(Rev)";
                        String message = "Cachuelito se encuentra eliminando los registros de información del trabajador...";

                        showCustomProgressDialog(title, message);

                        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(trabajador.getIdUsuario(), context);
                        deleteAsyncTask.execute();
                        deleteAsyncTask.setOnListenerAsyncTask(new DeleteAsyncTask.ClickListener() {
                            @Override
                            public void onTokenListener(String publicKey) {
                                if (publicKey.equals("1")) {
                                    String idUsuario = trabajador.getIdUsuario();
                                    trabajador.setDeleteUserOnFirebase(idUsuario);
                                    trabajador.eliminarInfo((Activity) context);
                                    closeCustomAlertDialog();
                                    trabajador.cleanFirebaseDeleteUser(idUsuario);
                                } else {
                                    Toast.makeText(context, context.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                    closeCustomAlertDialog();
                                }
                            }
                        });


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).create();
    }

    public void showCustomProgressDialog(String title, String message) {
        try {
            closeCustomAlertDialog();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
//        LayoutInflater inflater = this.getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(context);
        View promptsView = inflater.inflate(R.layout.custom_progress_dialog, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
//        return builder.create();
        final TextView textViewTitle = promptsView.findViewById(R.id.textViewTitle);
        final TextView textViewMessage = promptsView.findViewById(R.id.textViewMessage);

        textViewTitle.setText(title);
        textViewMessage.setText(message);

        alertDialogVar = builder.create();
        alertDialogVar.show();
//        builder.show();
    }


    public void closeCustomAlertDialog() {
        try {
            alertDialogVar.dismiss();
        } catch (Exception e) {

        }
    }


    public void setOficioList(List<Oficio> oficioList) {
        this.oficioList = oficioList;
    }
}
