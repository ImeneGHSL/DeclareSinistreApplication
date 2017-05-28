package dz.esi.declaresinistreapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 21/05/2017.
 */

public class ContratAssurance implements Parcelable{

    private long id;
    private int numContrat;
    private String matriculeVehiculeAssure;
    private String compagnieAssurance;
    private String marqueVehicule;

    public ContratAssurance(){

    }
    public ContratAssurance(long id, int numContrat, String matriculeVehiculeAssure, String compagnieAssurance,String marqueVehicule) {
        this.id = id;
        this.numContrat = numContrat;
        this.matriculeVehiculeAssure = matriculeVehiculeAssure;
        this.compagnieAssurance=compagnieAssurance;
        this.marqueVehicule=marqueVehicule;
    }

    public ContratAssurance(Parcel in) {
        this.id = in.readLong();
        this.numContrat=in.readInt();
        this.matriculeVehiculeAssure=in.readString();
        this.compagnieAssurance=in.readString();
        this.marqueVehicule=in.readString();
    }

    public long getId() {
        return id;
    }

    public int getNumContrat() {
        return numContrat;
    }

    public String getMatriculeVehiculeAssure() {
        return matriculeVehiculeAssure;
    }

    public String getCompagnieAssurance() {
        return compagnieAssurance;
    }

    public String getMarqueVehicule() {
        return marqueVehicule;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNumContrat(int numContrat) {
        this.numContrat = numContrat;
    }

    public void setMatriculeVehiculeAssure(String matriculeVehiculeAssure) {
        this.matriculeVehiculeAssure = matriculeVehiculeAssure;
    }

    public void setCompagnieAssurance(String compagnieAssurance) {
        this.compagnieAssurance = compagnieAssurance;
    }

    public void setMarqueVehicule(String marqueVehicule) {
        this.marqueVehicule = marqueVehicule;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(numContrat);
        dest.writeString(matriculeVehiculeAssure);
        dest.writeString(compagnieAssurance);
        dest.writeString(marqueVehicule);

    }

    public static final Parcelable.Creator<ContratAssurance> CREATOR = new Parcelable.Creator<ContratAssurance>(){
        @Override
        public ContratAssurance createFromParcel(Parcel source)
        {
            return new ContratAssurance(source);
        }

        @Override
        public ContratAssurance[] newArray(int size)
        {
            return new ContratAssurance[size];
        }
    };
}
