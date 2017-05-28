package dz.esi.declaresinistreapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 19/05/2017.
 */

public class DataSourceDossierOuvert {

    private SQLiteDatabase database;
    private DataHelperDossierOuvert dbHelper;

    private String[] allColumnsDossierOuvert={
            TableDossierSinistreOuvert.COLUMN_ID,
            TableDossierSinistreOuvert.COLUMN_TYPE_SINISTRE,
            TableDossierSinistreOuvert.COLUMN_ID_ASSURE,
            TableDossierSinistreOuvert.COLUMN_CONDUCTEUR,
            TableDossierSinistreOuvert.COLUMN_ADVERSAIRE,

            TableDossierSinistreOuvert.COLUMN_DATE_SINISTRE ,
            TableDossierSinistreOuvert.COLUMN_HEURE_SINISTRE ,
            TableDossierSinistreOuvert.COLUMN_ENDROIT_SINISTRE ,
            TableDossierSinistreOuvert.COLUMN_DESCRIPTION ,

            TableDossierSinistreOuvert.COLUMN_NB_TEMOINS,
            TableDossierSinistreOuvert.COLUMN_NB_BLESSES ,
            TableDossierSinistreOuvert.COLUMN_NB_MORTS,

            TableDossierSinistreOuvert.COLUMN_PHOTO,
            TableDossierSinistreOuvert.COLUMN_VIDEO};

    private String[] allColumnsPersonne={
            TablePersonne.COLUMN_ID,
            TablePersonne.COLUMN_NOM,
            TablePersonne.COLUMN_PRENOM,
            TablePersonne.COLUMN_CONTRAT_ASSURANCE};

    private String[] allColumnsAssuranceAdversaire={
            TableAssuranceAdversaire.COLUMN_ID ,
            TableAssuranceAdversaire.COLUMN_NUM_CONTRAT,
            TableAssuranceAdversaire.COLUMN_MATRICULE_VEHICULE,
            TableAssuranceAdversaire.COLUMN_COMPAGNIE_ASSURANCE};

    public DataSourceDossierOuvert(Context context){
        dbHelper=new DataHelperDossierOuvert(context);
    }

