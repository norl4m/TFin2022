package com.marlon.apolo.tfinal2022.ui.bienvenido;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioTrabajadorVistaListAdapter;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorListAdapter;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import java.util.ArrayList;
import java.util.List;

public class BienvenidoTrabajadorListAdapter extends RecyclerView.Adapter<BienvenidoTrabajadorListAdapter.TrabajadorViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Trabajador> trabajadors;
    private String TAG;
    private Dialog dialogVar;

    public void setUsuarioFrom(Usuario usuarioFrom) {
        this.usuarioFrom = usuarioFrom;
    }

    private Usuario usuarioFrom;

    public BienvenidoTrabajadorListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public BienvenidoTrabajadorListAdapter.TrabajadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.card_view_presentacion_trabajador, parent, false);
        return new BienvenidoTrabajadorListAdapter.TrabajadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BienvenidoTrabajadorListAdapter.TrabajadorViewHolder holder, int position) {
        Trabajador current = trabajadors.get(position);
        holder.textViewNombre.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
        if (current.getFotoPerfil() != null) {
            Glide.with(context).load(current.getFotoPerfil()).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewTrabajador);
        }


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




    public class TrabajadorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombre;
        private final TextView textViewCalif;
        private final RecyclerView recyclerViewOficios;
        private final ImageView imageViewTrabajador;
        private RatingBar ratingBar;
        private final TextView textViewContacto;


        public TrabajadorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCalif = itemView.findViewById(R.id.textViewCalificacion);
            recyclerViewOficios = itemView.findViewById(R.id.recyclerViewOficios);
            imageViewTrabajador = itemView.findViewById(R.id.imageViewTrabajador);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);
            imageViewTrabajador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
                        int usuario = myPreferences.getInt("usuario", -1);

                        switch (usuario) {
                            case 0:/*admin*/
                            case 1:/*empleador*/
                                opcionesTrabajadorDialog(trabajadors.get(getAdapterPosition()));
                                break;
                            case 2:
                                break;
                        }
                    } else {
                        alertDialogInfo();
                    }
                    //opcionesTrabajadorDialog(trabajadors.get(getAdapterPosition()));

                }
            });


        }
    }


    public void opcionesTrabajadorDialog(Trabajador trabajador) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);


        View promptsView = inflater.inflate(R.layout.alert_dialog_opciones_trabajadores, null);


        // set prompts.xml to alertdialog builder
        builder.setView(promptsView);

        final ImageView imageButtonMessage = promptsView.findViewById(R.id.imageViewMessage);
        final ImageView imageButtonCall = promptsView.findViewById(R.id.imageViewCall);
        final ImageView imageButtonVideoCall = promptsView.findViewById(R.id.imageViewVideoCall);
        final ImageView imageButtonInfo = promptsView.findViewById(R.id.imageViewInfo);
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean mode = mPrefs.getBoolean("sync_theme", false);
        if (mode) {
            imageButtonMessage.setColorFilter(context.getResources().getColor(R.color.white));
            imageButtonCall.setColorFilter(context.getResources().getColor(R.color.white));
            imageButtonVideoCall.setColorFilter(context.getResources().getColor(R.color.white));
            imageButtonInfo.setColorFilter(context.getResources().getColor(R.color.white));
        } else {

        }

        final ImageView imageView = promptsView.findViewById(R.id.imageViewTrabajador);
        final TextView textView = promptsView.findViewById(R.id.textViewNombreUsuario);
//

        textView.setText(String.format("%s %s", trabajador.getNombre(), trabajador.getApellido()));
        if (trabajador.getFotoPerfil() != null) {

//            Bitmap bitmap = procesamientoDeImagen.stringToBitMap(trabajador.getFotoPerfil());
//            imageView.setImageBitmap(bitmap);


//            Glide
//                    .with(requireActivity())
//                    .load(trabajador.getFotoPerfil())
//                    .placeholder(R.drawable.ic_baseline_person_24)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .into(imageView);

            Glide
                    .with(context)
                    .load(trabajador.getFotoPerfil())
//                    .circleCrop() /*mala idea*/
                    .apply(new RequestOptions().override(300, 400))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);


        } else {
            imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
        //imageButtonImages.setOnClickListener(clickListenerDialogCustom);
        //imageButtonCall.setOnClickListener(clickListenerDialogCustom);
//        imageButtonAudio.setOnClickListener(clickListenerDialogCustom);


        imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, IndividualChatActivity.class);
                intent.putExtra("trabajador", trabajador);
                //intent.putExtra("usuarioFrom", usuarioFrom);
                //Toast.makeText(context, usuarioFrom.toString(), Toast.LENGTH_SHORT).show();

                int exitFlag = 0;


                //intent.putExtra("trabajador", trabajador);

                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }

            }
        });

        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, LlamadaVozActivity.class);
                intent.putExtra("usuarioTo", (Usuario) trabajador);
//                intent.putExtra("usuarioFrom", usuarioLocal);
                intent.putExtra("callStatus", 0
                );
                context.startActivity(intent);

                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Video Call", Toast.LENGTH_SHORT).show();
                Intent intentVideollamada = new Intent(context, VideoLlamadaActivity.class);
                intentVideollamada.putExtra("usuarioTo", (Usuario) trabajador);
//                intentVideollamada.putExtra("usuarioFrom", usuarioLocal);
                intentVideollamada.putExtra("callStatus", 0);
                context.startActivity(intentVideollamada);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });

        imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Info", Toast.LENGTH_SHORT).show();
//                Intent intentVideoLlamada = new Intent(context, VideoLlamadaActivity.class);
//                context.startActivity(intentVideoLlamada);
                try {
                    dialogVar.dismiss();
                } catch (Exception e) {

                }
            }
        });


//        return builder.create();
        dialogVar = builder.create();
        dialogVar.show();
    }


    public void alertDialogInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View promptsView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(promptsView);
//
//        // set prompts.xml to alertdialog builder
        final TextView textViewInfo = promptsView.findViewById(R.id.textViewInfo);
        textViewInfo.setText(context.getResources().getString(R.string.text_select_trabjador_no_login));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                try {
                    dialog.dismiss();
                } catch (Exception e) {

                }
            }
        });
        dialogVar = builder.create();
        dialogVar.show();
    }

}
