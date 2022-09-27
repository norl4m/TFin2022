package com.marlon.apolo.tfinal2022.herramientas;

import static android.content.Context.CAMERA_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.marlon.apolo.tfinal2022.interfaces.DataStatusMLKit;
import com.marlon.apolo.tfinal2022.model.PoliceRecord;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirebaseMLKitVision {

    private static final String TAG = "mLKitVision";
    private static final String MY_CAMERA_ID = "my_camera_id";
    private ArrayList<String> arrayListTextRecognized;
    private DataValidation dataValidation;

    public ArrayList<String> getArrayListTextRecognized() {
        return arrayListTextRecognized;
    }

    public FirebaseMLKitVision() {
        dataValidation = new DataValidation();

    }

    public InputImage imageFromBitmap(Bitmap bitmap) {
        int rotationDegree = 0;
        // [START image_from_bitmap]
        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
        // [END image_from_bitmap]
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void imageFromMediaImage(Image mediaImage, int rotation) {
        // [START image_from_media_image]
        InputImage image = InputImage.fromMediaImage(mediaImage, rotation);
        // [END image_from_media_image]
    }

    private void imageFromBuffer(ByteBuffer byteBuffer, int rotationDegrees) {
        // [START set_metadata]
        // TODO How do we document the FrameMetadata developers need to implement?
        // [END set_metadata]

        // [START image_from_buffer]
        InputImage image = InputImage.fromByteBuffer(byteBuffer,
                /* image width */ 480,
                /* image height */ 360,
                rotationDegrees,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        );
        // [END image_from_buffer]
    }

    private void imageFromArray(byte[] byteArray, int rotation) {
        // [START image_from_array]
        InputImage image = InputImage.fromByteArray(
                byteArray,
                /* image width */480,
                /* image height */360,
                rotation,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        );
        // [END image_from_array]
    }

    private void imageFromPath(Context context, Uri uri) {
        // [START image_from_path]
        InputImage image;
        try {
            image = InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // [END image_from_path]
    }

    // [START get_rotation]
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
    }
    // [END get_rotation]

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getCompensation(Activity activity, boolean isFrontFacing) throws CameraAccessException {
        // Get the ID of the camera using CameraManager. Then:
        int rotation = getRotationCompensation(MY_CAMERA_ID, activity, isFrontFacing);
    }

    public void recognizeText(InputImage inputImage, DataStatusMLKit dataStatusMLKit) {

        // [START get_detector_default]
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END get_detector_default]

        // [START run_detector]
        Task<Text> result =
                recognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(@NotNull Text visionText) {
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]
//                                processTextBlock(visionText);
                                dataStatusMLKit.readTextRecognized(visionText);
                                // [END get_text]
                                // [END_EXCLUDE]
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }

    public ArrayList<String> processTextRecognized(Text result) {
        ArrayList<String> dataPoliceRecord = new ArrayList<>();
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        if (!resultText.isEmpty()) {
            for (Text.TextBlock block : result.getTextBlocks()) {
                String blockText = block.getText();
                Point[] blockCornerPoints = block.getCornerPoints();
                Rect blockFrame = block.getBoundingBox();
                //displayToast(block.getText());
                //dataPoliceRecord.add(blockText);
//                    displayToast(block.getText());
                for (Text.Line line : block.getLines()) {
                    String lineText = line.getText();
                    Point[] lineCornerPoints = line.getCornerPoints();
                    Rect lineFrame = line.getBoundingBox();
                    Log.d(TAG, line.getText());
                    dataPoliceRecord.add(line.getText() + "*");
                    for (Text.Element element : line.getElements()) {
                        String elementText = element.getText();
                        Point[] elementCornerPoints = element.getCornerPoints();
                        Rect elementFrame = element.getBoundingBox();
                    }
                }
            }
            return dataPoliceRecord;
        } else {
            dataPoliceRecord.add("No hay texto en la imagen");
            return dataPoliceRecord;
        }
        // [END mlkit_process_text_block]
    }

    public PoliceRecord convertTextRecognizedToPoliceReport(ArrayList<String> dataRecognized) {
        StringBuilder data = new StringBuilder();
        PoliceRecord policeRecord = new PoliceRecord("No detectado",
                "No detectado",
                "No detectado",
                "No detectado",
                "No detectado",
                true);
        for (String block : dataRecognized) {
            data = new StringBuilder(data.toString() + dataRecognized.indexOf(block) + "-" + block + "\n");
        }

        for (String dataField : dataRecognized) {
            createPoliceRecord(dataField, policeRecord);
        }

        return policeRecord;
    }

    // Function to print Strings present
    // between any pair of delimeters
    private String getDataBetweenDelimiters(String str) {
        String data = "";
        // Regex to extract the string
        // between two delimiters
        String regex = ":(.*?)\\*";

        // Compile the Regex.
        Pattern p = Pattern.compile(regex);

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Get the subsequence
        // using find() method
        while (m.find()) {
            System.out.println(m.group(1));
            data = m.group(1);
        }
        return data;
    }

    @SuppressLint("SimpleDateFormat")
    private void createPoliceRecord(String text, PoliceRecord policeRecord) {
        String aux;

        // 2 formas de reconocer el no dentro del bloque registra antecedentes
        // y dentro del bloque de la palabra "NO", el bloque solo contiene la palabra "NO"
        // se filta debido a que pueden confundirse con palabras como GOBIER"NO" o "NO"A, "NO"VIEMBRE
        if (text.contains("Registra Antecedentes")) {
//            Log.d(TAG, "Registraaaaaaaaaaaaaaaaaaaaaaaaaa");
            if (getDataBetweenDelimiters(text).contains("NO")) {
                policeRecord.setStatusCriminalRecord(false);
            }
        }
        if (text.contains("NO")) {
            if (text.length() <= 3) {
                //Log.d(TAG, "Registraaaaaaaaaaaaaaaaaaaaaaaaaa");
                //Log.d(TAG, text);
                //Log.d(TAG, String.valueOf(text.length()));
                policeRecord.setStatusCriminalRecord(false);
            }
        }
//        else {
        //Log.d(TAG, String.valueOf(policeRecord.isStatusCriminalRecord()));
//            if (policeRecord.isStatusCriminalRecord()) {
//                policeRecord.setStatusCriminalRecord(true);
        //Log.d(TAG, String.valueOf(policeRecord.isStatusCriminalRecord()));
//            }
//        }

        // contains me permite asegurarme que la cadena que viene contiene los parámetros
        // necesarios para poder validad el record policial
        if (text.contains("Fecha de Emisión")) {
            aux = getDataBetweenDelimiters(text);
            aux = aux.toString().replaceAll("\\|+", "");

            if (aux.length() > 4) {
                policeRecord.setDateCreation(aux);
            }
        }

        if (text.contains("Número de Certificado")) {
            aux = getDataBetweenDelimiters(text);
            aux = aux.toString().replaceAll("\\|+", "");

            if (aux.length() > 4) {
                policeRecord.setCertificateNumber(aux);
            }
        }

        if (text.contains("Tipo de Documento")) {
            aux = getDataBetweenDelimiters(text);
            aux = aux.toString().replaceAll("\\|+", "");

            if (aux.length() > 4) {
                policeRecord.setTypeDocument(aux);
            }
        }

        if (text.contains("No. de")) {
            String ci = getDataBetweenDelimiters(text);
            // Creating a StringBuilder object
            StringBuilder sb = new StringBuilder(ci);
            // Removing the last character
            // of a string
            // Remplaco el caracter | que sale debido a al reconocimiento del enmarcado de los datos
            try {
                sb.deleteCharAt(ci.indexOf("|"));
            } catch (Exception ex) {

            }
            // se remplaza los espacios en blancos generados
            ci = sb.toString().replaceAll("\\s+", "");
            //ci = "1772162698";
            //  Validar el numero de cédula
            Log.d(TAG, String.valueOf(policeRecord.getCi().length()));

            if (ci.length() == 10) {
//                if (dataValidation.validateCI(ci)) {
                policeRecord.setCi(ci);
//                } else {
//                    policeRecord.setCi("No detectado");
//                }
            } else {
                policeRecord.setCi("No detectado");
            }

//-
        }

        if (text.contains("Apellidos y Nombres")) {
            aux = getDataBetweenDelimiters(text);
            aux = aux.toString().replaceAll("\\|+", "");

            if (aux.length() > 4) {
                policeRecord.setNameAndLastName(aux);
            }
        }
    }
}