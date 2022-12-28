package com.marlon.apolo.tfinal2022.ui.oficioArchi.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.registro.view.RegWithEmailPasswordActivity;
import com.marlon.apolo.tfinal2022.registro.view.regMethod.RegistrarseConCelularActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.model.OficioArchiModel;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.NuevoOficioArchiActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.OficioArchiActivity;
import com.marlon.apolo.tfinal2022.ui.oficioArchi.view.OficioArchiEditDeleteActivity;

import java.util.ArrayList;
import java.util.List;

public class OficioArchiRepository {

    private static final String TAG = OficioArchiRepository.class.getSimpleName();
    private static final String OFICIO_REF_ON_FIREBASE = "oficios";

    // [START declare_database_ref]
    private DatabaseReference mDatabase; /*TIPO interface DAO O LINQ, MAPEO*/
    // [END declare_database_ref]


    private LiveData<List<OficioArchiModel>> allOficios;
    private MutableLiveData<List<OficioArchiModel>> allOficiosAux;
    private MutableLiveData<Integer> totalSize;


    public OficioArchiRepository() {
        Log.d(TAG, "inicializando... (OficioArchiRepository)");
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        loadCountOficios();
        // [END initialize_database_ref]    }
        allOficios = getAllOficiosAux();
    }

    public LiveData<List<OficioArchiModel>> getAllOficios() {
        return allOficios;
    }

    public MutableLiveData<Integer> getTotalSize() {
        if (totalSize == null) {
            totalSize = new MutableLiveData<>();
            loadCountOficios();
        }
        return totalSize;
    }

