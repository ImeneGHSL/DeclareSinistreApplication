package dz.esi.declaresinistreapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DetailDossierSinistreActivity extends AppCompatActivity {

    private TextView montant;
    private TextView etat;
    private TextView textDescription;
    private TextView nbrTemoins;
    private TextView nbrBlesse;
    private TextView nbrMort;
    private TextView conducteur;
    private TextView adversaire;
    private TextView expert;
    private int id_;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DossierSinistre dossier;
    private StorageReference mStorage, mStorageV, mStorageI;
    private ImageView image;
    private VideoView video;

    LinearLayout linExpert , linAdv , linCond ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dossier_sinistre);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_ = extras.getInt("id_dossier");
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Assure").child(mUser.getUid()).child("dossiers");
        myRef = database.getReference("DossierSinistre").child(mUser.getUid()).child(String.valueOf(id_));

        final DossierSinistre dossierSinistre = getIntent().getExtras().getParcelable(ListeDeclarationsActivity.DOSSIER);
        //Toast.makeText(DetailDossierSinistreActivity.this, dossierSinistre.getEndroitSinistre().toString(), Toast.LENGTH_LONG).show();


        montant = (TextView) findViewById(R.id.textViewMontant);
        etat = (TextView) findViewById(R.id.textViewEtat);
        textDescription = (TextView) findViewById(R.id.textViewDescription);
        nbrTemoins = (TextView) findViewById(R.id.textViewNbrTemoins);
        nbrBlesse = (TextView) findViewById(R.id.textViewNbrBlesse);
        nbrMort = (TextView) findViewById(R.id.textViewNbrMorts);
        conducteur = (TextView) findViewById(R.id.textViewConducteur);
        adversaire = (TextView) findViewById(R.id.textViewAdversaire);
        expert = (TextView) findViewById(R.id.textViewExpert);
        image = (ImageView) findViewById(R.id.imageDossierSinistre);
        video = (VideoView) findViewById(R.id.videoDossiersinistre);
        linAdv = (LinearLayout) findViewById(R.id.linearAdversaire) ;
        linCond = (LinearLayout) findViewById(R.id.linearConducteur) ;
        linExpert = (LinearLayout) findViewById(R.id.linearExpert) ;


        montant.setText(String.valueOf(dossierSinistre.getMontantEstime())+" DA");
        etat.setText("Le dossier est : "+dossierSinistre.getEtat());
        textDescription.setText(dossierSinistre.getDescription());
        nbrTemoins.setText(String.valueOf(dossierSinistre.getNbTemoins()));
        nbrBlesse.setText(String.valueOf(dossierSinistre.getNbBlesses()));
        nbrMort.setText(String.valueOf(dossierSinistre.getNbMorts()));
        if(dossierSinistre.getConducteur() != null){
            conducteur.setText(dossierSinistre.getConducteur().getNom() + ", " + dossierSinistre.getConducteur().getPrenom());
        } else linCond.setVisibility(View.GONE);
        if(dossierSinistre.getAdversaire() != null){
            adversaire.setText(dossierSinistre.getAdversaire().getNom() + ", " + dossierSinistre.getAdversaire().getPrenom());
        }else linAdv.setVisibility(View.GONE);
        if(dossierSinistre.getExpert() != null){
            adversaire.setText(dossierSinistre.getExpert().getNom() + ", " + dossierSinistre.getExpert().getPrenom());
        }else linExpert.setVisibility(View.GONE);


        Uri uriPhoto = Uri.parse(dossierSinistre.getPhoto()) ;
        File filePhoto = new File("" + uriPhoto);
        //filePhoto.getName() ;



        mStorage = FirebaseStorage.getInstance().getReference().child(mUser.getUid());
        mStorageI = mStorage.child("photos").child(filePhoto.getName());

        mStorageI.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(DetailDossierSinistreActivity.this).load(uri).fit().centerCrop().into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        Uri uriVideo = Uri.parse(dossierSinistre.getVideo()) ;
        File fileVideo = new File("" + uriVideo);

        mStorageV = mStorage.child("videos").child(fileVideo.getName());
        mStorageV.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                MediaController mc = new MediaController(DetailDossierSinistreActivity.this);
                video.setMediaController(mc);
                video.setVideoURI(uri);
                video.requestFocus();
                //video.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}

