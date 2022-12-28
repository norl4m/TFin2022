package com.marlon.apolo.tfinal2022.ui.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientasAsíncronas.DeleteAsyncTask;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.model.UsuarioFirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AuthUserListAdapter extends
        RecyclerView.Adapter<AuthUserListAdapter.AuthUserViewHolder> {

    private static final String TAG = AuthUserListAdapter.class.getSimpleName();
    private final Context contextInstance;
    private ArrayList<UsuarioFirebaseAuth> mWordList;
    private final LayoutInflater mInflater;
    private AlertDialog alertDialogVar;

    private List<Trabajador> trabajadorArrayList;
    private List<Empleador> empleadorArrayList;

    public List<Trabajador> getTrabajadorArrayList() {
        return trabajadorArrayList;
    }

    public void setTrabajadorArrayList(List<Trabajador> trabajadorArrayList) {
        this.trabajadorArrayList = trabajadorArrayList;
    }

    public List<Empleador> getEmpleadorArrayList() {
        return empleadorArrayList;
    }

    public void setEmpleadorArrayList(List<Empleador> empleadorArrayList) {
        this.empleadorArrayList = empleadorArrayList;
    }

    class AuthUserViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView textViewUid;
        public final TextView textViewEmail;
        public final TextView textViewPhone;
        public final ImageButton imageButtonDelete;
        public final AuthUserListAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter  The adapter that manages the the data and views
         *                 for the RecyclerView.
         */
        public AuthUserViewHolder(View itemView, AuthUserListAdapter adapter) {
            super(itemView);
            textViewUid = itemView.findViewById(R.id.uid);
            textViewEmail = itemView.findViewById(R.id.email);
            textViewPhone = itemView.findViewById(R.id.phone);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
            this.mAdapter = adapter;
//            itemView.setOnClickListener(this);
            imageButtonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();

            // Use that to access the affected item in mWordList.
            UsuarioFirebaseAuth element = mWordList.get(mPosition);
            // Change the word in the mWordList.

//            mWordList.set(mPosition, "Clicked! " + element);
            // Notify the adapter, that the data has changed so it can
            // update the RecyclerView to display the data.
//            mAdapter.notifyDataSetChanged();

            alertDialogConfirmar(element, mPosition, mAdapter).show();
//            mWordList.remove(mPosition);
//            mAdapter.notifyItemRemoved(mPosition);


        }
    }

    public android.app.AlertDialog alertDialogConfirmar(UsuarioFirebaseAuth usuarioFirebaseAuth, int pos, AuthUserListAdapter adapter) {

        return new android.app.AlertDialog.Builder(contextInstance)
                .setIcon(R.drawable.ic_oficios)
                .setTitle("Eliminar trabajador:")
                .setMessage("¿Está seguro que desea eliminar la siguiente cuenta de usuario?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*aSYNC operation*/
                        String title = "Por favor espere ";
//        message = "Cachuelito se encuentra verificando su información personal..." + "(Rev)";
                        String message = "Cachuelito se encuentra eliminando los registros de información del usuario...";

                        showCustomProgressDialog(title, message);

                        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(usuarioFirebaseAuth.getUid(), contextInstance);
                        deleteAsyncTask.execute();
                        deleteAsyncTask.setOnListenerAsyncTask(new DeleteAsyncTask.ClickListener() {
                            @Override
                            public void onTokenListener(String publicKey) {
                                if (publicKey.equals("1")) {
                                    try {
                                        Usuario usuariodel = usuarioFirebaseAuth.getExtraUsuarioLol();
//                                        if (usuariodel instanceof Trabajador) {
//
//                                        }
//                                        if (usuariodel instanceof Empleador) {
                                        String idUsuario = usuariodel.getIdUsuario();
                                        usuariodel.setDeleteUserOnFirebase(idUsuario);
                                        usuariodel.eliminarInfo((Activity) contextInstance);
                                        closeCustomAlertDialog();
                                        usuariodel.cleanFirebaseDeleteUser(idUsuario);
//                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
//                                    trabajador.setDeleteUserOnFirebase(idUsuario);
//                                    trabajador.eliminarInfo((Activity) context);
                                    closeCustomAlertDialog();
                                    mWordList.remove(pos);
                                    adapter.notifyItemRemoved(pos);

//                                    trabajador.cleanFirebaseDeleteUser(idUsuario);
                                } else {
                                    Toast.makeText(contextInstance, contextInstance.getString(R.string.error_inesperado), Toast.LENGTH_LONG).show();
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
//            Log.d(TAG, e.toString());
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

    public AuthUserListAdapter(Context context) {
        contextInstance = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Associates a list of words with this adapter
     */
    void setFirebaseUsers(ArrayList<UsuarioFirebaseAuth> words) {
        mWordList = words;
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after
     *                 it is bound to an adapter position.
     * @param viewType The view type of the new View. @return A new ViewHolder
     *                 that holds a View of the given view type.
     */
    @Override
    public AuthUserListAdapter.AuthUserViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(
                R.layout.auth_user_list_item, parent, false);
        return new AuthUserViewHolder(mItemView, this);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent
     *                 the contents of the item at the given position in the
     *                 data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(AuthUserListAdapter.AuthUserViewHolder holder,
                                 int position) {
        // Retrieve the data for that position.
        UsuarioFirebaseAuth mCurrent = mWordList.get(position);
        // Add the data to the view holder.
        holder.textViewUid.setText(String.format("UID: %s", mCurrent.getUid()));
        holder.textViewEmail.setText(String.format("EMAIL: %s", mCurrent.getEmail()));
        holder.textViewPhone.setText(String.format("CELULAR: %s", mCurrent.getPhoneNumber()));
        Drawable[] itrems = new Drawable[]{AppCompatResources.getDrawable(contextInstance, R.drawable.bg4),
                AppCompatResources.getDrawable(contextInstance, R.drawable.bg5),
                AppCompatResources.getDrawable(contextInstance, R.drawable.bg6)};
        try {
            switch (mCurrent.getExtraLol()) {
                case "si":
                    holder.itemView.setBackground(itrems[0]);
                    break;
                case "trabajador":
                    holder.itemView.setBackground(itrems[1]);
                    break;
                case "empleador":
                    holder.itemView.setBackground(itrems[2]);
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mWordList != null)
            return mWordList.size();
        else return 0;
    }

}
