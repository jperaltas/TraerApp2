package com.example.traerapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;

//import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.traerapp.Login.Login;
import com.example.traerapp.Mapa.Maps2Activity;
import com.example.traerapp.Mapa.MapsActivity;
import com.example.traerapp.datospref.datpref;
import com.paymentez.android.Paymentez;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener, UploadCallBacks {

    private Spinner inicio;
    private Spinner destino;
    private Button buscar;
    private EditText cantidad;
    private TextView cerrar;
    private TextView fecha;
    private ImageView inicioimg;
    private ImageView destinoimg;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int auxini;
    private int auxfin;

    String[] ListaStr;
    String[] IDTravel;
    String[] IDPrecio;
    ProgressDialog progress;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        inicio = findViewById(R.id.sp_inicio);
        destino = findViewById(R.id.sp_destino);
        buscar = findViewById(R.id.btn_buscar_viajes);
        fecha = findViewById(R.id.fechabusq);
        cantidad = findViewById(R.id.et_cantidad);
        inicioimg=findViewById(R.id.img_inicio);
        destinoimg =findViewById(R.id.img_dest);
        cerrar = findViewById(R.id.txt_cerrar_sesion);
        auxini=0;
        auxfin=1;
        request = Volley.newRequestQueue(getApplicationContext());

        Paymentez.setEnvironment(Constants.PAYMENTEZ_IS_TEST_MODE, Constants.PAYMENTEZ_CLIENT_APP_CODE, Constants.PAYMENTEZ_CLIENT_APP_KEY);


        List<String> list = new ArrayList<String>();
        list.add("CUENCA");
        list.add("GUAYAQUIL");
        list.add("QUITO");
        list.add("LOJA");
        list.add("MACHALA");

        ArrayAdapter<String> adapterini = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        //ArrayAdapter<CharSequence> adapterini = ArrayAdapter.createFromResource(this, R.array.ciudades, android.R.layout.simple_spinner_item);
        adapterini.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inicio.setAdapter(adapterini);

        ArrayAdapter<String> adapterdes = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        //ArrayAdapter<CharSequence> adapterdes = ArrayAdapter.createFromResource(this, R.array.ciudades, android.R.layout.simple_spinner_item);
        adapterdes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destino.setAdapter(adapterdes);
        destino.setSelection(1);


        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datpref.setDatosUsuario(MainActivity.this,"NI","NI","NI","NI","NI","NI");
                Intent intent1 = new Intent (MainActivity.this, Login.class);
                startActivity(intent1);
                finish(); //Finaliza el login
            }
        });



        inicio.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(destino.getSelectedItemPosition()==position){
                    destino.setSelection(auxini);
                    switch(position) {
                        case 0:
                            inicioimg.setImageResource(R.drawable.cuenca5);
                            break;
                        case 1:
                            inicioimg.setImageResource(R.drawable.guayaquil3);
                            break;
                        case 2:
                            inicioimg.setImageResource(R.drawable.quito);
                            break;
                        case 3:
                            inicioimg.setImageResource(R.drawable.loja);
                            break;
                        case 4:
                            inicioimg.setImageResource(R.drawable.machala);
                            break;
                        default:
                            inicioimg.setImageResource(R.drawable.cuenca3);
                            break;
                    }

                    switch(auxini) {
                        case 0:
                            destinoimg.setImageResource(R.drawable.cuenca5);
                            break;
                        case 1:
                            destinoimg.setImageResource(R.drawable.guayaquil3);
                            break;
                        case 2:
                            destinoimg.setImageResource(R.drawable.quito);
                            break;
                        case 3:
                            destinoimg.setImageResource(R.drawable.loja);
                            break;
                        case 4:
                            destinoimg.setImageResource(R.drawable.machala);
                            break;
                        default:
                            destinoimg.setImageResource(R.drawable.cuenca3);
                            break;
                    }
                    auxini=position;

                }else{
                    switch(position) {
                        case 0:
                            inicioimg.setImageResource(R.drawable.cuenca5);
                            break;
                        case 1:
                            inicioimg.setImageResource(R.drawable.guayaquil3);
                            break;
                        case 2:
                            inicioimg.setImageResource(R.drawable.quito);
                            break;
                        case 3:
                            inicioimg.setImageResource(R.drawable.loja);
                            break;
                        case 4:
                            inicioimg.setImageResource(R.drawable.machala);
                            break;
                        default:
                            inicioimg.setImageResource(R.drawable.cuenca3);
                            break;
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        destino.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(inicio.getSelectedItemPosition()==position){



                    inicio.setSelection(auxfin);
                    switch(position) {
                        case 0:
                            destinoimg.setImageResource(R.drawable.cuenca5);
                            break;
                        case 1:
                            destinoimg.setImageResource(R.drawable.guayaquil3);
                            break;
                        case 2:
                            destinoimg.setImageResource(R.drawable.quito);
                            break;
                        case 3:
                            destinoimg.setImageResource(R.drawable.loja);
                            break;
                        case 4:
                            destinoimg.setImageResource(R.drawable.machala);
                            break;
                        default:
                            destinoimg.setImageResource(R.drawable.cuenca3);
                            break;
                    }

                    switch(auxfin) {
                        case 0:
                            inicioimg.setImageResource(R.drawable.cuenca5);
                            break;
                        case 1:
                            inicioimg.setImageResource(R.drawable.guayaquil3);
                            break;
                        case 2:
                            inicioimg.setImageResource(R.drawable.quito);
                            break;
                        case 3:
                            inicioimg.setImageResource(R.drawable.loja);
                            break;
                        case 4:
                            inicioimg.setImageResource(R.drawable.machala);
                            break;
                        default:
                            inicioimg.setImageResource(R.drawable.cuenca3);
                            break;
                    }
                    auxfin=position;



                }else{
                    switch(position) {
                        case 0:
                            destinoimg.setImageResource(R.drawable.cuenca5);
                            break;
                        case 1:
                            destinoimg.setImageResource(R.drawable.guayaquil3);
                            break;
                        case 2:
                            destinoimg.setImageResource(R.drawable.quito);
                            break;
                        case 3:
                            destinoimg.setImageResource(R.drawable.loja);
                            break;
                        case 4:
                            destinoimg.setImageResource(R.drawable.machala);
                            break;
                        default:
                            destinoimg.setImageResource(R.drawable.cuenca3);
                            break;
                    }
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!fecha.getText().toString().equals("Escoger una fecha")){
                    progress = new ProgressDialog(MainActivity.this);
                    progress.setMessage("Cargando.....");
                    progress.show();
                    String inib = inicio.getSelectedItem().toString();
                    String finb = destino.getSelectedItem().toString();
                    String bust12="http://161.35.104.61/APPWS/wsGetViajes.php?INICIO="+inib+"&FIN="+finb+"&FECHA="+fecha.getText().toString();
                    cargarws("http://161.35.104.61/APPWS/wsGetViajes.php?INICIO="+inib+"&FIN="+finb+"&FECHA="+fecha.getText().toString());
                    progress.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(),"Elija una fecha", Toast.LENGTH_SHORT).show();
                }


            }

        });


        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String monthstr = Integer.toString(month);
                String daystr = Integer.toString(day);
                if (month<10){
                    monthstr = "0"+monthstr;
                }
                if (day<10){
                    daystr = "0"+daystr;
                }
                String date = daystr+"/"+monthstr+"/"+year;
                fecha.setText(date);
            }
        };






    }

    private void cargarws(String URL) {
        String url = URL;
        url=url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("Error","Se haregistrado con exito: "+error);
        Toast.makeText(getApplicationContext(),"No se ha encontrado viajes", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.i("Error","Se haregistrado con exito: "+response);

        JSONArray jsonArray = null;
        try {
            jsonArray = response.getJSONArray("VIAJES");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CargarArray(jsonArray);

    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    public void CargarArray(JSONArray jsonArray){
        ArrayList<String> Lista = new ArrayList<>();
        ListaStr=new String[jsonArray.length()];
        IDTravel=new String[jsonArray.length()];
        IDPrecio=new String[jsonArray.length()];
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                //Aquí se obtiene el dato y es guardado en una lista
                Lista.add("HORA:"+json.getString("HORA")+"       PUESTOS LIBRES:"+json.getString("LIBRES"));
                ListaStr[i]="HORA:"+json.getString("HORA")+"      PUESTOS LIBRES:"+json.getString("LIBRES");
                IDTravel[i]=json.getString("ID");
                IDPrecio[i]=json.getString("PRECIO");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Escoje un horario");
        builder.setItems(ListaStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Viaje","Viaje : "+ListaStr[which]);
                String inib2 = inicio.getSelectedItem().toString();
                String finb2 = destino.getSelectedItem().toString();
                datpref.setIDViaje(getApplicationContext(),IDTravel[which],IDPrecio[which],inib2,finb2);
                datpref.setDatosViaje(getApplicationContext(),inicio.getSelectedItem().toString(),destino.getSelectedItem().toString(),fecha.getText().toString(),cantidad.getText().toString());
                Intent intent1 = new Intent (getApplicationContext(), MapsActivity.class);
                startActivity(intent1);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();



    }
}