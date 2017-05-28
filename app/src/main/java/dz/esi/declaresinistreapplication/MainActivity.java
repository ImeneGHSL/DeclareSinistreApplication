package dz.esi.declaresinistreapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {

    //Button declarerSinistreButton ;
    private LinearLayout pricipalView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions=new String[2];
        boolean requieredPermission=false;

        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions[0]=android.Manifest.permission.CAMERA;
                requieredPermission=true;
        }

        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions[1]=android.Manifest.permission.READ_EXTERNAL_STORAGE;
            requieredPermission=true;
        }

        if(requieredPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(permissions,5);
        }
        pricipalView=(LinearLayout) findViewById(R.id.pricipalView);

        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(3000);
        pricipalView.setAlpha(1f);
        pricipalView.startAnimation(animation1);


        pricipalView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignInActivity.class) ;
                startActivity(intent);
            }
        });


    }


}
