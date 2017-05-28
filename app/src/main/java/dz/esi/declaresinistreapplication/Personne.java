package dz.esi.declaresinistreapplication;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceActivity;

/**
 * Created by asus on 21/05/2017.
 */

public class Personne implements Parcelable{

    private long id;
    private String nom;
    private String prenom;
    private ContratAssurance contratAssurance;

    public Personne(){

    }
    public Personne(long id, String nom, String prenom, ContratAssurance contratAssurance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.contratAssurance = contratAssurance;
    }

    public Personne(Parcel in){
        this.id=in.readLong();
        this.nom=in.readString();
        this.prenom=in.readString();
        this.contratAssurance=in.readParcelable(ContratAssurance.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public ContratAssurance getContratAssurance() {
        return contratAssurance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setContratAssurance(ContratAssurance contratAssurance) {
        this.contratAssurance = contratAssurance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeParcelable(contratAssurance,flags);
    }

    public static final Parcelable.Creator<Personne> CREATOR = new Parcelable.Creator<Personne>(){
        @Override
        public Personne createFromParcel(Parcel source)
        {
            return new Personne(source);
        }

        @Override
        public Personne[] newArray(int size)
        {
            return new Personne[size];
        }
    };
}
