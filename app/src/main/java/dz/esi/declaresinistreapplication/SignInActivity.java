package dz.esi.declaresinistreapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class SignInActivity extends AppCompatActivity {

    private ChampSaisieView editAdress ;
    private ChampSaisieView editPwd ;
    private Button buttonSignIn ;
    public static  FirebaseAuth mAuth ;
    private LinearLayout connexionLayout;
    private FirebaseUser mUser;
    private FirebaseDatabase database ;
    private DatabaseReference assureRef ;
    private Assure assure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chargerView();
        mAuth = FirebaseAuth.getInstance() ;

    }

    public void chargerView(){
        setContentView(R.layout.activity_sign_in);
        editAdress = (ChampSaisieView) findViewById(R.id.signInAdress) ;
        editPwd = (ChampSaisieView) findViewById(R.id.signInPwd) ;
        buttonSignIn = (Button) findViewById(R.id.siignInButton) ;
        connexionLayout=(LinearLayout)findViewById(R.id.connexionLayout);
        buttonSignIn.setText("Se Connecter");
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValide()) {
                    attente("Connexion en cours");
                    if(true/*checkWifiOnAndConnected()*/)
                        startSingIn();
                    else {
                        echec("Aucune connexion Wifi n'a été détectée");
                    }

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null && assure==null){
            attente("Connexion en cours");
            getAssure();
        }

    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    public boolean isValide(){
        editAdress.verifyChamp();
        editPwd.verifyChamp();
        return editAdress.isValide() && editPwd.isValide();
    }

    private void startSingIn() {
        String adress = editAdress.toString() ;
        String pwd = editPwd.toString() ;

        mAuth.signInWithEmailAndPassword(adress , pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    echec("Echec de connexion");
                }
                else {
                    getAssure();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ListeDeclarationsActivity.DECONNEXION_CODE) {
            if(resultCode==RESULT_OK) {
                finish();
            }
            else {
                if(resultCode==RESULT_CANCELED){
                    mAuth.signOut();
                    chargerView();
                }
            }
        }
    }

    public void getAssure(){

        attente("Chargement de données en cours");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        assureRef=database.getReference("Assure").child(mUser.getUid());
        assureRef.keepSynced(true);
        assureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long cpt = dataSnapshot.getChildrenCount();
                if(cpt>0){
                    if(assure==null) {
                        assure = dataSnapshot.getValue(Assure.class);
                        Intent intent = new Intent(SignInActivity.this, ListeDeclarationsActivity.class);
                        intent.putExtra(ListeDeclarationsActivity.ASSURE, assure);
                        startActivityForResult(intent, ListeDeclarationsActivity.DECONNEXION_CODE);
                    }

                }
                else {
                    echec("Echec de chargement des données, vérifiez votre connexion et ressayer");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {


            }
        });
    }

    public void echec(String errorMessage){
        connexionLayout.removeAllViews();
        connexionLayout.addView(getLayoutInflater().inflate(R.layout.echec_connexion_layout, null));
        ((TextView)findViewById(R.id.causeEchecTextView)).setText(errorMessage);
        buttonSignIn.setText("Ressayer");
    }

    public void attente(String message){
        connexionLayout.removeAllViews();
        connexionLayout.addView(getLayoutInflater().inflate(R.layout.attente_layout, null));
        ((TextView)findViewById(R.id.attenteTextView)).setText(message);
        ProgressBar spinner = (ProgressBar)findViewById(R.id.attenteProgressBar);
        spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#80DAEB"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }




}
