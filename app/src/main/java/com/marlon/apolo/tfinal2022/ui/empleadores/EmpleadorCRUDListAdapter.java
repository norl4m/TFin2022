package com.marlon.apolo.tfinal2022.ui.empleadores;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.DeleteAsyncTask;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.editarDatos.EditarDataActivity;

import java.util.List;

public class EmpleadorCRUDListAdapter extends RecyclerView.Adapter<EmpleadorCRUDListAdapter.EmpleadorViewHolder> {
    private static final String TAG = EmpleadorCRUDListAdapter.class.getSimpleName();
    private final Context contextInstance;
    private final LayoutInflater mInflater;
    private List<Empleador> mEmpleadores; // Cached copy of words
    private Dialog alertDialogVar;

    public EmpleadorCRUDListAdapter(Context context) {
        contextInstance = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public EmpleadorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_crud_empleador, parent, false);
        return new EmpleadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmpleadorViewHolder holder, int position) {
        Empleador current = mEmpleadores.get(position);
        holder.textViewNombreUsuario.setText(String.format("%s %s", current.getNombre(), current.getApellido()));
        if (current.getEmail() != null) {
            holder.textViewContacto.setText(current.getEmail());
        }
        if (current.getCelular() != null) {
            holder.textViewContacto.setText(current.getCelular());
        }
        if (current.getFotoPerfil() != null) {
            Glide.with(contextInstance)
                    .load(current.getFotoPerfil())
                    .apply(new RequestOptions().override(150, 150))
                    .placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewUsuario);
        } else {
            Glide.with(contextInstance).load(ContextCompat.getDrawable(contextInstance, R.drawable.ic_baseline_person_24)).placeholder(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imageViewUsuario);
        }

    }

    public void setEmpleadores(List<Empleador> empleadores) {
        mEmpleadores = empleadores;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mEmpleadores != null)
            return mEmpleadores.size();
        else return 0;
    }

    public class EmpleadorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNombreUsuario;
        private final TextView textViewContacto;
        private final ImageView imageViewUsuario;
        private ImageButton imageButtonEdit;
        private ImageButton imageButtonDelete;

        private EmpleadorViewHolder(View itemView) {
            super(itemView);
            textViewNombreUsuario = itemView.findViewById(R.id.textViewNombreUsuario);
            textViewContacto = itemView.findViewById(R.id.textViewContacto);
            imageViewUsuario = itemView.findViewById(R.id.imageViewUser);

            imageButtonEdit = itemView.findViewById(R.id.imageButtonEdit);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);

            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(contextInstance, "Editar", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(contextInstance, EditarDataActivity.class);
                    intent.putExtra("empleador", mEmpleadores.get(getAdapterPosition()));
                    intent.putExtra("usuario", 1);
                    contextInstance.startActivity(intent);
                }
            });

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(contextInstance, "Eliminar", Toast.LENGTH_LONG).show();
                    alertDialogConfirmar(mEmpleadores.get(getAdapterPosition())).show();
                }
            });
        }
    }


    public android.app.AlertDialog alertDialogConfirmar(Empleador empleador) {

        return new android.app.AlertDialog.Builder(contextInstance)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Eliminar empleador:")
                .setMessage("¿Está seguro que desea eliminar la información del empleador?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        /*aSYNC operation*/
                        String title = "Por favor espere ";
//        message = "Cachuelito se encuentra verificando su información personal..." + "(Rev)";
                        String message = "Cachuelito se encuentra eliminando los registros de información del empleador...";

                        showCustomProgressDialog(title, message);

                        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(empleador.getIdUsuario(), contextInstance);
                        deleteAsyncTask.execute();
                        deleteAsyncTask.setOnListenerAsyncTask(new DeleteAsyncTask.ClickListener() {
                            @Override
                            public void onTokenListener(String publicKey) {
                                if (publicKey.equals("1")) {
                                    empleador.eliminarInfo((Activity) contextInstance);
                                    closeCustomAlertDialog();
                                } else {
                                    Toast.makeText(contextInstance, contextInstance.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
                                    closeCustomAlertDialog();
                                }
                            }
                        });

                        //empleador.eliminarInfo((Activity) contextInstance);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(contextInstance);
        // Get the layout inflater
//        LayoutInflater inflater = this.getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(contextInstance);
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


}
