package com.example.appsenati.fragments;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appsenati.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecepcionFragment extends Fragment {

    //Variables
    Button btnBuscarHerramienta, btnActualizar;
    RequestQueue requestQueue; // Cola de solicitud
    String condicion = "", tipo = ""; // RadioButton
    private final String URL = "http://192.168.101.22:3000/api/herramientas/"; // Endpoint
    EditText edtNombre, edtMarca, edtDescripcion, edtBuscar;
    RadioButton rbtBueno, rbtRegular, rbtMalo; // Condición
    RadioButton rbtElectrica, rbtManual; // Tipo

    // Contructor
    public RecepcionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asociar el fragment con el XML
        return inflater.inflate(R.layout.fragment_recepcion, container, false);
    }

    private void listarHerramienta(){
        //GET (listar)
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        String id = edtBuscar.getText().toString();
        String urlBuscar = URL + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlBuscar,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {

                                JSONObject herramienta = jsonObject.getJSONObject("data");

                                edtNombre.setText(herramienta.getString("nombre"));
                                edtMarca.setText(herramienta.getString("marca"));
                                edtDescripcion.setText(herramienta.getString("descripcion"));

                                String condicion = herramienta.getString("condicion");
                                String tipo = herramienta.getString("tipo");

                                if (condicion.equals("Bueno"))
                                    rbtBueno.setChecked(true);
                                else if (condicion.equals("Regular"))
                                    rbtRegular.setChecked(true);
                                else
                                    rbtMalo.setChecked(true);

                                if (tipo.equals("Manual"))
                                    rbtManual.setChecked(true);
                                else
                                    rbtElectrica.setChecked(true);
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Es posible que el WS nos envie un codigo 4XX
                        //Log.e("ErrorJSON", volleyError.toString());
                        NetworkResponse response = volleyError.networkResponse;

                        if (response != null && response.data != null){
                            //Código (INT)
                            int statusCode = response.statusCode;

                            //Constenido (STRING > JSON)
                            String messageJSON = new String(response.data);

                            try {
                                JSONObject jsonWS = new JSONObject(messageJSON);
                                String message = jsonWS.getString("message");
                                if (statusCode == 404){
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                Log.e("ErrorJSON", e.toString());
                            }
                        }
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

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

    private void actualizarHerramienta(){
        //GET (listar)
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        String id = edtBuscar.getText().toString();
        String urlActualizar = URL + id;

              condicion = "";

              if (rbtBueno.isChecked()) condicion = "Bueno";
              if (rbtRegular.isChecked()) condicion = "Regular";
              if (rbtMalo.isChecked()) condicion = "Malo";

              tipo = "";

              if (rbtManual.isChecked()) tipo = "Manual";
              if (rbtElectrica.isChecked()) tipo = "Electrica";

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

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                urlActualizar,
                datosEnviar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            int id = response.getInt("id");

                            if (success) {
                                resetUI();
                                Toast.makeText(getContext(), message + " - ID: " + id, Toast.LENGTH_LONG).show();
                                edtNombre.requestFocus();
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {

                            try {

                                String errorJSON = new String(response.data);
                                JSONObject jsonObject = new JSONObject(errorJSON);

                                String message = jsonObject.getString("message");

                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                            } catch (Exception e) {

                                Toast.makeText(getContext(),
                                        "Error al procesar la respuesta",
                                        Toast.LENGTH_LONG).show();

                                Log.e("ErrorJSON", e.toString());
                            }
                        }
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Referencias
        btnBuscarHerramienta = view.findViewById(R.id.btnBuscarHerramienta);
        btnActualizar = view.findViewById(R.id.btnActualizar);

        edtNombre = view.findViewById(R.id.edtNombreH);
        edtMarca = view.findViewById(R.id.edtMarcaH);
        edtDescripcion = view.findViewById(R.id.edtDescripcionH);
        edtBuscar = view.findViewById(R.id.edtBuscar);

        rbtBueno = view.findViewById(R.id.rbtBuenoH);
        rbtRegular = view.findViewById(R.id.rbtRegularH);
        rbtMalo = view.findViewById(R.id.rbtMaloH);

        rbtManual = view.findViewById(R.id.rbtManualH);
        rbtElectrica = view.findViewById(R.id.rbtElectricaH);

        btnBuscarHerramienta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarHerramienta();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarHerramienta();
            }
        });

    }

}
