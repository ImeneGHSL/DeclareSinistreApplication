package dz.esi.declaresinistreapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ProximityNotificationActivity extends AppCompatActivity {

    private String longitude="0";
    private String latitude="0";
    private String distance="0";

    private String expert;
    private DossierSinistre dossierSinistre;


    private TextView textView;

    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_notification);


        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancel(0);
        Intent i = getIntent();

        longitude = i.getStringExtra(ListeDeclarationsActivity.LONGITUDE);
        latitude= i.getStringExtra(ListeDeclarationsActivity.LATITUDE);
        distance=i.getStringExtra(ListeDeclarationsActivity.DISTANCEe);
        expert=i.getExtras().getString(ListeDeclarationsActivity.EXPERT);
        dossierSinistre=i.getExtras().getParcelable(ListeDeclarationsActivity.DOSSIER);

        textView = (TextView) findViewById(R.id.proximiteTextView);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                // Called when a new location is found by the network location provider.
                double startLongitude=location.getLongitude();
                double startLatitude=location.getLatitude();
                double endLongitude=Double.valueOf(longitude);
                double endLatitude=Double.valueOf(latitude);
                float[] result=new float[100];
                Location.distanceBetween(startLatitude,startLongitude,endLatitude,endLongitude,result);

                if(result.length>0) {
                    distance=String.valueOf(result[0]);
                    textView.setText("L'expert "+expert+ " traite un dossier qui vous appartient, il est à proximation de "+distance+".");

                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
