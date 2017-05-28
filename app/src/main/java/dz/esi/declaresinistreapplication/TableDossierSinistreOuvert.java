package dz.esi.declaresinistreapplication;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by asus on 19/05/2017.
 */

public class TableDossierSinistreOuvert {

    public static final String TABLE_DOSSIER_SINISTRE_OUVERT="dossierSinistreOuvert";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_TYPE_SINISTRE="typeSinistre";

    public static final String COLUMN_ID_ASSURE="idAssure";
    public static final String COLUMN_CONDUCTEUR="idConducteur";
    public static final String COLUMN_ADVERSAIRE="idAdversaire";

    public static final String COLUMN_DATE_SINISTRE="dateSinistre";
    public static final String COLUMN_HEURE_SINISTRE="heureSinistre";
    public static final String COLUMN_ENDROIT_SINISTRE="endroitSinistre";
    public static final String COLUMN_DESCRIPTION="description";


    public static final String COLUMN_NB_TEMOINS="nbTemoins";
    public static final String COLUMN_NB_BLESSES="nbBlesses";
    public static final String COLUMN_NB_MORTS="nbMorts";

    public static final String COLUMN_PHOTO="photo";
    public static final String COLUMN_VIDEO="video";

    private static final String DATABASE_CREATE="create table "
            +TABLE_DOSSIER_SINISTRE_OUVERT
            +"("
            +COLUMN_ID + " integer primary key autoincrement,"
            +COLUMN_TYPE_SINISTRE+ " text, "
            +COLUMN_ID_ASSURE+ " integer, "
            +COLUMN_CONDUCTEUR +" integer, "
            +COLUMN_ADVERSAIRE + " integer, "

            +COLUMN_DATE_SINISTRE + " text, "
            +COLUMN_HEURE_SINISTRE + " text, "
            +COLUMN_ENDROIT_SINISTRE + " text, "
            +COLUMN_DESCRIPTION + " text, "

            +COLUMN_NB_TEMOINS+ " integer, "
            +COLUMN_NB_BLESSES + " integer, "
            +COLUMN_NB_MORTS+ " integer, "

            +COLUMN_PHOTO+ " text, "
            +COLUMN_VIDEO+ " text "
            +");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DOSSIER_SINISTRE_OUVERT);
        onCreate(database);
    }
}
