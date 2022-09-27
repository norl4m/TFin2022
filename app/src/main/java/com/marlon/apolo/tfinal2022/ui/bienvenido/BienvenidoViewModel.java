package com.marlon.apolo.tfinal2022.ui.bienvenido;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Habilidad;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.oficios.OficioRepository;

import java.util.ArrayList;

public class BienvenidoViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private BienvenidoRepository bienvenidoRepository;
    private MutableLiveData<ArrayList<Trabajador>> allTrabajadores;
    private MutableLiveData<ArrayList<Trabajador>> allTrabajadoresbyEmail;
    private MutableLiveData<ArrayList<Trabajador>> allTrabajadoresByPhone;
    private MutableLiveData<ArrayList<Empleador>> allEmpleadores;
    private MutableLiveData<ArrayList<Empleador>> allEmpleadoresByEmail;
    private MutableLiveData<ArrayList<Empleador>> allEmpleadoresByPhone;
    private MutableLiveData<ArrayList<Oficio>> allOficios;
    private MutableLiveData<ArrayList<Habilidad>> habilidadesByOficio;

    public BienvenidoViewModel() {
        bienvenidoRepository = new BienvenidoRepository();
    }

    public MutableLiveData<ArrayList<Trabajador>> getAllTrabajadores() {
        allTrabajadores = bienvenidoRepository.getAllTrabajadores();
        return allTrabajadores;
    }

    public MutableLiveData<ArrayList<Empleador>> getAllEmpleadores() {
        allEmpleadores = bienvenidoRepository.getAllEmpleadores();
        return allEmpleadores;
    }

    public MutableLiveData<ArrayList<Oficio>> getAllOficios() {
        allOficios = bienvenidoRepository.getAllOficios();
        return allOficios;
    }

    public MutableLiveData<ArrayList<Habilidad>> getHabilidadesByOficio(String idOficio) {
        habilidadesByOficio = bienvenidoRepository.getHabilidadesByOficio(idOficio);
        return habilidadesByOficio;
    }

    public void addHabilidadToOficioTofirebase(Activity activity, Oficio oficio, Habilidad habilidad) {
        bienvenidoRepository.addHabilidadToOficioTofirebase(activity, oficio, habilidad);
    }


    public MutableLiveData<ArrayList<Trabajador>> getAllTrabajadoresbyEmail() {
        allTrabajadoresbyEmail = bienvenidoRepository.getTrabajadoresByEmail();
        return allTrabajadoresbyEmail;
    }

    public MutableLiveData<ArrayList<Trabajador>> getAllTrabajadoresByPhone() {
        allTrabajadoresByPhone = bienvenidoRepository.getTrabajadoresByPhone();

        return allTrabajadoresByPhone;
    }

    public MutableLiveData<ArrayList<Empleador>> getAllEmpleadoresByEmail() {
        allEmpleadoresByEmail = bienvenidoRepository.getEmpleadoresByEmail();

        return allEmpleadoresByEmail;
    }

    public MutableLiveData<ArrayList<Empleador>> getAllEmpleadoresByPhone() {
        allEmpleadoresByPhone = bienvenidoRepository.getEmpleadoresByPhone();

        return allEmpleadoresByPhone;
    }
}