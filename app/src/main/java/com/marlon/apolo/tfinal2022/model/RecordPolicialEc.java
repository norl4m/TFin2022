package com.marlon.apolo.tfinal2022.model;

public class RecordPolicialEc {
    private String fechaEmision;
    private String numCert;
    private String tipoDocu;
    private String numIdenti;
    private String ApellidosNombres;
    private String regAntecedentes;

    public RecordPolicialEc() {
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNumCert() {
        return numCert;
    }

    public void setNumCert(String numCert) {
        this.numCert = numCert;
    }

    public String getTipoDocu() {
        return tipoDocu;
    }

    public void setTipoDocu(String tipoDocu) {
        this.tipoDocu = tipoDocu;
    }

    public String getNumIdenti() {
        return numIdenti;
    }

    public void setNumIdenti(String numIdenti) {
        this.numIdenti = numIdenti;
    }

    public String getApellidosNombres() {
        return ApellidosNombres;
    }

    public void setApellidosNombres(String apellidosNombres) {
        ApellidosNombres = apellidosNombres;
    }

    public String getRegAntecedentes() {
        return regAntecedentes;
    }

    public void setRegAntecedentes(String regAntecedentes) {
        this.regAntecedentes = regAntecedentes;
    }

    @Override
    public String toString() {
        return "Fecha de emisión: " + fechaEmision +
                "\nNúmero de certificado: " + numCert +
                "\nTipo de Documento: " + tipoDocu +
                "\nNo. de Identificación: " + numIdenti +
                "\nApellidos y Nombres: " + ApellidosNombres +
                "\nRegistra Antecedentes: " + regAntecedentes;
    }

}
