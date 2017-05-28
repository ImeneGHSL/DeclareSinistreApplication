package dz.esi.declaresinistreapplication;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by asus on 21/05/2017.
 */

public class TableAssuranceAdversaire {

    public static final String TABLE_ASSURANCE_ADVERSAIRE="assuranceAdversaire";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_NUM_CONTRAT="numContrat";
    public static final String COLUMN_MATRICULE_VEHICULE="matriculeVehicule";
    public static final String COLUMN_COMPAGNIE_ASSURANCE="compagnieAssurance";

    private static final String DATABASE_CREATE="create table "
            +TABLE_ASSURANCE_ADVERSAIRE
            +"("
            +COLUMN_ID + " integer primary key autoincrement,"
            +COLUMN_NUM_CONTRAT+ " integer, "
            +COLUMN_MATRICULE_VEHICULE+ " text, "
            +COLUMN_COMPAGNIE_ASSURANCE+ " text "
            +");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSURANCE_ADVERSAIRE);
        onCreate(database);
    }
}
