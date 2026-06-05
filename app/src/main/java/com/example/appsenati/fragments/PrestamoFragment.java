package com.example.appsenati.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appsenati.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrestamoFragment extends Fragment {

    Button btnTestWS;
    RequestQueue requestQueue;

    private final String URL = "http://192.168.101.22:3000/api/herramientas/"; //ENDPOINT

    // Contructor
    public PrestamoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asociar el fragment con el XML
        return inflater.inflate(R.layout.fragment_prestamo, container, false);
    }

    private void testWS(){
        //¿Qué nops devolvera en la consulta
        //GET (listar) => [{}, {}, {}]
        //GET (buscador) => {}
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("Resultado", jsonObject.toString());

                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String resultado = "";

                            if (success){
                                //Iterar la clave data = []
                                JSONArray listaHerramientas = jsonObject.getJSONArray("data");

                                //Ahora para terminar ,iteramos recorrenos el JSONArray
                                for (int i = 0; i < listaHerramientas.length(); i++) {
                                    JSONObject herramientas = listaHerramientas.getJSONObject(i);
                                    resultado += herramientas.getString("nombre") + ", ";
                                }

                                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            Log.e("ErrorJSON", "No podemos leer JSON");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorWS", volleyError.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTestWS = view.findViewById(R.id.btnTestWs);

        btnTestWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWS();
            }
        });
    }
}
