package com.marlon.apolo.tfinal2022.ui.bienvenido;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import java.util.ArrayList;

public class BienvenidoRepository {
    private static final String TAG = BienvenidoRepository.class.getSimpleName();
    MutableLiveData<ArrayList<Trabajador>> allTrabajadores;
    MutableLiveData<ArrayList<Empleador>> allEmpleadores;
    MutableLiveData<ArrayList<Oficio>> allOficios;
    MutableLiveData<ArrayList<Trabajador>> trabajadoresByEmail;
    MutableLiveData<ArrayList<Trabajador>> trabajadoresByPhone;
    MutableLiveData<ArrayList<Empleador>> empleadoresByEmail;
    MutableLiveData<ArrayList<Empleador>> empleadoresByPhone;


    public BienvenidoRepository() {
    }

    public MutableLiveData<ArrayList<Trabajador>> getAllTrabajadores() {
        if (allTrabajadores == null) {
            allTrabajadores = new MutableLiveData<>();
            loadAllTrabajadores();
        }
        return allTrabajadores;
    }

    private void loadAllTrabajadores() {
        ArrayList<Trabajador> trabajadorArrayList = new ArrayList<>();
        ChildEventListener childEventListenerTrabajadores = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "loadAllTrabajadores-onChildAdded");
                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    trabajadorArrayList.add(trabajador);
                    allTrabajadores.setValue(trabajadorArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "loadAllTrabajadores-onChildChanged");

