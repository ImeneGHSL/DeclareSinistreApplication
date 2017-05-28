package dz.esi.declaresinistreapplication;

import android.os.Parcelable;

/**
 * Created by asus on 27/05/2017.
 */

public class Expert extends Personne{
    private double longitude;
    private double latitude;

    public Expert(){

    }


    public Expert(long id, String nom, String prenom, ContratAssurance contratAssurance, double longitude, double latitude) {
        super(id, nom, prenom, contratAssurance);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Expert(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


}
