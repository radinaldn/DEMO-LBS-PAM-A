package id.topapp.radinaldn.demolbspama.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import id.topapp.radinaldn.demolbspama.R;
import id.topapp.radinaldn.demolbspama.response.ResponsePesanan;
import id.topapp.radinaldn.demolbspama.rest.ApiClient;
import id.topapp.radinaldn.demolbspama.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etMakanan, etPorsi, etKet;
    TextView tvLat, tvLng;
    Button btKirim, btBatal;

    LocationManager lm;
    LocationListener locationListener;

    ApiInterface apiService;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        etMakanan = findViewById(R.id.etMakanan);
        etPorsi = findViewById(R.id.etPorsi);
        etKet = findViewById(R.id.etKet);

        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);

        btKirim = findViewById(R.id.btKirim);
        btBatal = findViewById(R.id.btBatal);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        btKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesanan();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            // ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else{
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }
    }


    private void kirimPesanan() {
        String makanan = etMakanan.getText().toString();
        String porsi = etPorsi.getText().toString();
        String ket = etKet.getText().toString();
        String lat = tvLat.getText().toString();
        String lng = tvLng.getText().toString();

        if (lat!=null && !lat.equalsIgnoreCase("0")){
            apiService.doPemesanan(makanan, porsi, ket, lat, lng)
                    .enqueue(new Callback<ResponsePesanan>() {
                        @Override
                        public void onResponse(Call<ResponsePesanan> call, Response<ResponsePesanan> response) {

                            if (response.isSuccessful()){
                                if (response.body().getStatus().equalsIgnoreCase("success")){
                                    Toast.makeText(getApplicationContext(), response.body().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    restartActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), response.body().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponsePesanan> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }

    }

    private void restartActivity() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null){
                String lat = String.valueOf(location.getLatitude());
                tvLat.setText(lat);

                String lng = String.valueOf(location.getLongitude());
                tvLng.setText(lng);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusString = "";

            switch (status){
                case LocationProvider.AVAILABLE:
                    statusString = "Available";
                    case LocationProvider.OUT_OF_SERVICE:
                        statusString = "Out Of Service";
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            statusString = "Temporarily Unavailable";
            }

            Toast.makeText(getBaseContext(),
                    "Provider : "+provider+" "+statusString,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider : "+provider+ " enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider : "+provider+ " disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
