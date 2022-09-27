package com.marlon.apolo.tfinal2022.model;

import org.jetbrains.annotations.NotNull;

public class PoliceRecord {
    private String dateCreation;
    private String certificateNumber;
    private String typeDocument;
    private String ci;
    private String nameAndLastName;
    private boolean statusCriminalRecord;

    public PoliceRecord() {
    }

    public PoliceRecord(String dateCreation, String certificateNumber, String typeDocument, String ci, String names, boolean statusCriminalRecord) {
        this.dateCreation = dateCreation;
        this.certificateNumber = certificateNumber;
        this.typeDocument = typeDocument;
        this.ci = ci;
        this.nameAndLastName = names;
        this.statusCriminalRecord = statusCriminalRecord;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNameAndLastName() {
        return nameAndLastName;
    }

    public void setNameAndLastName(String nameAndLastName) {
        this.nameAndLastName = nameAndLastName;
    }

    public boolean isStatusCriminalRecord() {
        return statusCriminalRecord;
    }

    public void setStatusCriminalRecord(boolean statusCriminalRecord) {
        this.statusCriminalRecord = statusCriminalRecord;
    }

    public boolean verifyRecord(PoliceRecord policeRecord) {
        return false;
    }

    @NotNull
    @Override
    public String toString() {
        String valueCriminalStatus = "";
        if (isStatusCriminalRecord()) {
            valueCriminalStatus = "SI";
        } else {
            valueCriminalStatus = "NO";
        }

        return "Fecha de emisión: " + dateCreation +
                "\nNúmero de Certificado: " + certificateNumber +
                "\nTipo de Documento: " + typeDocument +
                "\nNo. de Identificación: " + ci +
                "\nApellidos y Nombres: " + nameAndLastName +
                "\nRegistra Antecedentes: " + valueCriminalStatus;
    }
}
