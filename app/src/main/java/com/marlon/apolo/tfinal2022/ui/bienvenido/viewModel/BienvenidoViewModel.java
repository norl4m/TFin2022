package com.marlon.apolo.tfinal2022.ui.bienvenido.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Oficio;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.ui.bienvenido.repository.BienvenidoRepository;

import java.util.ArrayList;

public class BienvenidoViewModel extends ViewModel {

    private BienvenidoRepository bienvenidoRepository;
    private MutableLiveData<ArrayList<Trabajador>> allTrabajadores;
    private MutableLiveData<ArrayList<Trabajador>> allTrabajadoresbyEmail;
    private MutableLiveData<ArrayList<Trabajador>> allTrabajadoresByPhone;
    private MutableLiveData<ArrayList<Empleador>> allEmpleadores;
    private MutableLiveData<ArrayList<Empleador>> allEmpleadoresByEmail;
    private MutableLiveData<ArrayList<Empleador>> allEmpleadoresByPhone;
    private MutableLiveData<ArrayList<Oficio>> allOficios;


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