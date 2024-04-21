package com.example.exameniii;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EntrevistasLista extends AppCompatActivity {
    private FirebaseFirestore db;
    AdaptadorListaEntrevista adaptadorlist;
    private List<Entrevista> entrevistaslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrevistas_lista);

        db=FirebaseFirestore.getInstance();

        cargarLista();

    }

    private void cargarLista() {
        dbEntrevistas();
        adaptadorlist = new AdaptadorListaEntrevista(entrevistaslist, this, new AdaptadorListaEntrevista.OnItemDoubleClickListener(){
            @Override
            public void onItemDoubleClick(Entrevista entrevista){
                Log.d("Test","Test");
            }
        });

        RecyclerView recyclerView = findViewById(R.id.EntrevistasRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorlist);
    }

    private void dbEntrevistas() {
        entrevistaslist = new ArrayList<>();
        db.collection("entrevista")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()){
                                Integer idOrden = 0;
                                String id_fb = document.getId();
                                String descripcion = document.getString("descripcion");
                                String periodista = document.getString("periodista");
                                String fecha = document.getString("fecha");
                                String imagen = document.getString("imagen");
                                String audio = document.getString("audio");
                                entrevistaslist.add(new Entrevista(idOrden,id_fb,descripcion,periodista,fecha,imagen,audio));
                            }
                            adaptadorlist.notifyDataSetChanged();
                        }else{
                            Log.d(TAG, "Error al obtener los datos. ", task.getException());
                        }
                    }
                });

    }
}