                try {
                    Trabajador trabajadorChanged = snapshot.getValue(Trabajador.class);
                    int index = 0;
                    for (Trabajador tr : trabajadorArrayList) {
                        if (tr.getIdUsuario().equals(trabajadorChanged.getIdUsuario())) {
                            trabajadorArrayList.set(index, trabajadorChanged);
                            break;
                        }
                        index++;
                    }
                    allTrabajadores.setValue(trabajadorArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "loadAllTrabajadores-onChildRemoved");

                try {
                    Trabajador trabajadorRemoved = snapshot.getValue(Trabajador.class);
                    int index = 0;
                    for (Trabajador tr : trabajadorArrayList) {
                        if (tr.getIdUsuario().equals(trabajadorRemoved.getIdUsuario())) {
                            trabajadorArrayList.remove(index);
                            break;
                        }
                        index++;
                    }
                    allTrabajadores.setValue(trabajadorArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .addChildEventListener(childEventListenerTrabajadores);
    }

    public MutableLiveData<ArrayList<Empleador>> getAllEmpleadores() {
        if (allEmpleadores == null) {
            allEmpleadores = new MutableLiveData<>();
            loadAllEmpleadores();
        }
        return allEmpleadores;
    }

    private void loadAllEmpleadores() {
        ArrayList<Empleador> empleadorArrayList = new ArrayList<>();
        ChildEventListener childEventListenerEmpleadores = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    empleadorArrayList.add(empleador);
                    allEmpleadores.setValue(empleadorArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleadorChanged = snapshot.getValue(Empleador.class);
                    int index = 0;
                    for (Empleador e : empleadorArrayList) {
                        if (e.getIdUsuario().equals(empleadorChanged.getIdUsuario())) {
                            empleadorArrayList.set(index, empleadorChanged);
                            break;
                        }
                        index++;
                    }
                    allEmpleadores.setValue(empleadorArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    Empleador empleadorRemoved = snapshot.getValue(Empleador.class);
                    int index = 0;
                    for (Empleador e : empleadorArrayList) {
                        if (e.getIdUsuario().equals(empleadorRemoved.getIdUsuario())) {
                            empleadorArrayList.remove(index);
                            break;
                        }
                        index++;
                    }
                    allEmpleadores.setValue(empleadorArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .addChildEventListener(childEventListenerEmpleadores);
    }

    public MutableLiveData<ArrayList<Oficio>> getAllOficios() {
        if (allOficios == null) {
            allOficios = new MutableLiveData<>();
            loadAllOficios();
        }
        return allOficios;
    }

    private void loadAllOficios() {
        ArrayList<Oficio> oficioArrayList = new ArrayList<>();

        ChildEventListener childEventListenerOficios = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Oficio oficio = snapshot.getValue(Oficio.class);
                    oficioArrayList.add(oficio);
                    allOficios.setValue(oficioArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Oficio oficioChanged = snapshot.getValue(Oficio.class);
                    int index = 0;
                    for (Oficio o : oficioArrayList) {
                        if (o.getIdOficio().equals(oficioChanged.getIdOficio())) {
                            oficioArrayList.set(index, oficioChanged);
                            break;
                        }
                        index++;
                    }
                    allOficios.setValue(oficioArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    Oficio oficioRemoved = snapshot.getValue(Oficio.class);
                    int index = 0;
                    for (Oficio o : oficioArrayList) {
                        if (o.getIdOficio().equals(oficioRemoved.getIdOficio())) {
                            oficioArrayList.remove(index);
                            break;
                        }
                        index++;
                    }
                    allOficios.setValue(oficioArrayList);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("oficios")
                .addChildEventListener(childEventListenerOficios);
    }
    


    public MutableLiveData<ArrayList<Trabajador>> getTrabajadoresByEmail() {
        if (trabajadoresByEmail == null) {
            trabajadoresByEmail = new MutableLiveData<>();
            loadTrabajadoresByEmail();
        }
        return trabajadoresByEmail;
    }

    private void loadTrabajadoresByEmail() {
        ArrayList<Trabajador> trabajadorsAux = new ArrayList<>();
        ChildEventListener childEventListenerTrabajadoresByEmail = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    if (trabajador.getEmail() != null) {
                        trabajadorsAux.add(trabajador);
                        trabajadoresByEmail.setValue(trabajadorsAux);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Trabajador trabajadorChanged = snapshot.getValue(Trabajador.class);
                    int index = 0;
                    for (Trabajador tr : trabajadorsAux) {
                        if (tr.equals(trabajadorChanged.getIdUsuario())) {
                            trabajadorsAux.set(index, trabajadorChanged);
                        }
                        index++;
                    }
                    trabajadoresByEmail.setValue(trabajadorsAux);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    Trabajador trabajadorRemoved = snapshot.getValue(Trabajador.class);
                    int index = 0;
                    for (Trabajador tr : trabajadorsAux) {
                        if (tr.equals(trabajadorRemoved.getIdUsuario())) {
                            trabajadorsAux.remove(index);
                        }
                        index++;
                    }
                    trabajadoresByEmail.setValue(trabajadorsAux);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .addChildEventListener(childEventListenerTrabajadoresByEmail);
    }

    public MutableLiveData<ArrayList<Trabajador>> getTrabajadoresByPhone() {
        if (trabajadoresByPhone == null) {
            trabajadoresByPhone = new MutableLiveData<>();
            loadTrabajadoresByPhone();
        }
        return trabajadoresByPhone;
    }

    private void loadTrabajadoresByPhone() {
        ArrayList<Trabajador> trabajadorsAux = new ArrayList<>();
        ChildEventListener childEventListenerTrabajadoresByPhone = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
                    if (trabajador.getCelular() != null) {
                        trabajadorsAux.add(trabajador);
                        trabajadoresByPhone.setValue(trabajadorsAux);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Trabajador trabajadorChanged = snapshot.getValue(Trabajador.class);
                    int index = 0;
                    for (Trabajador tr : trabajadorsAux) {
                        if (tr.equals(trabajadorChanged.getIdUsuario())) {
                            trabajadorsAux.set(index, trabajadorChanged);
                        }
                        index++;
                    }
                    trabajadoresByPhone.setValue(trabajadorsAux);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    Trabajador trabajadorRemoved = snapshot.getValue(Trabajador.class);
                    int index = 0;
                    for (Trabajador tr : trabajadorsAux) {
                        if (tr.equals(trabajadorRemoved.getIdUsuario())) {
                            trabajadorsAux.remove(index);
                        }
                        index++;
                    }
                    trabajadoresByPhone.setValue(trabajadorsAux);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .addChildEventListener(childEventListenerTrabajadoresByPhone);
    }

    public MutableLiveData<ArrayList<Empleador>> getEmpleadoresByEmail() {
        if (empleadoresByEmail == null) {
            empleadoresByEmail = new MutableLiveData<>();
            loadEmpleadoresByEmail();
        }
        return empleadoresByEmail;
    }

    private void loadEmpleadoresByEmail() {
        ArrayList<Empleador> empleadorsAux = new ArrayList<>();
        ChildEventListener childEventListenerEmpleadoresByEmail = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    if (empleador.getEmail() != null) {
                        empleadorsAux.add(empleador);
                        empleadoresByEmail.setValue(empleadorsAux);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleadorChanged = snapshot.getValue(Empleador.class);
                    int index = 0;
                    for (Empleador e : empleadorsAux) {
                        if (e.getIdUsuario().equals(empleadorChanged.getIdUsuario())) {
                            empleadorsAux.set(index, empleadorChanged);
                        }
                        index++;
                    }
                    empleadoresByEmail.setValue(empleadorsAux);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    Empleador empleadorRemoved = snapshot.getValue(Empleador.class);
                    int index = 0;
                    for (Empleador e : empleadorsAux) {
                        if (e.getIdUsuario().equals(empleadorRemoved.getIdUsuario())) {
                            empleadorsAux.remove(index);
                        }
                        index++;
                    }
                    empleadoresByEmail.setValue(empleadorsAux);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .addChildEventListener(childEventListenerEmpleadoresByEmail);
    }

    public MutableLiveData<ArrayList<Empleador>> getEmpleadoresByPhone() {
        if (empleadoresByPhone == null) {
            empleadoresByPhone = new MutableLiveData<>();
            loadEmpleadoresByPhone();
        }
        return empleadoresByPhone;
    }

    private void loadEmpleadoresByPhone() {
        ArrayList<Empleador> empleadorsAux = new ArrayList<>();
        ChildEventListener childEventListenerEmpleadoresByPhone = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleador = snapshot.getValue(Empleador.class);
                    if (empleador.getCelular() != null) {
                        empleadorsAux.add(empleador);
                        empleadoresByPhone.setValue(empleadorsAux);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Empleador empleadorChanged = snapshot.getValue(Empleador.class);
                    int index = 0;
                    for (Empleador e : empleadorsAux) {
                        if (e.getIdUsuario().equals(empleadorChanged.getIdUsuario())) {
                            empleadorsAux.set(index, empleadorChanged);
                        }
                        index++;
                    }
                    empleadoresByPhone.setValue(empleadorsAux);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    Empleador empleadorRemoved = snapshot.getValue(Empleador.class);
                    int index = 0;
                    for (Empleador e : empleadorsAux) {
                        if (e.getIdUsuario().equals(empleadorRemoved.getIdUsuario())) {
                            empleadorsAux.remove(index);
                        }
                        index++;
                    }
                    empleadoresByPhone.setValue(empleadorsAux);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .addChildEventListener(childEventListenerEmpleadoresByPhone);

    }
}
