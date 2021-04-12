package com.example.traerapp.Pago;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.traerapp.Constants;
import com.example.traerapp.EmailSender;
import com.example.traerapp.ListCardsActivity;
import com.example.traerapp.Login.Login;
import com.example.traerapp.R;
import com.example.traerapp.datospref.datpref;
import com.example.traerapp.rest.BackendService;
import com.example.traerapp.rest.RetrofitFactory;
import com.example.traerapp.rest.model.CreateChargeResponse;
import com.example.traerapp.utils.Alert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paymentez.android.Paymentez;
import com.paymentez.android.model.Card;
import com.paymentez.android.rest.model.ErrorResponse;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.traerapp.datospref.datpref.getDatosUsuario;


public class Pago extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private TextView valor_final;
    private EditText identif;
    private EditText nombre;
    private EditText celular;
    private EditText correo;
    private EditText direccion;
    private EditText referen;
    private Button Btncomprar;
    private LinearLayout buttonSelectPayment;
    ImageView imageViewCCImage;
    TextView textViewCCLastFour;
    private RadioButton chPayPal;
    private RadioButton chPayMentez;
    String CARD_TOKEN = "";
    int SELECT_CARD_REQUEST = 1004;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    private AlertDialog dialogfopago;


    private ProgressDialog cargando;
    private String Extra1 = "";
    private String Extra2 = "";


    private static final String TAG = "paymentExample";
    public static  final String  PAYPAL_KEY = "AQwvmP5_7rcTEkUg0O8vteepQLdYdA9F-juvPfleUtkNvxkENtxNMyvn0md4GRp3K9_Y5eH5gC-OT1xE";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX; //Configuracion para pruebas
    private static PayPalConfiguration config;
    PayPalPayment thingsToBuy;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);
        getSupportActionBar().hide();
        valor_final = findViewById(R.id.tv_total_fopago);
        identif = findViewById(R.id.et_identif_client);
        nombre = findViewById(R.id.et_client_nom);
        celular = findViewById(R.id.et_client_cel);
        correo = findViewById(R.id.et_client_correo);
        Btncomprar = findViewById(R.id.btn_realizar_pago);
        chPayPal = findViewById(R.id.rb_tarjeta_paypal);
        chPayMentez = findViewById(R.id.rb_tarjeta_Paymentez);
        direccion=findViewById(R.id.et_client_direc);
        referen=findViewById(R.id.et_client_referencia);
        imageViewCCImage = (ImageView) findViewById(R.id.imageViewCCImage);
        textViewCCLastFour = (TextView) findViewById(R.id.textViewCCLastFour);
        request = Volley.newRequestQueue(getApplicationContext());
        final BackendService backendService = RetrofitFactory.getClient().create(BackendService.class);
        configPaypal();

        String[] datLugares = datpref.getDatosViaje(getApplicationContext());
        String[] datViaje = datpref.getIDViaje(getApplicationContext());
        valor_final.setText(String.valueOf(redondearDecimales(Double.valueOf(datLugares[3])*Double.valueOf(datViaje[1]),2)));

        Btncomprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarBlanco()){


                    AlertDialog.Builder mBuilder3 = new AlertDialog.Builder(Pago.this);
                    mBuilder3.setCancelable(false);
                    View mViewp3 = getLayoutInflater().inflate(R.layout.ad_cv,null);
                    Button BtnSiguiente = (Button) mViewp3.findViewById(R.id.btnsiguiente);
                    final EditText cvet = (EditText) mViewp3.findViewById(R.id.et_cv);

                    BtnSiguiente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(!cvet.getText().toString().equals("")){

                                //TODO: ---------------------------------------------------------

                                if(chPayPal.isChecked()){
                                    MakePayment(valor_final.getText().toString());
                                }
                                if(chPayMentez.isChecked()){
                                    //Metodo de PAGO Paymentez;

                                    if(CARD_TOKEN == null || CARD_TOKEN.equals("")){
                                        Alert.show(Pago.this,
                                                "Error",
                                                "No se ha seleccionado una tarjeta");
                                    }else{

                                        final ProgressDialog pd = new ProgressDialog(Pago.this);
                                        pd.setMessage("Procesando Pago");
                                        pd.show();

                                        //double ORDER_AMOUNT = Double.valueOf(valor_final.getText().toString());
                                        String ORDER_ID = ""+System.currentTimeMillis();
                                        String ORDER_DESCRIPTION = "ORDER" + ORDER_ID;
                                        String DEV_REFERENCE = ORDER_ID;



                                        Double totalp = Double.valueOf(valor_final.getText().toString());
                                        Double base_imp = redondearDecimales(totalp / 1.12,2);
                                        Double ivap = redondearDecimales(totalp * 0.12,2);

                                        /*
                                        Double totalp = 112.0;
                                        Double base_imp = 100.00;
                                        Double ivap = 12.00;
                                         */

                                        String[] datosusu = getDatosUsuario(Pago.this);

                                        cargarws("http://161.35.104.61/APPWS/pagar.php?id="+datosusu[5]+"&email="+datosusu[4]+"&amount="+totalp+"&taxable_amount="
                                                +base_imp+"&tax_percentage="+"12"+"&description="+ORDER_DESCRIPTION+"&vat="+ivap+"&cvc="+cvet.getText().toString()+"&token="+CARD_TOKEN);


                            /*
                            backendService.createCharge(datosusu[5], Paymentez.getSessionId(Pago.this),
                                    CARD_TOKEN, ORDER_AMOUNT, DEV_REFERENCE, ORDER_DESCRIPTION).enqueue(new Callback<CreateChargeResponse>() {
                                @Override
                                public void onResponse(Call<CreateChargeResponse> call, retrofit2.Response<CreateChargeResponse> response) {
                                    pd.dismiss();
                                    CreateChargeResponse createChargeResponse = response.body();
                                    if(response.isSuccessful() && createChargeResponse != null && createChargeResponse.getTransaction() != null) {
                                        Alert.show(Pago.this,
                                                "Successful Charge",
                                                "status: " + createChargeResponse.getTransaction().getStatus() +
                                                        "\nstatus_detail: " + createChargeResponse.getTransaction().getStatusDetail() +
                                                        "\nmessage: " + createChargeResponse.getTransaction().getMessage() +
                                                        "\ntransaction_id:" + createChargeResponse.getTransaction().getId());
                                    }else {
                                        Gson gson = new GsonBuilder().create();
                                        try {
                                            ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                                            Alert.show(Pago.this,
                                                    "Error",
                                                    errorResponse.getError().getType());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<CreateChargeResponse> call, Throwable e) {
                                    pd.dismiss();
                                    Alert.show(Pago.this,
                                            "Error",
                                            e.getLocalizedMessage());
                                }
                            });
                            */




                                    }


                                }

                                //TODO: .........................................................

                            }else{
                                Toast.makeText(Pago.this,"Ingrese un cv valido",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    mBuilder3.setView(mViewp3);

                    dialogfopago = mBuilder3.create();
                    dialogfopago.getWindow().setLayout(200, 200);
                    dialogfopago.show();




                }else{
                    Toast.makeText(Pago.this,"Ingrese todos los campos obligatorios",Toast.LENGTH_LONG).show();
                }

            }
        });



        buttonSelectPayment = (LinearLayout)findViewById(R.id.buttonSelectPayment);
        buttonSelectPayment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Pago.this, ListCardsActivity.class);
                startActivityForResult(intent, SELECT_CARD_REQUEST);
            }
        });



    }


    private Boolean validarBlanco(){
        Boolean res = true;
        if(identif.getText().toString().equals("")){
            res=false;
        }
        if(nombre.getText().toString().equals("")){
            res=false;
        }
        if(celular.getText().toString().equals("")){
            res=false;
        }
        if(correo.getText().toString().equals("")){
            res=false;
        }
        return res;
    }



    // Realiza el pedido cuando se acepta la forma de pago
    private void RealizarPedido(String ViaPago,String IDPAGO){
        String[] datLugares = datpref.getDatosViaje(getApplicationContext());
        String[] datViaje = datpref.getIDViaje(getApplicationContext());
        String[] datCoord = datpref.getCoordenadas(getApplicationContext());
        String[] datCoordLleg = datpref.getCoordenadasLlegada(getApplicationContext());
        cargarws("http://161.35.104.61/APPWS/wsSetBoleto.php?IDVIAJE="+datViaje[0]+"&CANTIDAD="+datLugares[3]+"&VALOR="+valor_final.getText().toString()+"&VIAPAGO="+ViaPago+"&CEDULA="+identif.getText().toString()+"&NOMBRE="+nombre.getText().toString()+"&CELULAR="
                +celular.getText().toString()+"&DIRECCION="+direccion.getText().toString()+"&REFERENCIA="+referen.getText().toString()+"&CORREO="+correo.getText().toString()+"&LATITUD="+datCoord[2]+"&LONGITUD="+datCoord[1]+"&LATITUDD="+datCoordLleg[2]+"&LONGITUDD="+datCoordLleg[1]
                +"&IDPAGO="+IDPAGO);

    }

    private void cargarws(String URL) {
        String url = URL;
        url=url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
    }

    private void MakePayment(String valor){
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        thingsToBuy = new PayPalPayment(new BigDecimal(String.valueOf(valor)),"USD","Payment",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent payment = new Intent(this, PaymentActivity.class);
        payment.putExtra(PaymentActivity.EXTRA_PAYMENT,thingsToBuy);
        payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startActivityForResult(payment,REQUEST_CODE_PAYMENT);
    }

    private void configPaypal(){
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(PAYPAL_KEY)
                .merchantName("Paypal Login")
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(chPayPal.isChecked()){
            if(requestCode == REQUEST_CODE_PAYMENT){
                if(resultCode == Activity.RESULT_OK){
                    PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                    if(confirm != null){
                        try{
                            System.out.println(confirm.toJSONObject().toString(4));
                            //System.out.println(confirm.getPayment().toJSONObject().toString(4));
                            //System.out.println(confirm.getPayment().toJSONObject().toString(4));
                            String id = confirm.toJSONObject().getJSONObject("response").getString("id");
                            String state = confirm.toJSONObject().getJSONObject("response").getString("state");
                            Extra1 = id;
                            Extra2 = state;
                            System.out.println("PAgo Realizado : "+id + "  "+ state);
                    /*
                    JSONObject objet = confirm.getPayment().toJSONObject();
                    JSONArray objetArray = (JSONArray) objet.get("response");
                    String id_pago = objetArray.get(1).toString();
                    System.out.println(id_pago);
                    */
                            if(state.equals("approved")){
                                RealizarPedido("PAYPAL",id);
                            }else{
                                Toast.makeText(this,"No se aprobo su pago en PayPal",Toast.LENGTH_LONG).show();
                            }


                        }catch (JSONException e){
                            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(this,"Payment has bee cancelled",Toast.LENGTH_LONG).show();
                }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                    Toast.makeText(this,"error ocurred",Toast.LENGTH_LONG).show();
                }
            }else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT){
                if(resultCode == Activity.RESULT_OK){

                    PayPalAuthorization auth = data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                    if(auth != null){

                        try{
                            Log.i("FuturePaymentExample",auth.toJSONObject().toString(4));
                            String authorization_code = auth.getAuthorizationCode();
                            Log.e("paypal","future payment code received from PayPal :"+authorization_code);

                        }catch (JSONException e){
                            Toast.makeText(this,"failure Occurred",Toast.LENGTH_LONG).show();
                            Log.e("FuturePaymentExample","an extremely unlikely faiture occurred : "+e);
                        }

                    }

                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(this,"payment has been_cancelled",Toast.LENGTH_LONG).show();
                    Log.d("FuturePaymentExample","The user canceled");
                }else if(resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID){
                    Toast.makeText(this,"error_ocurred",Toast.LENGTH_LONG).show();
                }



            }
        }

        if(chPayMentez.isChecked()){
            if (requestCode == SELECT_CARD_REQUEST) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    CARD_TOKEN = data.getStringExtra("CARD_TOKEN");
                    String CARD_TYPE = data.getStringExtra("CARD_TYPE");
                    String CARD_LAST4 = data.getStringExtra("CARD_LAST4");

                    if(CARD_LAST4 != null && !CARD_LAST4.equals("")){
                        textViewCCLastFour.setText("XXXX." + CARD_LAST4);
                        imageViewCCImage.setImageResource(Card.getDrawableBrand(CARD_TYPE));
                    }

                }
            }


        }


    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("Error","Se ha registrado con exito: "+error);
        Toast.makeText(getApplicationContext(),"Ha ocurrido un error, por favor intentelo mas tarde", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResponse(JSONObject response) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
        String status="error";
        String idpmtz="999";
        String amount="";
        String status_detail="";
        try {
            JSONObject invalid = response.getJSONObject("transaction"); // throws JSONException - "invalid" entry doesn't exists
            status = invalid.getString("status");
            String payment_date = invalid.getString("payment_date");
            amount = invalid.getString("amount");
            String authorization_code = invalid.getString("authorization_code");
            String installments = invalid.getString("installments");
            String dev_reference = invalid.getString("dev_reference");
            String message = invalid.getString("message");
            String carrier_code = invalid.getString("carrier_code");
            idpmtz = invalid.getString("id");
            status_detail = invalid.getString("status_detail");

            switch (status)
            {
                case "success":
                    RealizarPedido("PMTZ",idpmtz);
                    builder.setTitle("Procesado con Exito");
                    builder.setMessage("Estaremos en contacto con usted, recuerde que la recogida de pasajeros empieza una hora antes del horario de salida");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });

                    String[] datViaje = datpref.getIDViaje(getApplicationContext());
                    String[] datLugares = datpref.getDatosViaje(getApplicationContext());
                    String[] userlog = datpref.getDatosUsuario(getApplicationContext());

                    EmailSender emailSender=new EmailSender();
                    emailSender.execute("traerapp@gmail.com","traerapp2021",userlog[4],"Confirmación de Pago",
                            "Notificacion de Pago<br><br> " +
                                    "A continuacion los detalles de su pago<br><br>" +
                                    "ID de transaccion : "+idpmtz+"<br><br>"+
                                    "Monto             : "+amount+"<br><br>"+
                                    "Ruta              : "+datLugares[0]+"-"+datLugares[1]+"<br><br>"+
                                    "Fecha             : "+datLugares[2]+"<br><br>"+
                                    "Cantidad          : "+datLugares[3]+"<br><br>","/sdcard/screenshotdemo.jpg");

                    break;
                case "pending":
                    //RealizarPedido("PMTZ",idpmtz);
                    builder.setTitle("Transaccion Pendiente");
                    builder.setMessage("");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    break;
                case "failure":
                    //RealizarPedido("PMTZ",idpmtz);
                    builder.setTitle("Transaccion Rechazada");
                    builder.setMessage(status_detail);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    break;
                default:

                    break;
            }


            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Toast.makeText(getApplicationContext(),"Se ha registrado con exito", Toast.LENGTH_SHORT).show();
    }






}