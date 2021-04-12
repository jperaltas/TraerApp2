package com.example.traerapp.Login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.traerapp.MainActivity;
import com.example.traerapp.R;
import com.example.traerapp.UploadCallBacks;
import com.example.traerapp.datospref.datpref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener, UploadCallBacks {

    Button registrar;
    Button ingresar;
    EditText user;
    EditText pass;
    String serv="";
    ProgressDialog progress;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        String[] userlog = datpref.getDatosUsuario(Login.this);

        if(!userlog[0].equals("NI")){
            Intent intent1 = new Intent (Login.this, MainActivity.class);
            startActivity(intent1);
            finish(); //Finaliza el login
        }


        registrar = findViewById(R.id.btn_registrar);
        ingresar = findViewById(R.id.btn_ingresar);
        user = findViewById(R.id.usu_log);
        pass = findViewById(R.id.pass_log);
        request = Volley.newRequestQueue(getApplicationContext());

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(Login.this);
                progress.setMessage("Cargando.....");
                progress.show();
                serv="login";
                cargarws("http://161.35.104.61/APPWS/wsGetCliente.php?USUARIO="+user.getText().toString()+"&PASS="+pass.getText().toString());
                progress.dismiss();
            }
        });


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent1 = new Intent (view.getContext(), MainActivity.class);
                //startActivity(intent1);
                //finish(); //Finaliza el login


                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView1 = inflater.inflate(R.layout.alertdialogregistrar, null);

                final EditText NombreCre = dialogView1.findViewById(R.id.et_nombre_cre);
                final EditText ApellidoCre = dialogView1.findViewById(R.id.et_apellidos_cre);
                final EditText UsuarioCre = dialogView1.findViewById(R.id.et_usuario_cre);
                final EditText ContraCre = dialogView1.findViewById(R.id.et_contra_cre);
                final EditText Contra2 = dialogView1.findViewById(R.id.et_contra_cre2);
                final EditText CorreoCre = dialogView1.findViewById(R.id.et_correo_cre);
                final EditText IdentCre = dialogView1.findViewById(R.id.et_cedula_cre);
                final EditText NumTelCre = dialogView1.findViewById(R.id.et_numero_tel);
                final CheckBox acuerdo = dialogView1.findViewById(R.id.chk_acuerdo);
                final TextView terminos = dialogView1.findViewById(R.id.terminos_condiciones);

                terminos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                        LayoutInflater inflater2 = getLayoutInflater();
                        View dialogView2 = inflater2.inflate(R.layout.activity_acuerdo_legal, null);
                        builder2.setView(dialogView2);
                        AlertDialog alert2 = builder2.create();
                        alert2.show();
                    }
                });

                builder1.setView(dialogView1).setPositiveButton("Crear", null).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                final AlertDialog alert = builder1.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = ((AlertDialog)alert).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(acuerdo.isChecked()){
                                    if(ContraCre.getText().toString().equals(Contra2.getText().toString())){
                                        progress = new ProgressDialog(Login.this);
                                        progress.setMessage("Cargando.....");
                                        progress.show();
                                        cargarws("http://161.35.104.61/APPWS/wsSetCliente.php?IDENTIFICACION="+IdentCre.getText().toString()+"&TIPIDEN=CED&NOMBRES="+NombreCre.getText().toString()+"&APELLIDOS="+ApellidoCre.getText().toString()+"&CELULAR="+NumTelCre.getText().toString()+"&CORREO="+CorreoCre.getText().toString()+"&DIRECCION=%20&REFERENCIA=%20&USUARIO="+UsuarioCre.getText().toString()+"&PASS="+ContraCre.getText().toString());
                                        progress.dismiss();
                                        alert.dismiss();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "La contraseña ingresada no coincide", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Acepte los terminos y condiciones", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alert.show();



            }
        });



    }


    private void cargarws(String URL) {
        String url = URL;
        url=url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(serv.equals("login")){
            Toast.makeText(getApplicationContext(), "Datos incorrectos: Verifique sus credenciales", Toast.LENGTH_LONG).show();
            serv="";
        }else{
            Toast.makeText(getApplicationContext(), "Intentelo mas tarde", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResponse(JSONObject response) {
        if(serv.equals("login")){
            serv="";
            JSONArray jsonArray = null;
            try {
                jsonArray = response.getJSONArray("CLIENTES");
                JSONObject json = jsonArray.getJSONObject(0);
                datpref.setDatosUsuario(Login.this,json.getString("IDENTIFICACION"),json.getString("NOMBRES"),json.getString("APELLIDOS"),json.getString("CELULAR"),json.getString("CORREO"),user.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent1 = new Intent (Login.this, MainActivity.class);
            startActivity(intent1);
            finish(); //Finaliza el login

        }else{
            Toast.makeText(getApplicationContext(), "Se ha creado el usuario con exito", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onProgressUpdate(int percentage) {

    }
}