    public void writeNewOficioWithTaskListeners(Oficio oficioArchiModel, NuevoOficioArchiActivity nuevoOficioArchiActivity, ProgressDialog progressDialog) {

        boolean photoFlag = false;

        if (oficioArchiModel.getUriPhoto() != null) {
            Uri returnUri = Uri.parse(oficioArchiModel.getUriPhoto().toString());
            Cursor returnCursor = nuevoOficioArchiActivity.getContentResolver().query(returnUri, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            Log.d(TAG, returnCursor.getString(nameIndex));
            Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

            if (returnCursor.getLong(sizeIndex) > 0) {
                //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                photoFlag = true;
            } else {
                //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                photoFlag = false;
            }
        } else {
            photoFlag = false;
        }

        oficioArchiModel.setIdOficio(mDatabase.child(OFICIO_REF_ON_FIREBASE).push().getKey());
        // [START rtdb_write_new_user_task]
        boolean finalPhotoFlag = photoFlag;

        if (!finalPhotoFlag) {
            writeOnFirebase(oficioArchiModel, nuevoOficioArchiActivity, progressDialog);

        } else {

            Log.e(TAG, "REGISTRANDO FOTO");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            String baseReference = "gs://tfinal2022-afc91.appspot.com";
            String imagePath = baseReference + "/" + OFICIO_REF_ON_FIREBASE + "/" + oficioArchiModel.getIdOficio() + "/" + "fotoPerfil.jpg";
            Log.d(TAG, "Path reference on fireStorage");
            StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

            UploadTask uploadTask = storageRef.putFile(Uri.parse(oficioArchiModel.getUriPhoto()), metadata);

            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");

            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "on failure Foto complete...");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    // ...
                    Log.d(TAG, "Upload is complete...");
                    //  registroActivity.limpiarUI();

                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        oficioArchiModel.setUriPhoto(downloadUri.toString());
                        writeOnFirebase(oficioArchiModel, nuevoOficioArchiActivity, progressDialog);

                    } else {
                        // Handle failures
                        Log.d(TAG, "Registro de oficio con foto fallido");
                    }
                }
            });
        }


    }


    public void writeOnFirebase(Oficio oficio, NuevoOficioArchiActivity nuevoOficioArchiActivity, ProgressDialog progressDialog) {
        mDatabase.child(OFICIO_REF_ON_FIREBASE)
                .child(oficio.getIdOficio())
                .setValue(oficio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                        Log.d(TAG, "Oficio registrado existosamente");
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        nuevoOficioArchiActivity.getTextInputLayoutNombre().getEditText().setText("");
                        Glide.with(nuevoOficioArchiActivity)
                                .load(R.drawable.ic_oficios)
                                .into(nuevoOficioArchiActivity.getImageViewIcono());

                        Toast.makeText(nuevoOficioArchiActivity, "Oficio registrado existosamente.", Toast.LENGTH_LONG).show();

                        nuevoOficioArchiActivity.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Log.d(TAG, "Registro de oficio fallido");
                    }
                });
        // [END rtdb_write_new_user_task]


    }

    public void deleteOficio(OficioArchiModel oficioArchiModel, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
        mDatabase.child(OFICIO_REF_ON_FIREBASE)
                .child(oficioArchiModel.getIdOficio())
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                        Log.d(TAG, "Oficio eliminado existosamente");
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
//                        nuevoOficioArchiActivity.getTextInputLayoutNombre().getEditText().setText("");
//                        Glide.with(nuevoOficioArchiActivity)
//                                .load(R.drawable.ic_oficios)
//                                .into(nuevoOficioArchiActivity.getImageViewIcono());

                        Toast.makeText(oficioArchiEditDeleteActivity, "Oficio eliminado.", Toast.LENGTH_LONG).show();

                        oficioArchiEditDeleteActivity.finish();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Log.d(TAG, "Eliminación de oficio fallido");
                    }
                });
        // [END rtdb_write_new_user_task]

    }

    public void updateOficio(Oficio oficio, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
//        mDatabase.child(OFICIO_REF_ON_FIREBASE)
//                .child(oficio.getIdOficio())
//                .setValue(oficio)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Write was successful!
//                        // ...
//                        Log.d(TAG, "Oficio actualizado existosamente");
//                        try {
//                            progressDialog.dismiss();
//                        } catch (Exception e) {
//                            Log.d(TAG, e.toString());
//                        }
////                        nuevoOficioArchiActivity.getTextInputLayoutNombre().getEditText().setText("");
////                        Glide.with(nuevoOficioArchiActivity)
////                                .load(R.drawable.ic_oficios)
////                                .into(nuevoOficioArchiActivity.getImageViewIcono());
//
//                        Toast.makeText(oficioArchiEditDeleteActivity, "Oficio actualizado.", Toast.LENGTH_LONG).show();
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Write failed
//                        // ...
//                        Log.d(TAG, "Actualización de oficio fallido");
//                    }
//                });
//        // [END rtdb_write_new_user_task]
//

        boolean photoFlag = false;

        if (oficio.getUriPhoto() != null) {
            Uri returnUri = Uri.parse(oficio.getUriPhoto().toString());
            Cursor returnCursor = oficioArchiEditDeleteActivity.getContentResolver().query(returnUri, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */

            if (oficio.getUriPhoto().contains("https://")) {
                photoFlag = false;
            } else {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                Log.d(TAG, returnCursor.getString(nameIndex));
                Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));

                if (returnCursor.getLong(sizeIndex) > 0) {
                    //Toast.makeText(getApplicationContext(), "Registro con foto", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.format("Tamaño de archivo: %s", Long.toString(returnCursor.getLong(sizeIndex))));
                    photoFlag = true;
                } else {
                    //Toast.makeText(getApplicationContext(), "Registro normal", Toast.LENGTH_SHORT).show();
                    photoFlag = false;
                }
            }

        } else {
            photoFlag = false;
        }

        // [START rtdb_write_new_user_task]
        boolean finalPhotoFlag = photoFlag;

        if (!finalPhotoFlag) {
            mCRUDOficioOnFirebase(oficio, oficioArchiEditDeleteActivity, progressDialog);
            // [END rtdb_write_new_user_task]

        } else {

            Log.e(TAG, "REGISTRANDO FOTO");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            String baseReference = "gs://tfinal2022-afc91.appspot.com";
            String imagePath = baseReference + "/" + OFICIO_REF_ON_FIREBASE + "/" + oficio.getIdOficio() + "/" + "fotoPerfil.jpg";
            Log.d(TAG, "Path reference on fireStorage");
            StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

            UploadTask uploadTask = storageRef.putFile(Uri.parse(oficio.getUriPhoto()), metadata);

            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");

            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "on failure Foto complete...");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    // ...
                    Log.d(TAG, "Upload is complete...");
                    //  registroActivity.limpiarUI();

                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        oficio.setUriPhoto(downloadUri.toString());
                        mCRUDOficioOnFirebase(oficio, oficioArchiEditDeleteActivity, progressDialog);


                    } else {
                        // Handle failures
                        Log.d(TAG, "Registro de oficio con foto fallido");
                    }
                }
            });
        }


    }

    public void mCRUDOficioOnFirebase(Oficio oficio, OficioArchiEditDeleteActivity oficioArchiEditDeleteActivity, ProgressDialog progressDialog) {
        mDatabase
                .child(OFICIO_REF_ON_FIREBASE)
                .child(oficio.getIdOficio())
                .setValue(oficio).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                                        Log.d(TAG, "Oficios registrado con foto existosamente");
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }


                            Toast.makeText(oficioArchiEditDeleteActivity, "Oficio actualizado.", Toast.LENGTH_LONG).show();
                            oficioArchiEditDeleteActivity.setUriPhoto(null);

                        } else {
                            Log.d(TAG, "Registro de oficio con foto fallido");

                        }
                    }
                });
    }


    public LiveData<List<OficioArchiModel>> getAllOficiosAux() {
        if (allOficiosAux == null) {
            allOficiosAux = new MutableLiveData<>();
            loadAllOficios();
        }
        return allOficiosAux;
    }

    private void loadCountOficios() {
        mDatabase.child(OFICIO_REF_ON_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
//                totalSize = (int) dataSnapshot.getChildrenCount();
//                Log.d(TAG, String.valueOf(totalSize));
                totalSize.setValue(size);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
    }

    private void loadAllOficios() {
        // [START oficio_value_event_listener]
        ValueEventListener oficioListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<OficioArchiModel> oficioArchiModels = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // Get Oficio object and use the values to update the UI
                    OficioArchiModel oficioArchiModel = data.getValue(OficioArchiModel.class);
                    // ..
                    try {
                        Log.d(TAG, oficioArchiModel.toString());
                        oficioArchiModels.add(oficioArchiModel);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
                allOficiosAux.setValue(oficioArchiModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Oficio failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(OFICIO_REF_ON_FIREBASE).addValueEventListener(oficioListener);
        // [END oficio_value_event_listener]
    }

}
