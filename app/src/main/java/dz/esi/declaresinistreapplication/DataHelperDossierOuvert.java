package dz.esi.declaresinistreapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asus on 19/05/2017.
 */

public class DataHelperDossierOuvert extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="dossierOuvert.db";
    private static final int DATABASE_VERSION=1;

    public DataHelperDossierOuvert(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database){
        TablePersonne.onCreate(database);
        TableAssuranceAdversaire.onCreate(database);
        TableDossierSinistreOuvert.onCreate(database);


    }
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        TablePersonne.onUpgrade(database, oldVersion, newVersion);
        TableAssuranceAdversaire.onUpgrade(database, oldVersion, newVersion);
        TableDossierSinistreOuvert.onUpgrade(database, oldVersion, newVersion);

    }
}
