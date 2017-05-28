package dz.esi.declaresinistreapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 21/05/2017.
 */

public class Assure extends  Personne implements Parcelable{

    private String adresse;
    private String tel;


    public Assure(){

    }
    public Assure(int id, String nom, String prenom, ContratAssurance contratAssurance, String adresse, String tel) {
        super(id, nom, prenom, contratAssurance);

        this.adresse = adresse;
        this.tel = tel;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTel() {
        return tel;
    }

    public Assure(Parcel in){
        super(in);
        this.adresse=in.readString();
        this.tel=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(adresse);
        dest.writeString(tel);

    }

    public static final Parcelable.Creator<Assure> CREATOR = new Parcelable.Creator<Assure>(){
        @Override
        public Assure createFromParcel(Parcel source)
        {
            return new Assure(source);
        }

        @Override
        public Assure[] newArray(int size)
        {
            return new Assure[size];
        }
    };




}
