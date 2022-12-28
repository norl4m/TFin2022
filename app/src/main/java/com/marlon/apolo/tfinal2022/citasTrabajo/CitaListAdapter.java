package com.marlon.apolo.tfinal2022.citasTrabajo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.herramientas.DataValidation;
import com.marlon.apolo.tfinal2022.model.Cita;

import java.util.ArrayList;

public class CitaListAdapter extends RecyclerView.Adapter<CitaListAdapter.CitaViewHolder> {
    private final LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Cita> citaArrayList;
    private String TAG = CitaListAdapter.class.getSimpleName();


    public CitaListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }


    public ArrayList<Cita> getCitaArrayList() {
        return citaArrayList;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.card_view_service_detail, parent, false);
        return new CitaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        if (citaArrayList != null) {
            Cita cita = citaArrayList.get(position);
            DataValidation dataValidation = new DataValidation();
            String sec = dataValidation.splitterData(cita.getFechaCita(), "(seconds=", ",");
            String nansec = dataValidation.splitterData(cita.getFechaCita(), ", nanoseconds=", ")");
            //  Log.d("TAG", String.format("%s %s", sec, nansec));
            //long seconds = Long.parseLong(sec);
            //int nanoseconds = Integer.parseInt(nansec);
            //Timestamp timestamp = new Timestamp(seconds, nanoseconds);
            // timestamp.toDate()
            //Returns a new Date corresponding to this timestamp. This may lose precision.
            //Date date = timestamp.toDate();
//        Log.d("TAG", date.toLocaleString());
//        Log.d("TAG", date.toGMTString());
//        Log.d("TAG", String.valueOf(date.getTime()));
            // date.toLocaleString() permite que se obtenga la hora correcta
            // si utilizo GMT entonces la hora se cambia al formato GMT
            // Se decide escoger toLacaleString()
            holder.textViewTitle.setVisibility(View.GONE);
            if (cita.isState()) {
                holder.imageView.setImageResource(R.drawable.ic_baseline_check_circle_24);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_baseline_schedule_24);

            }
            try {
                if (cita.getObservaciones() == null) {
                    holder.textViewObs.setText(String.format("Observaciones: %s", "Ninguna"));

                } else {
                    holder.textViewObs.setText(String.format("Observaciones: %s", cita.getObservaciones()));

                }

                switch (cita.getObservaciones()) {
                    case "Trabajador no asistió":
                        holder.imageView.setImageResource(R.drawable.ic_no_vino);
                        break;
                    case "Trabajador incumplido":
                        holder.imageView.setImageResource(R.drawable.ic_incompleto);
                        break;

                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            holder.textViewFechaCita.setText(String.format("Fecha: %s", cita.getFechaCita()));
            holder.textViewTrabjador.setText(String.format("Trabajador: %s", cita.getNombreTrabajador()));
            holder.textViewEmpleador.setText(String.format("Empleador: %s", cita.getNombreEmpleador()));
            holder.textViewTotal.setText(String.format("Total: $ %.2f", cita.getTotal()));
            holder.textViewCalif.setText(String.format("Calificación: %.1f", cita.getCalificacion()));
            int someColorFrom = 0;


            switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
//                someColorFrom = ContextCompat.getColor(activity, R.color.black_minus);
                    someColorFrom = ContextCompat.getColor(context, R.color.purple_200);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    someColorFrom = ContextCompat.getColor(context, R.color.purple_700);
                    break;
            }

//        holder.linearLayout.setBackgroundColor(someColorTo);
            holder.imageView.setColorFilter(someColorFrom);


        }

    }


    @Override
    public int getItemCount() {
        if (citaArrayList != null)
            return citaArrayList.size();
        else return 0;
    }

    class CitaViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewEmpleador;
        private final TextView textViewTitle;
        private final TextView textViewTrabjador;
        private final TextView textViewFechaCita;
        private final TextView textViewTotal;
        private final TextView textViewCalif;
        private final TextView textViewObs;
        private final ImageView imageView;


        private CitaViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewEmpleador = itemView.findViewById(R.id.textViewEmpleadorDialog);
            textViewTrabjador = itemView.findViewById(R.id.textViewTrabajadorDialog);
            textViewFechaCita = itemView.findViewById(R.id.textViewFechaCitadialog);
            textViewTotal = itemView.findViewById(R.id.textViewCostoTotalDialog);
            imageView = itemView.findViewById(R.id.imageViewStatus);
            textViewCalif = itemView.findViewById(R.id.textViewCalificacion);
            textViewObs = itemView.findViewById(R.id.textViewObsv);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG", "Click en cita imageView");

                    ArrayList<Cita> citas = new ArrayList<>();
                    Cita cita = citaArrayList.get(getAdapterPosition());
                    Log.d(TAG, cita.toString());
                    Cita citaPass = new Cita();
                    citaPass.setChatID(cita.getChatID());
                    citaPass.setIdCita(cita.getIdCita());
                    citaPass.setFechaCita(cita.getFechaCita());
                    citaPass.setTotal(cita.getTotal());
                    citaPass.setNombreEmpleador(cita.getNombreEmpleador());
                    citaPass.setNombreTrabajador(cita.getNombreTrabajador());
                    citaPass.setFrom(cita.getFrom());
                    citaPass.setTo(cita.getTo());


                    citaPass.setState(cita.isState());
                    citaPass.setStateReceive(cita.isStateReceive());
                    citaPass.setObservaciones(cita.getObservaciones());
//                    citaPass.setParticipants(cita.getParticipants());

                    citaPass.setState(cita.isState());
                    Log.e(TAG, "Click en cita imageView: " + citaPass.toString());

                    citas.add(citaPass);

                    //Toast.makeText(context, cita.toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, DetalleServicioActivity.class);
                    //  Lo comentarios se deben a las formas de pasar los datos al intent
                    //
                    //intent.putExtra("cita", (Serializable) cita);
//                    intent.putExtra("idCita", cita.getIdCita());
//                    intent.putExtra("nombreEmpleador", cita.getNombreEmpleador());
//                    intent.putExtra("nombreTrabajador", cita.getNombreTrabajador());
//                    intent.putExtra("fecha", cita.getFechaCita());
//                    intent.putExtra("total", cita.getTotal());
//                    intent.putExtra("Cita", cita);
                    //  Bundle args = new Bundle();
                    //  args.putSerializable("key", cita);
                    // intent.putExtra("value", args);
                    intent.putExtra("cita", citaPass);

                    context.startActivity(intent);
                    //clickListener.onItemClick(v, getAdapterPosition());
                }
            });

        }


    }


    public void setCitas(ArrayList<Cita> citas) {
        citaArrayList = citas;
        notifyDataSetChanged();
    }

    public Cita getCitaAtPosition(int position) {
        return citaArrayList.get(position);
    }
}