    public void open() throws SQLException {

        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            database = dbHelper.getReadableDatabase();
        }
    }

    public void openToUpdate(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public long insererDossierSinistre( String typeSinistre, long idAssure ,long idConducteur,
                                         long idAdversaire, String dateSinistre, String heureSinistre,
                                         String endroitSinistre, String description, int nbTemoins, int nbBlesses,
                                         int nbMorts, String photo, String video){;

        ContentValues values=new ContentValues();
        values.put(TableDossierSinistreOuvert.COLUMN_TYPE_SINISTRE,typeSinistre);
        values.put(TableDossierSinistreOuvert.COLUMN_ID_ASSURE,idAssure);
        values.put(TableDossierSinistreOuvert.COLUMN_CONDUCTEUR,idConducteur);
        values.put(TableDossierSinistreOuvert.COLUMN_ADVERSAIRE ,idAdversaire);

        values.put(TableDossierSinistreOuvert.COLUMN_DATE_SINISTRE ,dateSinistre);
        values.put(TableDossierSinistreOuvert.COLUMN_HEURE_SINISTRE ,heureSinistre);
        values.put(TableDossierSinistreOuvert.COLUMN_ENDROIT_SINISTRE,endroitSinistre);
        values.put(TableDossierSinistreOuvert.COLUMN_DESCRIPTION ,description);

        values.put(TableDossierSinistreOuvert.COLUMN_NB_TEMOINS ,nbTemoins);
        values.put(TableDossierSinistreOuvert.COLUMN_NB_BLESSES ,nbBlesses);
        values.put(TableDossierSinistreOuvert.COLUMN_NB_MORTS,nbMorts);
        values.put(TableDossierSinistreOuvert.COLUMN_PHOTO,photo);
        values.put(TableDossierSinistreOuvert.COLUMN_VIDEO,video);

        return database.insert(TableDossierSinistreOuvert.TABLE_DOSSIER_SINISTRE_OUVERT,null,values);
    }

    public long insererPersonne(String nom, String prenom, long idContratAssurance){;

        ContentValues values=new ContentValues();

        values.put(TablePersonne.COLUMN_NOM,nom);
        values.put(TablePersonne.COLUMN_PRENOM,prenom);
        values.put(TablePersonne.COLUMN_CONTRAT_ASSURANCE,idContratAssurance);


        return database.insert(TablePersonne.TABLE_PERSONNE,null,values);
    }

    public long insererAssuranceAdversaire(int numContrat, String matriculeVehiculeAssure,String compagnieAssurance){;

        ContentValues values=new ContentValues();

        values.put(TableAssuranceAdversaire.COLUMN_NUM_CONTRAT,numContrat);
        values.put(TableAssuranceAdversaire.COLUMN_MATRICULE_VEHICULE,matriculeVehiculeAssure);
        values.put(TableAssuranceAdversaire.COLUMN_COMPAGNIE_ASSURANCE,compagnieAssurance);

        return database.insert(TableAssuranceAdversaire.TABLE_ASSURANCE_ADVERSAIRE,null,values);
    }

    public List<DossierSinistre> getAllDossiers(Assure assure){
        List<DossierSinistre> dossiersSinistre=new ArrayList<DossierSinistre>();
        String[] args={String.valueOf(assure.getId())};
        Cursor cursorDossiersSinistre= database.query(TableDossierSinistreOuvert.TABLE_DOSSIER_SINISTRE_OUVERT,allColumnsDossierOuvert, TableDossierSinistreOuvert.COLUMN_ID_ASSURE+"=?",args,null,null,null);
        cursorDossiersSinistre.moveToFirst();
        while (!cursorDossiersSinistre.isAfterLast()){
            dossiersSinistre.add(dossierFromCursor(cursorDossiersSinistre,assure));
            cursorDossiersSinistre.moveToNext();
        }
        cursorDossiersSinistre.close();
        return dossiersSinistre;
    }

    public Personne getPersonne(long idPersonne){
        String[] args={String.valueOf(idPersonne)};
        Cursor cursorPersonne= database.query(TablePersonne.TABLE_PERSONNE,allColumnsPersonne, TablePersonne.COLUMN_ID+"=?",args,null,null,null);
        if(cursorPersonne.moveToNext()) {
            String nom = cursorPersonne.getString(1);
            String prenom = cursorPersonne.getString(2);
            ContratAssurance contratAssurance = null;
            if (cursorPersonne.getInt(3) != -1)
                contratAssurance = getContratAssurance(cursorPersonne.getInt(3));
            return new Personne(idPersonne,nom,prenom,contratAssurance);
        }
        return null;
    }

    public ContratAssurance getContratAssurance(long idContrat){
        String[] args={String.valueOf(idContrat)};
        Cursor cursorContrat= database.query(TableAssuranceAdversaire.TABLE_ASSURANCE_ADVERSAIRE,allColumnsAssuranceAdversaire, TableAssuranceAdversaire.COLUMN_ID+"=?",args,null,null,null);
        if(cursorContrat.moveToNext()) {
            int numContrat = cursorContrat.getInt(1);
            String matriculeVehiculeAssure = cursorContrat.getString(2);
            String compagnieAssurance = cursorContrat.getString(3);
            return new ContratAssurance(idContrat, numContrat, matriculeVehiculeAssure, compagnieAssurance, null);
        }
        return null;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void supprimerDossier(){

    }

    public DossierSinistre getDossierById(long idDossier, Assure assure){
        DossierSinistre dossierSinistre=null;
        String[] args={String.valueOf(idDossier)};
        Cursor cursorDossier= database.query(TableDossierSinistreOuvert.TABLE_DOSSIER_SINISTRE_OUVERT,allColumnsDossierOuvert, TableDossierSinistreOuvert.COLUMN_ID+"=?",args,null,null,null);
        if(cursorDossier.moveToNext()) {
            dossierSinistre=dossierFromCursor(cursorDossier,assure);
            dossierSinistre.setId(idDossier);
            return dossierSinistre;
        }

        return null;
    }

    public DossierSinistre dossierFromCursor(Cursor cursorDossiersSinistre,Assure assure){
        long id=cursorDossiersSinistre.getInt(0);
        int numDossier=-1;
        String typeSinistre=cursorDossiersSinistre.getString(1);
        Personne conducteur=getPersonne(cursorDossiersSinistre.getLong(3));
        Personne adversaire=getPersonne(cursorDossiersSinistre.getLong(4));;
        String dateSinistre=cursorDossiersSinistre.getString(5);
        String heureSinistre=cursorDossiersSinistre.getString(6);
        String endroitSinistre=cursorDossiersSinistre.getString(7);
        String description=cursorDossiersSinistre.getString(8);
        int nbTemoins=cursorDossiersSinistre.getInt(9);
        int nbBlesses=cursorDossiersSinistre.getInt(10);
        int nbMorts=cursorDossiersSinistre.getInt(11);
        String photo=cursorDossiersSinistre.getString(12);
        String video=cursorDossiersSinistre.getString(13);
        String etat="ouvert";
        double montantEstime=0;
        return new DossierSinistre(id,numDossier,typeSinistre,  assure,conducteur,
                adversaire, dateSinistre, heureSinistre,
                endroitSinistre,description,nbTemoins,nbBlesses,
                nbMorts,photo,video,etat,montantEstime);
    }

    public boolean deleteDossierById(long idDossier){
        String[] args={String.valueOf(idDossier)};
        return database.delete(TableDossierSinistreOuvert.TABLE_DOSSIER_SINISTRE_OUVERT, TableDossierSinistreOuvert.COLUMN_ID+"=?",args)>0;
    }

    public void updateContratAssuranceAdversaire(ContratAssurance contratAssurance){

        ContentValues values=new ContentValues();
        Log.e("id contrat =",""+contratAssurance.getId());
        Log.e("recherche contrat =",""+getContratAssurance(contratAssurance.getId()).getCompagnieAssurance());
        String[] args={String.valueOf(contratAssurance.getId())};
        values.put(TableAssuranceAdversaire.COLUMN_NUM_CONTRAT,contratAssurance.getNumContrat());
        values.put(TableAssuranceAdversaire.COLUMN_MATRICULE_VEHICULE,contratAssurance.getMatriculeVehiculeAssure());
        values.put(TableAssuranceAdversaire.COLUMN_COMPAGNIE_ASSURANCE,contratAssurance.getCompagnieAssurance());
        int i= database.update(TableAssuranceAdversaire.TABLE_ASSURANCE_ADVERSAIRE,values, TableAssuranceAdversaire.COLUMN_ID+"=?", args);
        Log.e("nb row =",""+i);

    }

    public void updatePersonne(Personne personne){
        ContentValues values=new ContentValues();
        String[] args={String.valueOf(personne.getId())};
        values.put(TablePersonne.COLUMN_NOM,personne.getNom());
        values.put(TablePersonne.COLUMN_PRENOM,personne.getId());
        database.update(TablePersonne.TABLE_PERSONNE,values,TablePersonne.COLUMN_ID+"=?",args);

    }

    public void updateDossierSinistre(DossierSinistre dossierSinistre){

        ContentValues values = new ContentValues();
        String[] args = {String.valueOf(dossierSinistre.getId())};
        values.put(TableDossierSinistreOuvert.COLUMN_DATE_SINISTRE, dossierSinistre.getDateSinistre());
        values.put(TableDossierSinistreOuvert.COLUMN_HEURE_SINISTRE, dossierSinistre.getHeureSinistre());
        values.put(TableDossierSinistreOuvert.COLUMN_ENDROIT_SINISTRE, dossierSinistre.getEndroitSinistre());
        values.put(TableDossierSinistreOuvert.COLUMN_DESCRIPTION, dossierSinistre.getDescription());

        values.put(TableDossierSinistreOuvert.COLUMN_NB_TEMOINS, dossierSinistre.getNbTemoins());
        values.put(TableDossierSinistreOuvert.COLUMN_NB_BLESSES, dossierSinistre.getNbBlesses());
        values.put(TableDossierSinistreOuvert.COLUMN_NB_MORTS, dossierSinistre.getNbMorts());
        values.put(TableDossierSinistreOuvert.COLUMN_PHOTO, dossierSinistre.getPhoto());
        values.put(TableDossierSinistreOuvert.COLUMN_VIDEO, dossierSinistre.getVideo());
        if (dossierSinistre.getAdversaire() != null) {
            updatePersonne(dossierSinistre.getAdversaire());
            updateContratAssuranceAdversaire(dossierSinistre.getAdversaire().getContratAssurance());
        }
        if (dossierSinistre.getConducteur() != null)
            updatePersonne(dossierSinistre.getConducteur());

        int i = database.update(TableDossierSinistreOuvert.TABLE_DOSSIER_SINISTRE_OUVERT, values, TableDossierSinistreOuvert.COLUMN_ID + "=?", args);

    }
}
