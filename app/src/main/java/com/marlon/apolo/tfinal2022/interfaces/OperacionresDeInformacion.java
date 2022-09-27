package com.marlon.apolo.tfinal2022.interfaces;

import android.app.Activity;
import android.net.Uri;

public abstract class OperacionresDeInformacion {

    public abstract void registrarseEnFirebase(Activity activity, int metodoReg);

    public abstract void registrarseEnFirebaseConFoto(Activity activity, int metodoReg);

    public abstract void actualizarInfo(Activity activity);

    public abstract void actualizarInfoConFoto(Activity activity, Uri uri);

    public abstract void eliminarInfo(Activity activity);
}
