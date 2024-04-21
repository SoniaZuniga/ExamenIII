package com.example.exameniii;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText descripcion,periodista, fecha;
    private Button Guardar, Foto, Audio, listas;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    ImageView Imagenview;
    private String imagen_cod, grabacion;
    private boolean grabacionaudio = false;
    MediaRecorder   mediaRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        verificarPermisos();

        descripcion = findViewById(R.id.DescripcionTxt);
        periodista = findViewById(R.id.PeriodistaTxt);
        fecha = findViewById(R.id.FechaTxt);
        Guardar = findViewById(R.id.btnGuardar);
        Foto = findViewById(R.id.btnFoto);
        Imagenview = findViewById(R.id.Imagen);
        Audio = findViewById(R.id.btnAudio);
        listas = findViewById(R.id.btnlistado);

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardardatos();
            }
        });
        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {capturarfoto();

            }
        });
        Audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {grabraraudio();
            }
        });
        listas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EntrevistasLista.class);
                startActivity(intent);
            }
        });
    }

    private void grabraraudio() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        if(!grabacionaudio){
           if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED){
               mediaRecorder=new MediaRecorder();
               grabacion=getExternalCacheDir().getAbsolutePath()+"/grabacion.3gp";
               mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
               mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
               mediaRecorder.setOutputFile(grabacion);
               mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

               try{
                   mediaRecorder.prepare();
                   mediaRecorder.start();
                   grabacionaudio=true;
                   Audio.setText("DETENER GRABACION");
                   Toast.makeText(getApplicationContext(), "GRABACION INICIADA", Toast.LENGTH_SHORT).show();
               }catch (Exception ERROR){
                   ERROR.printStackTrace();
               }

           }else{
               ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSIONS);
           }

        }else{
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder= null;
            Audio.setText("Audio");
            Toast.makeText(getApplicationContext(), "GRABACION TERMINADA", Toast.LENGTH_SHORT).show();
        }
    }

    private void capturarfoto() {
        tomarFoto();

    }
    private void verificarPermisos() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }else{
            Toast.makeText(getApplicationContext(),"Error al Tomar Foto", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Imagenview.setImageBitmap(imageBitmap);
            imagen_cod = Img_Cod(imageBitmap);

        }
    }

    private String Img_Cod(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void guardardatos() {
        Integer idorden_s=0;
        String id_s="";
        String descripcion_s = descripcion.getText().toString();
        String periodista_s = periodista.getText().toString();
        String fecha_s = fecha.getText().toString();
        String imagen_s = imagen_cod;
        String audio_s = Aud_Cod(grabacion);

        Entrevista datos = new Entrevista(idorden_s, id_s, descripcion_s, periodista_s, fecha_s, imagen_s, audio_s);

// Add a new document with a generated ID
        db.collection("entrevista")
                .add(datos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        restablecer();
                        Toast.makeText(getApplicationContext(), "REGISTRO GUARDADO", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String Aud_Cod(String grabacion) {
        try{
            File archivoaudio=new File(grabacion);
            long tamaudio=archivoaudio.length();

            ByteArrayOutputStream temporal = new ByteArrayOutputStream();
            FileInputStream file = new FileInputStream(archivoaudio);
            byte[] buffer =  new byte[1024];
            int bytesRead;
            while ((bytesRead = file.read(buffer)) != -1) {
                temporal.write(buffer, 0, bytesRead);
            }
            byte[] audioBytes = temporal.toByteArray();

            return Base64.encodeToString(audioBytes, Base64.DEFAULT);

        }catch (IOException error){
            error.printStackTrace();
            return null;
        }
    }
    private void restablecer(){
        descripcion.setText("");
        periodista.setText("");
        fecha.setText("");
        Imagenview.setVisibility(View.INVISIBLE);


    }
}
