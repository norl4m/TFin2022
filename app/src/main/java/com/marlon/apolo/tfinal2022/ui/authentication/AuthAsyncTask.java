package com.marlon.apolo.tfinal2022.ui.authentication;

import static com.google.common.net.HttpHeaders.USER_AGENT;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.UsuarioFirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * El tipo de parámetro "Params" es String, lo que significa que MyAsyncTasktoma una o más cadenas como parámetros en doInBackground(),
 * por ejemplo, para usar en una consulta.
 * El tipo de parámetro "Progreso" es Void, lo que significa que MyAsyncTaskno usará los métodos publishProgress()o .onProgressUpdate()
 * El tipo de parámetro "Resultado" es Bitmap. MyAsyncTaskdevuelve un mapa de bits en doInbackground(), que se pasa a onPostExecute().
 */
public class AuthAsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = AuthAsyncTask.class.getSimpleName();
    private ProgressBar progressBarWeakReference;
    private Context contextInstance;
    private AuthUserListAdapter authUserListAdapter;
    ArrayList<UsuarioFirebaseAuth> authArrayList;

    public AuthAsyncTask(Context context, ProgressBar tv, AuthUserListAdapter var) {
        contextInstance = context;
        progressBarWeakReference = tv;
        authUserListAdapter = var;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            String GET_URL = "https://authwitouthauth.herokuapp.com/nimda/usuarios";
            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
//
//            Map<String, List<String>> map = con.getHeaderFields();
//
//
//            for (String key : map.keySet()) {
//                System.out.println(key + ":");
//
//                List<String> values = map.get(key);
//
//                for (String aValue : values) {
//                    System.out.println("\t" + aValue);
//                }
//            }


            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();


                while ((inputLine = in.readLine()) != null) {
//                    Log.d(TAG, inputLine);
                    response.append(inputLine);
                }
                in.close();

                // print result
//                Log.d(TAG, response.toString());
                // Return a String result
                authArrayList = new ArrayList<>();
                try {
//                    String str = "[{\"No\":\"17\",\"Name\":\"Andrew\"},{\"No\":\"18\",\"Name\":\"Peter\"}, {\"No\":\"19\",\"Name\":\"Tom\"}]";
//                    String str = response.toString();
                    String str = response.toString();
                    String strArray = str.substring(str.indexOf('['), str.indexOf(']') + 1);
                    Log.d(TAG, strArray.toString());

                    JSONArray array = new JSONArray(strArray);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
//                        System.out.println(object.getString("No"));
//                        System.out.println(object.getString("Name"));
//                        System.out.println(object.getString("uid"));
//                        System.out.println(object.getString("email"));
//                        System.out.println(object.getString("phoneNumber"));

                        UsuarioFirebaseAuth usuarioFirebaseAuthAux = new UsuarioFirebaseAuth();
                        usuarioFirebaseAuthAux.setUid(object.getString("uid"));
                        usuarioFirebaseAuthAux.setEmail(object.getString("email"));
                        usuarioFirebaseAuthAux.setPhoneNumber(object.getString("phoneNumber"));
                        authArrayList.add(usuarioFirebaseAuthAux);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

//                authUserListAdapter.setFirebaseUsers(authArrayList);


                return response.toString();
            } else {
                Log.d(TAG, "Error en la solicitud");
                // Return a String result
                progressBarWeakReference.setVisibility(View.GONE);
                return "Error en la solicitud";
            }
        } catch (Exception e) {
            progressBarWeakReference.setVisibility(View.GONE);
            return "Error: " + e.toString();
        }

    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
////        setProgressPercent(progress[0]);
//
//    }


    @Override
    protected void onPostExecute(String s) {
        progressBarWeakReference.setVisibility(View.GONE);
        /*Filtrado gg*/
        for (UsuarioFirebaseAuth us : authArrayList) {
            us.setExtraLol("si");
        }

        for (UsuarioFirebaseAuth us : authArrayList) {
            try {
                for (Trabajador tr : authUserListAdapter.getTrabajadorArrayList()) {
                    if (us.getUid().equals(tr.getIdUsuario())) {
                        us.setExtraLol("trabajador");
                        us.setExtraUsuarioLol(tr);
                    } else {
//                    us.setExtraLol("si");
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }

        for (UsuarioFirebaseAuth us : authArrayList) {
            try {
                for (Empleador tr : authUserListAdapter.getEmpleadorArrayList()) {
                    if (us.getUid().equals(tr.getIdUsuario())) {
                        us.setExtraLol("empleador");
                        us.setExtraUsuarioLol(tr);
                    } else {
//                    us.setExtraLol("si");
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }


        authUserListAdapter.setFirebaseUsers(authArrayList);
//        Log.d(TAG, s.toString());
//        Toast.makeText(contextInstance, s, Toast.LENGTH_LONG).show();
        super.onPostExecute(s);
    }
}
