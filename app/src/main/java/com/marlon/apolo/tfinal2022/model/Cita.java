package com.marlon.apolo.tfinal2022.model;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@IgnoreExtraProperties
public class Cita implements Serializable {
    //    private static final String TAG = Cita.class.getSimpleName();
    private String idCita;
    //    private ArrayList<String> participants;
    private String nombreTrabajador;
    private String nombreEmpleador;
    private String fechaCita;
    private String finalizacionTrabajo;
    private ArrayList<Item> items;
    private Double total;
    private String chatID;
    private Calendar calendaHoraCita;
    private float calificacion;
    private boolean state;
    private String from;
    private String to;
    private String observaciones;/*--*/

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    private boolean stateReceive;

    public Cita() {
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

//    public ArrayList<String> getParticipants() {
//        return participants;
//    }
//
//    public void setParticipants(ArrayList<String> participants) {
//        this.participants = participants;
//    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public String getNombreEmpleador() {
        return nombreEmpleador;
    }

    public void setNombreEmpleador(String nombreEmpleador) {
        this.nombreEmpleador = nombreEmpleador;
    }

    public String getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(String fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getFinalizacionTrabajo() {
        return finalizacionTrabajo;
    }

    public void setFinalizacionTrabajo(String finalizacionTrabajo) {
        this.finalizacionTrabajo = finalizacionTrabajo;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void finalizarTrabajo(String chatID) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("citas")
                .child(this.getIdCita())
                .setValue(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
//                        Log.e(TAG, "Cita de trabajo terminada");
                    }
                });
    }

    public void eliminarCita(String chatID) {
        this.setState(true);//finalizado
        FirebaseDatabase.getInstance()
                .getReference()
                .child("citas")
                .child(chatID)
                .child(this.getIdCita())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
//                        Log.e(TAG, "Cita eliminada");
                    }
                });
    }

    public void actualizarCitaEstado() {
        Log.e("CITA", "actualizando cita");

        FirebaseDatabase.getInstance()
                .getReference()
                                                        .child("citas")
                                        .child(this.getIdCita())
                                        .child("stateReceive")


                .setValue(this.stateReceive)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.e("CITA", "Cita de trabajo actualizada");
                    }
                });
    }

    public boolean validarCita(Activity activity, int opcion) {
        Cita cita = this;

//        Log.d(TAG, "/****************************/");
//        Log.d(TAG, cita.toString());
//        Log.d(TAG, "/****************************/");

        boolean validacion = true;
        ArrayList<String> arrayListErrores = new ArrayList<>();
//        if (cita.getTotal() == 0) {
//            Log.d(TAG, "Cita no tiene total");
//            validacion = false;
//            arrayListErrores.add("La cita debe contener al menos un detalle del servicio");
//        }
        /*if (cita.getItems().isEmpty()) {
//            Log.d(TAG, "Cita no tiene items");
            validacion = false;
            arrayListErrores.add("La cita debe contener al menos un detalle");
        }*/

        DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(localYear, localMonth, localDay, localHourDay, localMinute, 0);
        //Log.d(TAG, format.format(calendar.getTime()));

        String fechaYHora = cita.getFechaCita();
        String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
        Locale locale = new Locale("es", "ES");
        String patronFecha = "dd MMMM yyyy";
        String patronHora = "HH:mm aa";


        //String [] result = herramientaCalendar.separarFechaYHora(fechaYHora,patronFechaYHora,locale,patronFecha, patronHora);

        //cita.setFechaCita();

        try {
            SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
            Date date = formatFecha.parse(fechaYHora);
            //Log.d(TAG, "INPUT: " + horaYFecha);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

//            Date date1 = format.parse(format.format(calendar.getTime()));
            Date date1 = format.parse(format.format(cal.getTime()));
            Date date2 = new Date();
//            Log.d(TAG, "Date 1 selected: " + format.format(date1));
//            Log.d(TAG, "Date 2 compare: " + format.format(date2));
            if (date1.compareTo(date2) > 0) {
                //Log.d(TAG, "La fecha seleccionada es correcta");

            } else if (date1.compareTo(date2) < 0) {
//                Log.d(TAG, "La fecha seleccionada es incorrecta");
                switch (opcion) {
                    case 0:
                        arrayListErrores.add("La fecha seleccionada es incorrecta");
                        validacion = false;
                        break;
                    case 1:
                        // Para actualizacion en caso de que la cita de trabajo finalice
                        validacion = true;
                        break;
                }

            } else if (date1.compareTo(date2) == 0) {

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean validarItems = true;
        for (Item item : cita.getItems()) {
            if (item.getDetail().length() == 0) {
                validarItems = false;
            }
        }

        if (cita.getItems().size() == 0) {
            // Toast.makeText(activity, String.valueOf(cita.getItems().size()), Toast.LENGTH_SHORT).show();

            validacion = false;
            arrayListErrores.add("La cita debe contener al menos un detalle");
        }

        if (!validarItems) {
            arrayListErrores.add("Los detalles no pueden ir en blanco");
            validacion = false;
        }
        //Toast.makeText(activity, String.valueOf(validacion), Toast.LENGTH_LONG).show();

        if (!validacion) {
            String errores = "";
            for (String error : arrayListErrores) {
                errores = errores + error + "\n";
            }
            //Toast.makeText(activity, errores, Toast.LENGTH_LONG).show();

            erroresCita(errores, activity).show();
        }
        return validacion;
    }

    public Dialog erroresCita(String errores, Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Error")
                .setMessage(errores)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                })
        ;
        // Create the AlertDialog object and return it
        return builder.create();


    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("idCita", idCita);
        result.put("nombreEmpleador", nombreEmpleador);
        result.put("nombreTrabajador", nombreTrabajador);
        result.put("fechaCita", fechaCita);
        result.put("total", total);
        result.put("state", state);
//        result.put("participants", participants);
        result.put("calificacion", calificacion);
        result.put("from", from);
        result.put("to", to);
        result.put("observaciones", observaciones);
        result.put("stateReceive", stateReceive);

        return result;
    }
//    @Override
//    public String toString() {
//        return "Cita{" +
//                "idCita='" + idCita + '\'' +
//                ", participants=" + participants +
//                ", nombreTrabajador='" + nombreTrabajador + '\'' +
//                ", nombreEmpleador='" + nombreEmpleador + '\'' +
//                ", fechaCita='" + fechaCita + '\'' +
//                ", finalizacionTrabajo='" + finalizacionTrabajo + '\'' +
//                ", items=" + items +
//                ", total=" + total +
//                '}';
//    }


    public Calendar getCalendaHoraCita() {
        return calendaHoraCita;
    }

    public void setCalendaHoraCita(Calendar calendaHoraCita) {
        this.calendaHoraCita = calendaHoraCita;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }


    public boolean isStateReceive() {
        return stateReceive;
    }

    public void setStateReceive(boolean stateReceive) {
        this.stateReceive = stateReceive;
    }


    @Override
    public String toString() {
        return "Cita{" +
                "idCita='" + idCita + '\'' +
                ", nombreTrabajador='" + nombreTrabajador + '\'' +
                ", nombreEmpleador='" + nombreEmpleador + '\'' +
                ", fechaCita='" + fechaCita + '\'' +
                ", finalizacionTrabajo='" + finalizacionTrabajo + '\'' +
                ", items=" + items +
                ", total=" + total +
                ", chatID='" + chatID + '\'' +
                ", calendaHoraCita=" + calendaHoraCita +
                ", calificacion=" + calificacion +
                ", state=" + state +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", stateReceive=" + stateReceive +
                '}';
    }
}
