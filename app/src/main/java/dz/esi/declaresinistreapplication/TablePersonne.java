package dz.esi.declaresinistreapplication;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by asus on 21/05/2017.
 */

public class TablePersonne {

    public static final String TABLE_PERSONNE="personne";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_NOM="nom";
    public static final String COLUMN_PRENOM="prenom";
    public static final String COLUMN_CONTRAT_ASSURANCE="idContratAssurance";

    private static final String DATABASE_CREATE="create table "
            +TABLE_PERSONNE
            +"("
            +COLUMN_ID + " integer primary key autoincrement,"
            +COLUMN_NOM+ " text, "
            +COLUMN_PRENOM+ " text, "
            +COLUMN_CONTRAT_ASSURANCE+ " integer "
            +");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONNE);
        onCreate(database);
    }
}


