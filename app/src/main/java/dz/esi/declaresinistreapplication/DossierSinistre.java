package dz.esi.declaresinistreapplication;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import javax.xml.transform.dom.DOMLocator;

/**
 * Created by asus on 19/05/2017.
 */

public class DossierSinistre implements Parcelable{

    private Expert expert;
    private int idExpert=-1;
    private long id;
    private int numDossier;
    private String typeSinistre;
    private Assure assure;
    private Personne conducteur;
    private Personne adversaire;

    private String dateSinistre;
    private String heureSinistre;
    private String endroitSinistre;
    private String description;

    private int nbTemoins;
    private int nbBlesses;
    private int nbMorts;
    private String photo;
    private String video;
    private String etat;
    private double montantEstime;

    public DossierSinistre(){

    }

    public DossierSinistre(long id, int numDossier, String typeSinistre,  Assure assure,Personne conducteur,
                           Personne adversaire, String dateSinistre, String heureSinistre,
                           String endroitSinistre, String description, int nbTemoins, int nbBlesses,
                           int nbMorts, String photo, String video, String etat, double montantEstime) {
        this.id = id;
        this.numDossier = numDossier;
        this.typeSinistre = typeSinistre;
        this.conducteur = conducteur;
        this.adversaire = adversaire;
        this.assure = assure;
        this.dateSinistre = dateSinistre;
        this.heureSinistre = heureSinistre;
        this.endroitSinistre = endroitSinistre;
        this.description = description;
        this.nbTemoins = nbTemoins;
        this.nbBlesses = nbBlesses;
        this.nbMorts = nbMorts;
        this.photo = photo;
        this.video = video;
        this.etat = etat;
        this.montantEstime = montantEstime;
    }


    public DossierSinistre(Parcel in){
        this.id = in.readLong();
        this.numDossier = in.readInt();
        this.typeSinistre = in.readString();
        this.conducteur =in.readParcelable(Personne.class.getClassLoader());
        this.adversaire =  in.readParcelable(Personne.class.getClassLoader());
        this.assure =in.readParcelable(Assure.class.getClassLoader());
        this.dateSinistre = in.readString();
        this.heureSinistre = in.readString();
        this.endroitSinistre = in.readString();
        this.description = in.readString();
        this.nbTemoins = in.readInt();
        this.nbBlesses = in.readInt();
        this.nbMorts = in.readInt();
        this.photo = in.readString();
        this.video =in.readString();
        this.etat =in.readString();
        this.montantEstime = in.readFloat();
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public Expert getExpert() {
        return expert;
    }

    public long getId() {
        return id;
    }

    public int getNumDossier() {
        return numDossier;
    }

    public int getIdExpert(){
        return this.idExpert;
    }

    public String getTypeSinistre() {
        return typeSinistre;
    }

    public Assure getAssure() {
        return assure;
    }

    public Personne getConducteur() {
        return conducteur;
    }

    public Personne getAdversaire() {
        return adversaire;
    }

    public String getDateSinistre() {
        return dateSinistre;
    }

    public String getHeureSinistre() {
        return heureSinistre;
    }

    public String getEndroitSinistre() {
        return endroitSinistre;
    }

    public String getDescription() {
        return description;
    }

    public int getNbTemoins() {
        return nbTemoins;
    }

    public int getNbBlesses() {
        return nbBlesses;
    }

    public int getNbMorts() {
        return nbMorts;
    }

    public String getPhoto() {
        return photo;
    }

    public String getVideo() {
        return video;
    }

    public String getEtat() {
        return etat;
    }

    public double getMontantEstime() {
        return montantEstime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNumDossier(int numDossier) {
        this.numDossier = numDossier;
    }

    public void setTypeSinistre(String typeSinistre) {
        this.typeSinistre = typeSinistre;
    }

    public void setAssure(Assure assure) {
        this.assure = assure;
    }

    public void setConducteur(Personne conducteur) {
        this.conducteur = conducteur;
    }

    public void setAdversaire(Personne adversaire) {
        this.adversaire = adversaire;
    }

    public void setDateSinistre(String dateSinistre) {
        this.dateSinistre = dateSinistre;
    }

    public void setHeureSinistre(String heureSinistre) {
        this.heureSinistre = heureSinistre;
    }

    public void setEndroitSinistre(String endroitSinistre) {
        this.endroitSinistre = endroitSinistre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNbTemoins(int nbTemoins) {
        this.nbTemoins = nbTemoins;
    }

    public void setNbBlesses(int nbBlesses) {
        this.nbBlesses = nbBlesses;
    }

    public void setNbMorts(int nbMorts) {
        this.nbMorts = nbMorts;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setMontantEstime(double montantEstime) {
        this.montantEstime = montantEstime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(numDossier );
        dest.writeString(typeSinistre);
        dest.writeParcelable(conducteur,flags);
        dest.writeParcelable(adversaire,flags );
        dest.writeParcelable(assure,flags);
        dest.writeString(dateSinistre );
        dest.writeString(heureSinistre );
        dest.writeString(endroitSinistre);
        dest.writeString(description);
        dest.writeInt(nbTemoins);
        dest.writeInt(nbBlesses);
        dest.writeInt(nbMorts );
        dest.writeString(photo);
        dest.writeString(video);
        dest.writeString(etat);
        dest.writeDouble(montantEstime );
    }

    public static final Parcelable.Creator<DossierSinistre> CREATOR = new Parcelable.Creator<DossierSinistre>(){
        @Override
        public DossierSinistre createFromParcel(Parcel source)
        {
            return new DossierSinistre(source);
        }

        @Override
        public DossierSinistre[] newArray(int size)
        {
            return new DossierSinistre[size];
        }
    };





}
