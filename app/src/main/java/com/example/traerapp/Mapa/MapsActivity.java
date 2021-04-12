package com.example.traerapp.Mapa;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.traerapp.Pago.Pago;
import com.example.traerapp.R;
import com.example.traerapp.datospref.datpref;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerCenter;
    private Button aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        aceptar = findViewById(R.id.btn_aceptar_mapa);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Punto de salida","Coordenadas : "+markerCenter.getPosition().toString());
                datpref.setCoordenadas(getApplicationContext(),markerCenter.getPosition().toString(),markerCenter.getPosition().longitude,markerCenter.getPosition().latitude);
                Intent intent1 = new Intent (getApplicationContext(), Maps2Activity.class);
                startActivity(intent1);
                finish();
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        String[] datLugares = datpref.getDatosViaje(getApplicationContext());
        LatLng sydney = null;
        switch(datLugares[0]) {
            case "CUENCA":
                sydney = new LatLng(-2.899231, -78.999046);
                break;
            case "GUAYAQUIL":
                sydney = new LatLng(-2.151224, -79.898391);
                break;
            case "QUITO":
                sydney = new LatLng(-0.188286, -78.483158);
                break;
            case "LOJA":
                sydney = new LatLng(-4.008995, -79.205355);
                break;
            case "MACHALA":
                sydney = new LatLng(-3.260140, -79.957582);
                break;
            default:
                sydney = new LatLng(-2.899231, -78.999046);
                break;
        }


        markerCenter = mMap.addMarker(new MarkerOptions().position(sydney).title("Recogerme Aquí").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12));



        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                markerCenter.setPosition(mMap.getCameraPosition().target);
            }
        });
    }
}