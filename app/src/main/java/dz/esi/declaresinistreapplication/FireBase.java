package dz.esi.declaresinistreapplication;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by asus on 23/05/2017.
 */



public class FireBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
