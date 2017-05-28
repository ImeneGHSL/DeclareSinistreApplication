package dz.esi.declaresinistreapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListeDeclarationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LONGITUDE="longitude";
    public static final String LATITUDE="latitude";
    public static final String DISTANCEe="distance";
    public static final String DETAILS="details";
    public static final String EXPERT="expert";


    private LocationManager locationManager;


    public static final String TYPE_SINISTRE="typeSinistre";
    public static final String TYPE_AVEC_TIERS="Avec Tiers";
    public static final String TYPE_SANS_TIERS="sans Tiers";
    public static final String TYPE_STATIONNAIRE="Stationnaire";
    public static final String DOSSIER="dossier";
    public static final String ASSURE="assure";
    public static final  int DECONNEXION_CODE=33;

    private ListView listeDossiersView;
    private List<DossierSinistre> listeDossiersSinistre=new LinkedList<DossierSinistre>();
    private DossierSinistreAdapter dossierSinistreAdapter;
    private DataSourceDossierOuvert dataSourceDossierOuvert;
    private Assure assure;
    FirebaseUser mUser ;
    FirebaseDatabase database ;
    DatabaseReference assureRef ;
    private static String etat="default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_declarations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assure=getIntent().getExtras().getParcelable(ListeDeclarationsActivity.ASSURE);
        if(assure==null)
            getAssure();

        dataSourceDossierOuvert =new DataSourceDossierOuvert(this);

        listeDossiersView= (ListView) findViewById(R.id.listeDossiersView);
        dossierSinistreAdapter = new DossierSinistreAdapter(this,R.layout.dossier_sinistre_item, listeDossiersSinistre);
        listeDossiersView.setAdapter(dossierSinistreAdapter);


        chargerDossiers("default");

        listeDossiersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listeDossiersSinistre.get(position).getEtat()=="ouvert") {
                    Intent formIntent = new Intent(ListeDeclarationsActivity.this, FormActivity.class);
                    formIntent.putExtra(ListeDeclarationsActivity.DOSSIER, listeDossiersSinistre.get(position));
                    startActivityForResult(formIntent,0);
                }
                else{
                    Intent detailIntent = new Intent(ListeDeclarationsActivity.this,DetailDossierSinistreActivity.class);
                    detailIntent.putExtra(ListeDeclarationsActivity.DOSSIER, listeDossiersSinistre.get(position));
                    startActivity(detailIntent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void getAssure(){

        mUser=FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        assureRef=database.getReference("Assure").child(mUser.getUid());
        assureRef.keepSynced(true);
        assureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long cpt = dataSnapshot.getChildrenCount();
                if(cpt>0)
                    assure=dataSnapshot.getValue(Assure.class);
                else
                    Toast.makeText(ListeDeclarationsActivity.this,"echec de chargement des donnes",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    public void chargerDossiers(final String etat){
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference dossierRef=database.getReference("Assure").child(mUser.getUid()).child("dossiers").getRef();
        dossierRef.keepSynced(true);

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(etat.equals(ListeDeclarationsActivity.etat)) {
                    listeDossiersSinistre.clear();
                    if (etat == "default" || etat=="all") {
                        dataSourceDossierOuvert.open();
                        listeDossiersSinistre.addAll(dataSourceDossierOuvert.getAllDossiers(assure));
                        dataSourceDossierOuvert.close();
                    }
                    for (DataSnapshot dossierSnapshot : dataSnapshot.getChildren()) {
                        final DossierSinistre dossierSinistre = dossierSnapshot.getValue(DossierSinistre.class);
                        if(dossierSinistre.getIdExpert()!=-1 ) {
                            DatabaseReference expertRef = database.getReference().child("Expert").child(String.valueOf(dossierSinistre.getIdExpert()));

                            expertRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Expert expert = dataSnapshot.getValue(Expert.class);
                                    dossierSinistre.setExpert(expert);
                                    surveillerProximite(dossierSinistre);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        listeDossiersSinistre.add(dossierSinistre);
                    }

                    dossierSinistreAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        };
        switch (etat){
            case "all":
                dossierRef.addValueEventListener(valueEventListener);
                break;
            case "default" :
                dossierRef.orderByChild("etat").equalTo("enTraitement").addValueEventListener(valueEventListener);
                break;
            default:
                dossierRef.orderByChild("etat").equalTo(etat).addValueEventListener(valueEventListener);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setResult(RESULT_OK, null);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.liste_declarations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            setResult(RESULT_CANCELED, null);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.sansTiers:
                Intent intentSansTiers = new Intent(ListeDeclarationsActivity.this,FormActivity.class) ;
                intentSansTiers.putExtra(TYPE_SINISTRE,TYPE_SANS_TIERS);
                intentSansTiers.putExtra(ASSURE,assure);
                startActivity(intentSansTiers);
            break;
            case R.id.avecTiers:
                Intent intentAvecTiers = new Intent(ListeDeclarationsActivity.this,FormActivity.class) ;
                intentAvecTiers.putExtra(TYPE_SINISTRE,TYPE_AVEC_TIERS);
                intentAvecTiers.putExtra(ASSURE,assure);
                startActivity(intentAvecTiers);
                break;
            case R.id.stationnaire:
                Intent intentStationnaire = new Intent(ListeDeclarationsActivity.this,FormActivity.class) ;
                intentStationnaire.putExtra(TYPE_SINISTRE,TYPE_STATIONNAIRE);
                intentStationnaire.putExtra(ASSURE,assure);
                startActivity(intentStationnaire);
                break;

            case R.id.ouvert:
                etat="ouvert";
                afficherDossierOuverts();
                break;
            case R.id.envoyer:
                etat="envoyé";
                chargerDossiers("envoyé");
                break;
            case R.id.enTraitement:
                etat="enTraitement";
                chargerDossiers("enTraitement");
                break;
            case R.id.accepte:
                etat="accepté";
                chargerDossiers("accepté");
                break;
            case R.id.rejete:
                etat="rejeté";
                chargerDossiers("rejeté");
                break;
            case R.id.all:
                etat="all";
                chargerDossiers("all");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(etat=="ouvert")
            afficherDossierOuverts();
        else
            chargerDossiers(etat);

    }


    @Override
    protected void onPause() {
        dataSourceDossierOuvert.close();
        super.onPause();
    }

    public class DossierSinistreAdapter extends ArrayAdapter {

        private int resource;
        private List<DossierSinistre> listEnd;
        private LayoutInflater inflater;

        public DossierSinistreAdapter(Context context, int resource, List<DossierSinistre> objects) {
            super(context, resource, objects);
            listEnd = objects;
            this.resource = resource;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dossier_sinistre_item, parent, false);
            }

            TextView dateSinistre=(TextView) convertView.findViewById(R.id.sinisteDateTextView);
            TextView endroitSinistre=(TextView) convertView.findViewById(R.id.sinisteEndroitTextView);
            LinearLayout typeSinistre=(LinearLayout)  convertView.findViewById(R.id.typeSinistre);
            switch (listEnd.get(position).getEtat()){
                case "ouvert":
                    typeSinistre.setBackgroundColor(Color.parseColor("#23E5F2"));
                    break;
                case "enTraitement" :
                    typeSinistre.setBackgroundColor(Color.parseColor("#F5CC2A"));
                    break;
                case "envoyé":
                    typeSinistre.setBackgroundColor(Color.parseColor("#F25D1D"));
                    break;
                case "rejeté":
                    typeSinistre.setBackgroundColor(Color.RED);
                    break;
                case "accepté":
                    typeSinistre.setBackgroundColor(Color.parseColor("#00D974"));
                    break;
            }
            dateSinistre.setText(listEnd.get(position).getDateSinistre());
            endroitSinistre.setText(listEnd.get(position).getEndroitSinistre());
            return convertView;
        }
    }

    public void afficherDossierOuverts(){
        dataSourceDossierOuvert.open();
        listeDossiersSinistre.clear();
        listeDossiersSinistre.addAll(dataSourceDossierOuvert.getAllDossiers(assure));
        dataSourceDossierOuvert.close();
        dossierSinistreAdapter.notifyDataSetChanged();

    }

    public void surveillerProximite(DossierSinistre dossierSinistre){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent(this, ProximiteAlert.class);
        intent.putExtra(LONGITUDE,String.valueOf(dossierSinistre.getExpert().getLongitude()));
        intent.putExtra(LATITUDE, String.valueOf(dossierSinistre.getExpert().getLatitude()));
        intent.putExtra(EXPERT,dossierSinistre.getExpert().getNom()+" "+dossierSinistre.getExpert().getPrenom());
        intent.putExtra(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent,0);
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                }
            }
            locationManager.addProximityAlert(dossierSinistre.getExpert().getLatitude(), dossierSinistre.getExpert().getLongitude(), 1000, -1, pending);
        }catch (Exception e){

        }

    }



}
