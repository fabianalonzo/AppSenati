package com.example.appsenati.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
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

import java.lang.ref.ReferenceQueue;

public class PrestamoFragment extends Fragment {

    Button btnTestWS, btnGuardarHerramienta;
    RequestQueue requestQueue; // Cola de solicitud
    String condicion = "", tipo = ""; // RadioButton
    private final String URL = "http://192.168.101.22:3000/api/herramientas/"; // Endpoint
    EditText edtNombre, edtMarca, edtDescripcion;
    RadioButton rbtBueno, rbtRegular, rbtMalo; // Condición
    RadioButton rbtElectrica, rbtManual; // Tipo

    // Contructor
    public PrestamoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asociar el fragment con el XML
        return inflater.inflate(R.layout.fragment_prestamo, container, false);
    }

    /**
     * Este método retornara TRUE cuando el formulario este listo para el registro (todos los campos tendran datos)
     * @return
     */
    private boolean readyUI() {
        boolean ready = true;

        if ( edtNombre.getText().toString().isEmpty() ) { ready = false; };
        if ( edtMarca.getText().toString().isEmpty() ) { ready = false; };
        if ( edtDescripcion.getText().toString().isEmpty() ) { ready = false; };

        if ( !rbtBueno.isChecked() && !rbtRegular.isChecked() && !rbtMalo.isChecked()) { ready = false; };
        if ( !rbtManual.isChecked() && !rbtElectrica.isChecked()) { ready = false; };

        return ready;
    }

    /**
     * Regresa la UI (formulario) a su estado original
     */
    private void resetUI() {
        edtNombre.setText(null);
        edtMarca.setText("");
        edtDescripcion.setText("");

        rbtBueno.setChecked(false);
        rbtRegular.setChecked(false);
        rbtMalo.setChecked(false);
        condicion = "";

        rbtManual.setChecked(false);
        rbtElectrica.setChecked(false);
        tipo = "";
    }

    private void registrarHerramienta() {
        // 0. Preparar el JSON
        // Definir que condición tiene
        condicion = "";
        if ( rbtBueno.isChecked()) { condicion = "Bueno"; }
        if ( rbtRegular.isChecked()) { condicion = "Regular"; }
        if ( rbtMalo.isChecked()) { condicion = "Malo"; }

        tipo = "";
        if ( rbtManual.isChecked()) { tipo = "Manual"; }
        if ( rbtElectrica.isChecked()) { tipo = "Eléctrica"; }

        JSONObject datosEnviar = new JSONObject();
        try {
            datosEnviar.put("nombre", edtNombre.getText().toString()); // EditText
            datosEnviar.put("marca", edtMarca.getText().toString()); // EditText
            datosEnviar.put("descripcion", edtDescripcion.getText().toString()); // EditText
            datosEnviar.put("condicion", condicion);
            datosEnviar.put("tipo", tipo);
        } catch (Exception e) {
            Log.e("ErrorJSON", e.toString());
        }

        // 1. Canal de comunicación
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        // 2. Consumir el WS > Lectura de datos (JSON resultado)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                datosEnviar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");
                            int id = jsonObject.getInt("id");

                            if (success) {
                                resetUI();
                                Toast.makeText(getContext(), message + " - ID: " + id, Toast.LENGTH_LONG).show();
                                edtNombre.requestFocus();
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Cuando no realiza la operación...
                        NetworkResponse response = volleyError.networkResponse;

                        // Si existe un error...
                        if ( response != null && response.data != null ) {
                            // STATUS CODE
                            int statusCode = response.statusCode;
                            // MESSAGE DETAIL
                            String errorJSON = new String(response.data);

                            // Mostrar el message (JSON) en la pantalla TOAST
                            Log.d("ErrorStatusCode", String.valueOf(statusCode));
                            Log.d("ErrorDetallado", errorJSON);
                        }

                        // Log.e("ErrorWS", volleyError.toString());
                    }
                }

        );

        // 3. Ejecución
        requestQueue.add(jsonObjectRequest);
    }

    private void testWS() {
        // ¿Qué nos devolverá la consulta / request?
        // GET (listar) => [{}, {}, {}]
        // GET (buscador) => {}
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String resultado = "";

                            if (success) {
                                // JSONArrayRequest = solicitud / pedido
                                // JSONArray = contenedor
                                // Iterar la clave data = []
                                JSONArray listaHerramientas = jsonObject.getJSONArray("data");

                                // Ahora para terminar, iteramos (recorremos el JSONArray)
                                for (int i = 0; i < listaHerramientas.length(); i++) {
                                    JSONObject herramienta = listaHerramientas.getJSONObject(i);
                                    resultado += herramienta.getString("nombre") + ", ";
                                }

                                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
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

        // REFERENCIAS
        btnTestWS = view.findViewById(R.id.btnTestWs);
        btnGuardarHerramienta = view.findViewById(R.id.btnGuardarHerramienta);

        edtNombre = view.findViewById(R.id.edtNombre);
        edtMarca = view.findViewById(R.id.edtMarca);
        edtDescripcion = view.findViewById(R.id.edtDescripcion);

        rbtBueno = view.findViewById(R.id.rbtBueno);
        rbtRegular = view.findViewById(R.id.rbtRegular);
        rbtMalo = view.findViewById(R.id.rbtMalo);

        rbtManual = view.findViewById(R.id.rbtManual);
        rbtElectrica = view.findViewById(R.id.rbtElectrica);

        btnTestWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWS();
            }
        });

        btnGuardarHerramienta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyUI()) {
                    // Confirmación de proceso
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("App Herramientas");
                    builder.setMessage("¿Seguro de proceder con el registro?");

                    builder.setPositiveButton("Si", (a, b) -> {
                        registrarHerramienta();
                    });
                    builder.setNegativeButton("No", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(getContext(), "Complete el formulario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}