package com.marlon.apolo.tfinal2022.herramientas;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {
    private static final String TAG = DataValidation.class.getSimpleName();

    public DataValidation() {
    }

    //  https://medium.com/@bryansuarez/cómo-validar-cédula-y-ruc-en-ecuador-b62c5666186f
    // * La cédula ecuatoriana está formada por los dos primeros dígitos que corresponden
    // a la provincia donde fue expedida, por lo cual, los dos primeros dígitos no serán mayores a 24 ni menores a 0.
    //* El tercer dígito es un número menor a 6 (0,1,2,3,4,5).
    //* Los siguientes hasta el noveno dígito son un número consecutivo.
    //* El décimo es el dígito verificador.
    //Los coeficientes usados para verificar el décimo dígito de la cédula, mediante el “Módulo 10”.

    public boolean validateCI(String ci) {
//        int ciLength = ci.length();
        Log.d(TAG, "Verificando # CI");

        Log.d(TAG, ci);
        char firstDigit = ci.charAt(0);
        //Log.d("TAG", String.valueOf(firstDigit));
        char secondDigit = ci.charAt(1);
        //Log.d("TAG", String.valueOf(secondDigit));
        String firstDigits = String.valueOf(firstDigit) + String.valueOf(secondDigit);
        int provinceDigits = Integer.parseInt(firstDigits);
        int thirdDigit = Integer.parseInt(String.valueOf(ci.charAt(2)));
        // Log.d("TAG", String.valueOf(thirdDigit));
        /// Log.d("TAG", String.valueOf(provinceDigits));
        String consecutive = ci.substring(3, 9);
        int consecutiveDigits = Integer.parseInt(consecutive);
        // Log.d("TAG", String.valueOf(consecutiveDigits));
        //Sacar modulo de los 9 digitos para el decimo de la siguiente manera
        //Los dígitos pares son multiplicados por 2 y si el resultado es mayor o igual a 10 se le resta 9
        //Los dígitos impares son multiplicados por 1
        //A la suma de todos los dígitos conjuntamente con la operación anterior se le resta la decena
        // superior, es decir si la suma sale 31 debemos restar 40 - 31 = 9
        int suma = 0;
        for (int i = 0; i < ci.length() - 1; i++) {
            int digit = Integer.parseInt(String.valueOf(ci.charAt(i)));
            if (i % 2 == 0) {
                //Log.d("TAG", "par");
                digit = digit * 2;
                if (digit >= 10) {
                    digit = digit - 9;
                }
                suma = suma + digit;
                //Log.d("TAG", String.valueOf(digit));

            } else {
                //Log.d("TAG", "impar");
                digit = digit * 1;
                suma = suma + digit;
                //Log.d("TAG", String.valueOf(digit));

            }
            //Log.d("TAG", String.valueOf(ci.charAt(i)));
        }
        //Log.d("TAG", String.valueOf(suma));
        int verifier = (suma - (suma % 10) + 10) - suma;
        //Log.d("TAG", String.valueOf(verifier));
        boolean validation = (provinceDigits >= 0 && provinceDigits <= 24) &&
                (thirdDigit >= 0 && thirdDigit <= 5) &&
                (ci.length() == 10) &&
                consecutiveDigits > 0 &&
                verifier == Integer.parseInt(String.valueOf(ci.charAt(9))); //true 0<= digits <=24 // false other
//        validation = thirdDigit >= 0 && thirdDigit <= 5;
        //  Log.d("TAG", String.valueOf(validation));
        /*
         * Example CI = 17 2 216269 8 */
        return validation;
    }

    public boolean validateNameWithPoliceRecord(String inputName, String nameOnPoliceRecord) {
        boolean validation = true;
        String formatName = nameOnPoliceRecord.replaceAll("\\s+", ",");
        Pattern pattern = Pattern.
                compile(String.format("%s%s(.*?)%s",
                        inputName.charAt(0),
                        inputName.charAt(1),
                        ","), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(formatName);
        boolean matchFound = matcher.find();
        if (matchFound) {
            Log.d(TAG, "Match found");
            String findName = String.format("%s%s%s",
                    inputName.charAt(0),
                    inputName.charAt(1),
                    matcher.group(1));
            Log.d("TAG-find", findName);
            //Log.d("TAG", String.valueOf(findName.length()));
            Log.d("TAG-input", inputName);
            //Log.d("TAG", String.valueOf(inputName.length()));
            if (!(findName.length() == inputName.length())) {
                validation = false;
                Log.e("TAGV1", String.valueOf(validation));
            } else {
                for (int i = 0; i < findName.length(); i++) {
                    Log.e("TAG", String.valueOf(findName.charAt(i)));
                    if (!(findName.charAt(i) == inputName.charAt(i))) {
                        validation = false;
                    }
                }
                //Log.d("TAGV2", String.valueOf(validation));
            }

//            for (int i = 0; i < inputName.length(); i++) {
            //Log.d("TAG", String.valueOf(inputName.charAt(i)));
//            }
        } else {
            Log.d(TAG, "Match not found");
        }

        return validation;
    }

//
//    public boolean validateLastNameWithPoliceRecord(String inputLastNameName, String nameOnPoliceRecord) {
//        boolean validation = true;
//
//        return validation;
//    }


    public boolean validateNameWithPoliceRecord(String inputName, String inputLastName, String nameOnPoliceRecord) {
        boolean validation = true;
        String formatName = nameOnPoliceRecord.replaceAll("\\s+", ",");
        Log.e("VALIDACION DATOS", "VALIDANDO DATOS......");
        Log.e("NOMBRE FORMATEADO", formatName);

        if (inputName.length()<3){
            Log.e(TAG,"Por ingresar un nombre valido");
        }
        if (inputLastName.length()<3){
            Log.e(TAG,"Por ingresar un apellido valido");
        }

        if (inputName.contains("á"))
            inputName = inputName.replaceAll("á", "a");
        if (inputName.contains("é"))
            inputName = inputName.replaceAll("é", "e");
        if (inputName.contains("í"))
            inputName = inputName.replaceAll("í", "i");
        if (inputName.contains("ó"))
            inputName = inputName.replaceAll("ó", "o");
        if (inputName.contains("ú"))
            inputName = inputName.replaceAll("ú", "u");

        if (inputLastName.contains("á"))
            inputLastName = inputLastName.replaceAll("á", "a");
        if (inputLastName.contains("é"))
            inputLastName = inputLastName.replaceAll("é", "e");
        if (inputLastName.contains("í"))
            inputLastName = inputLastName.replaceAll("í", "i");
        if (inputLastName.contains("ó"))
            inputLastName = inputLastName.replaceAll("ó", "o");
        if (inputLastName.contains("ú"))
            inputLastName = inputLastName.replaceAll("ú", "u");


        inputName = inputName.toUpperCase();
        try {
            if (formatName.contains(inputName)) {
                Log.e("CONTIENE NOMBRE", inputName);
                Pattern pattern = Pattern.
                        compile(String.format("%s%s(.*?)%s",
                                inputName.charAt(0),
                                inputName.charAt(1),
                                ","), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(formatName);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    Log.d(TAG, "Match found");
                    String findName = String.format("%s%s%s",
                            inputName.charAt(0),
                            inputName.charAt(1),
                            matcher.group(1));
                    Log.d("TAG-find", findName);
                    //Log.d("TAG", String.valueOf(findName.length()));
                    Log.d("TAG-input", inputName);
                    //Log.d("TAG", String.valueOf(inputName.length()));
                    if (!(findName.length() == inputName.length())) {
                        validation = false;
                        Log.e("TAGV1", String.valueOf(validation));
                    } else {
                        for (int i = 0; i < findName.length(); i++) {
                            Log.e("TAG", String.valueOf(findName.charAt(i)));
                            if (!(findName.charAt(i) == inputName.charAt(i))) {
                                validation = false;
                            }
                        }
                        Log.d("TAGV2", String.valueOf(validation));
                    }

//            for (int i = 0; i < inputName.length(); i++) {
                    //Log.d("TAG", String.valueOf(inputName.charAt(i)));
//            }
                } else {
                    Log.d(TAG, "Match not found");
                }
//            Log.e(TAG, comparador);


            } else {
                Log.e("NO CONTIENE NOMBRE", "formatName");
                validation = false;

            }
        }catch (Exception ex){
            Log.e("NO CONTIENE NOMBRE", "formatName");
            validation = false;
        }

        inputLastName = inputLastName.toUpperCase();

        try {
            if (formatName.contains(inputLastName)) {
                Log.e("CONTIENE APELLIDOI", inputLastName);


                Pattern pattern = Pattern.
                        compile(String.format("%s%s%s(.*?)%s",
                                inputLastName.charAt(0),
                                inputLastName.charAt(1),
                                inputLastName.charAt(2),
                                ","), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(formatName);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    Log.d(TAG, "Match found");
                    String findName = String.format("%s%s%s%s",
                            inputLastName.charAt(0),
                            inputLastName.charAt(1),
                            inputLastName.charAt(2),
                            matcher.group(1));
                    Log.d("TAG-find", findName);
                    //Log.d("TAG", String.valueOf(findName.length()));
                    Log.d("TAG-input", inputLastName);
                    //Log.d("TAG", String.valueOf(inputName.length()));
                    if (!(findName.length() == inputLastName.length())) {
                        validation = false;
                        Log.e("TAGV1", String.valueOf(validation));
                    } else {
                        for (int i = 0; i < findName.length(); i++) {
                            Log.e("TAG", String.valueOf(findName.charAt(i)));
                            if (!(findName.charAt(i) == inputLastName.charAt(i))) {
                                validation = false;
                            }
                        }
                        Log.d("TAGV2", String.valueOf(validation));
                    }

//            for (int i = 0; i < inputName.length(); i++) {
                    //Log.d("TAG", String.valueOf(inputName.charAt(i)));
//            }
                } else {
                    Log.d(TAG, "Match not found");
                }
//            Log.e(TAG, comparador);


            } else {
                Log.e("NO CONTIENE APELLIDOI", "formatName");
                validation = false;

            }
        }catch (Exception e){
            Log.e(TAG,"Por ingresar un apellido valido");
            validation = false;
        }



        return validation;
    }

    //  Función que permite dividir los oficios debido a que llegan como un objeto JSON
    public String splitterData(String str, String initDelimiter, String finDelimiter) {
        String data = "";
        String regex = String.format("\\%s(.*?)\\%s", initDelimiter, finDelimiter);
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while (m.find()) {
            data = m.group(1);
            System.out.println(String.format("Value matcher: %s", m.group(1)));
        }
        return data;
    }

}
