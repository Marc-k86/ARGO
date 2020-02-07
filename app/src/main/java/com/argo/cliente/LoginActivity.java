package com.argo.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etUsuario, etContrasena;
    Button bIniciar;
    Spinner spinner;
    ArrayList<String> plaza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.usuario);
        etContrasena = findViewById(R.id.contrasena);
        bIniciar = findViewById(R.id.iniciar);
        spinner = findViewById(R.id.spinner);
        spinner.setEnabled(false);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        bIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDatos();
                Intent dep = new Intent(LoginActivity.this,Inicio.class);
                startActivity(dep);
                finish();
            }
        });
    }

    public void postDatos(){
        final String usuario = etUsuario.getText().toString();
        final String contrasena = etContrasena.getText().toString();

        RequestQueue requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        //Volley.newRequestQueue(getApplicationContext());

        String url = getResources().getString(R.string.url_login);

        StringRequest stringRequest= new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject object = new JSONObject(response);
                            String idCliente = object.getString("as_cliente");
                            String nombreCliente = object.getString("as_nombre");
                            boolean valido = object.getInt("as_access")==1;
                            if (valido){
                                spinner.setEnabled(false);
                                getPlaza();
                                Toast.makeText(getApplicationContext(), idCliente+ " " + nombreCliente + " " + valido, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Verifique usuario y contraseña", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> parametros=new HashMap<>();
                parametros.put("usr_usuario",usuario);
                parametros.put("usr_password",contrasena);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void getPlaza(){
        final String usuario = etUsuario.getText().toString();
        final String contrasena = etContrasena.getText().toString();

        RequestQueue requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        //Volley.newRequestQueue(getApplicationContext());

        String url = getResources().getString(R.string.url_plaza);

        StringRequest stringRequest= new StringRequest(Request.Method.GET, url+"?usr_usuario="+usuario+"&usr_password="+contrasena,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        try {
                               spinner.setEnabled(true);
                               plaza = new ArrayList<>();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("usuario");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String country = jsonObject1.getString("V_RAZON_SOCIAL");
                                    plaza.add(country);
                                }
                                spinner.setAdapter(new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, plaza));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("usr_usuario",usuario);
                parametros.put("usr_password",contrasena);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }
}
