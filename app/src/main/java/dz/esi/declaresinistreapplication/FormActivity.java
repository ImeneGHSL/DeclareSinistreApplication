package dz.esi.declaresinistreapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedList;

public class FormActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Button suivantButton;
    private String typeSinistre;
    private DossierSinistre dossierSinistre;
    private Assure assure;
    private DatabaseReference databaseRef;
    private FirebaseUser  mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        dossierSinistre= getIntent().getExtras().getParcelable(ListeDeclarationsActivity.DOSSIER);
        if(dossierSinistre!=null) {
            typeSinistre = dossierSinistre.getTypeSinistre();
            assure = dossierSinistre.getAssure();
        }

        else {
            typeSinistre = getIntent().getExtras().getString(ListeDeclarationsActivity.TYPE_SINISTRE);
            assure=getIntent().getExtras().getParcelable(ListeDeclarationsActivity.ASSURE);
        }

        suivantButton=(Button)findViewById(R.id.suivantButton);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),typeSinistre);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        suivantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).isValideForm()) {
                if(mViewPager.getCurrentItem()+2== mSectionsPagerAdapter.getCount()){
                    suivantButton.setText("Terminer");
                }
                if (mViewPager.getCurrentItem() + 1 < mSectionsPagerAdapter.getCount())
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                else{
                    if(mSectionsPagerAdapter.isAcceptingSend()) {
                        mSectionsPagerAdapter.sendDeclarationToFirebase();

                    }
                    else {
                        mSectionsPagerAdapter.mettreAJourDossierSinistre();
                    }
                    finish();
                }
            }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position>0 && !mSectionsPagerAdapter.getItem(position-1).isValideForm()) {
                    mViewPager.setCurrentItem(position - 1);
                }
                if(position<mSectionsPagerAdapter.getCount()-1) {
                    suivantButton.setText("Suivant");
                }
                else {
                    suivantButton.setText("Terminer");
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem()-1>=0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            suivantButton.setText("Suivant");
        }
        else
            super.onBackPressed();

    }

    public abstract static class Form extends Fragment{
        public abstract boolean isValideForm();
    }

    /* Fragment pour propriétaire voiture*/

    public static class ProprietaireVoitureFragment extends Form {

        private View rootView;
        private ChampSaisieView nameProprietaire;
        private ChampSaisieView prenomProprietaire;
        private ChampSaisieView adresse;
        private ChampSaisieView tel;
        public ProprietaireVoitureFragment() {

        }

        public static ProprietaireVoitureFragment newInstance(Assure assure) {
            ProprietaireVoitureFragment fragment = new ProprietaireVoitureFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.ASSURE,assure);
            fragment.setArguments(args);
            return fragment;
        }

        public String getText(){
            String text="_____Informations de l' Assuré\n";
            text+="Nom : "+nameProprietaire+"\n";
            text+="Prénom : "+prenomProprietaire+"\n";
            if(!adresse.isEmpty())
                text+="Adresse : "+adresse+"\n";
            if(!tel.isEmpty())
                text+="Tél : "+tel+"\n";
            text+="\n";
            return text;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.proprietaire_voiture_fragment, container, false);
                nameProprietaire = (ChampSaisieView) rootView.findViewById(R.id.nameProprietaire);
                prenomProprietaire = (ChampSaisieView) rootView.findViewById(R.id.prenomProprietaire);
                adresse = (ChampSaisieView) rootView.findViewById(R.id.adresseProprietaire);
                tel = (ChampSaisieView) rootView.findViewById(R.id.telProprietaire);

                chargerView((Assure)getArguments().getParcelable(ListeDeclarationsActivity.ASSURE));
            }
            return rootView;
        }

        public boolean isValideForm(){
            nameProprietaire.verifyChamp();
            prenomProprietaire.verifyChamp();
            adresse.verifyChamp();
            tel.verifyChamp();
            return nameProprietaire.isValide()&&prenomProprietaire.isValide()&&adresse.isValide()&&tel.isValide();

        }

        public void chargerView(Assure assure){
            this.nameProprietaire.setText(assure.getNom());
            this.prenomProprietaire.setText(assure.getPrenom());
            this.adresse.setText(assure.getAdresse());
            this.tel.setText(assure.getTel());
        }

    }

    /* Fragment pour conducteur voiture*/

    public static class ConducteurVoitureFragment extends Form {

        private View rootView;
        private ChampSaisieView nameConducteur;
        private ChampSaisieView prenomConducteur;
        private Switch isConducteur;

        public ConducteurVoitureFragment() {
        }

        public static ConducteurVoitureFragment newInstance(DossierSinistre dossierSinistre) {
            ConducteurVoitureFragment fragment = new ConducteurVoitureFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.conducteur_voiture_fragment, container, false);
                nameConducteur = (ChampSaisieView) rootView.findViewById(R.id.nameConducteur);
                prenomConducteur = (ChampSaisieView) rootView.findViewById(R.id.prenomConducteur);

                isConducteur = (Switch) rootView.findViewById(R.id.switchAut);
                isConducteur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        nameConducteur.setEnabled(buttonView.isChecked());
                        prenomConducteur.setEnabled(buttonView.isChecked());

                    }
                });

                if(getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)!=null && ((DossierSinistre)getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)).getConducteur()!=null) {
                    chargerView(((DossierSinistre) getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)).getConducteur());
                    isConducteur.setChecked(true);
                }
            }
            return rootView;
        }


        public boolean isValideForm(){
            if(!isConducteur.isChecked())
                return true;
            else {
                nameConducteur.verifyChamp();
                prenomConducteur.verifyChamp();
                return nameConducteur.isValide()&&prenomConducteur.isValide();
            }
        }

        public String getText(){
            String text="";
            if(isConducteur.isChecked()) {
                text = "_____Informations sur le Conducteur\n";
                text += "Nom : " + nameConducteur + "\n";
                text += "Prénom : " + prenomConducteur+ "\n\n";
            }
            return text;
        }

        public void chargerView(Personne conducteur){
            this.nameConducteur.setText(conducteur.getNom());
            this.prenomConducteur.setText(conducteur.getPrenom());

        }
    }

    /* Fragment pour propriétaire voiture*/

    public static class AdversaireFragment extends Form {

        private View rootView;
        private ChampSaisieView nameAdversaire;
        private ChampSaisieView prenomAdversaire;
        private ChampSaisieView matricule;
        private ChampSaisieView contratAssurance;
        private Spinner assureurSpinner;
        public AdversaireFragment() {

        }

        public static AdversaireFragment newInstance(DossierSinistre dossierSinistre) {
            AdversaireFragment fragment = new AdversaireFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
            fragment.setArguments(args);
            return fragment;
        }

        public String getText(){
            String text="_____Informations sur l'adversaire\n";
            text+="Nom : "+nameAdversaire+"\n";
            text+="Prénom : "+prenomAdversaire+"\n";
            text+="Assureur : "+assureurSpinner.getSelectedItem().toString()+"\n";
            if(!contratAssurance.isEmpty())
                text+="N° Contrat Assurance : "+contratAssurance+"\n";
            if(!matricule.isEmpty())
                text+="Matricule : "+matricule+"\n";
            text+="\n";
            return text;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.adversaire_fragment, container, false);
                nameAdversaire = (ChampSaisieView) rootView.findViewById(R.id.nameAdversaire);
                prenomAdversaire = (ChampSaisieView) rootView.findViewById(R.id.prenomAdversaire);
                matricule = (ChampSaisieView) rootView.findViewById(R.id.matriculeAdversaire);
                contratAssurance = (ChampSaisieView) rootView.findViewById(R.id.contratAdversaire);
                assureurSpinner=(Spinner) rootView.findViewById(R.id.assureurSpinner);
                assureurSpinner.setSelection(0);
                if(getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)!=null && ((DossierSinistre)getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)).getAdversaire()!=null)
                    chargerView(((DossierSinistre)getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)).getAdversaire());
            }
            return rootView;
        }

        public boolean isValideForm(){
            prenomAdversaire.verifyChamp();
            nameAdversaire.verifyChamp();
            matricule.verifyChamp();
            contratAssurance.verifyChamp();
            return prenomAdversaire.isValide()&&nameAdversaire.isValide()&&matricule.isValide()&&contratAssurance.isValide();
        }

        public void chargerView(Personne adversaire){
            this.nameAdversaire.setText(adversaire.getNom());
            this.prenomAdversaire.setText(adversaire.getPrenom());
            this.matricule.setText(adversaire.getContratAssurance().getMatriculeVehiculeAssure());
            this.contratAssurance.setText(String.valueOf(adversaire.getContratAssurance().getNumContrat()));
            int i=0;
            while (i<assureurSpinner.getChildCount()){
                if(assureurSpinner.getItemAtPosition(i).toString().equals(adversaire.getContratAssurance().getCompagnieAssurance()))
                    assureurSpinner.setSelection(i);
                i++;
            }

        }

    }

    /* Fragment pour detail sinistre */

    public static class DetailSinistreFragment extends Form {
        private View rootView;
        private ChampSaisieView endroitSinistre;


        private EditText textDate ;
        private EditText textTime ;
        private EditText textDescription;

        Calendar myCalendar = Calendar.getInstance();
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY) ;
        int minute = myCalendar.get(Calendar.MINUTE) ;
        DatePickerDialog.OnDateSetListener date ;

        public DetailSinistreFragment() {
        }

        public static DetailSinistreFragment newInstance(DossierSinistre dossierSinistre) {
            DetailSinistreFragment fragment = new DetailSinistreFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.detail_sinistre_fragment, container, false);

                endroitSinistre = (ChampSaisieView) rootView.findViewById(R.id.endroitSinistre);
                textDate = (EditText) rootView.findViewById(R.id.editTextDate);
                textTime = (EditText) rootView.findViewById(R.id.editTextTime);
                textDescription = (EditText) rootView.findViewById(R.id.editTextDescription);
                textDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(getActivity(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                textTime.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);

                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                textTime.setText(selectedHour + ":" + selectedMinute);
                            }
                        }, hour, minute, true);
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });

                date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        textDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                    }

                };

                if(getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)!=null)
                    chargerView(((DossierSinistre)getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)));
            }

            return rootView;
        }

        public boolean isValideForm(){
            endroitSinistre.verifyChamp();
            return endroitSinistre.isValide();
        }

        public String getText(){
            String text="_____Détails sur le Sinistre\n";
            text+="Endroit : "+endroitSinistre+"\n";
            if(textDate.getText()!=null && !textDate.getText().toString().isEmpty())
                text+="Date : "+textDate.getText().toString()+"\n";
            if(textTime.getText()!=null && !textTime.getText().toString().isEmpty())
                text+="heure : "+textTime.getText().toString()+"\n";
            if(textDescription.getText()!=null && !textDescription.getText().toString().isEmpty())
                text+="Description : "+textDescription.getText().toString()+"\n";
            text+="\n";
            return text;
        }

        public void chargerView(DossierSinistre dossierSinistre){
            this.textDate.setText(dossierSinistre.getDateSinistre());
            this.textTime.setText(dossierSinistre.getHeureSinistre());
            this.endroitSinistre.setText(dossierSinistre.getEndroitSinistre());
            this.textDescription.setText(dossierSinistre.getDescription());
        }
    }


    /* Fragment pour assurance et voiture*/

    public static class VoitureAssuranceFragment extends Form {
        private View rootView;
        private ChampSaisieView marqueVoiture;
        private ChampSaisieView matricule;
        private ChampSaisieView contratAssurance;

        public VoitureAssuranceFragment() {
        }

        public static VoitureAssuranceFragment newInstance(Assure assure) {
            VoitureAssuranceFragment fragment = new VoitureAssuranceFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.ASSURE,assure);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.voiture_assurance_fragment, container, false);
                marqueVoiture = (ChampSaisieView) rootView.findViewById(R.id.marqueVoiture);
                matricule = (ChampSaisieView) rootView.findViewById(R.id.matricule);
                contratAssurance = (ChampSaisieView) rootView.findViewById(R.id.contratAssurance);
                chargerView(((Assure)getArguments().getParcelable(ListeDeclarationsActivity.ASSURE)).getContratAssurance());
            }
            return rootView;
        }

        public boolean isValideForm(){
            marqueVoiture.verifyChamp();
            matricule.verifyChamp();
            contratAssurance.verifyChamp();
            return marqueVoiture.isValide()&&matricule.isValide()&&contratAssurance.isValide();
        }

        public String getText(){
            String text="_____Informations sur le véhicule assuré\n";
            text+="Matricule : "+matricule+"\n";
            text+="N° Contrat d'Assurance : "+contratAssurance+"\n";
            if(!marqueVoiture.isEmpty())
                text+="Marque de voiture : "+marqueVoiture+"\n";
            text+="\n";

            return text;
        }

        public void chargerView(ContratAssurance contratAssurance){
            this.matricule.setText(contratAssurance.getMatriculeVehiculeAssure());
            this.contratAssurance.setText(String.valueOf(contratAssurance.getNumContrat()));
            this.marqueVoiture.setText(contratAssurance.getMarqueVehicule());
        }
    }

    /* Fragment pour autres details*/
    public static class AutresDetailsFragment extends Form {
        private View rootView;
        private ChampSaisieView nombreTemoins;
        private ChampSaisieView nombreBlesses;
        private ChampSaisieView nombreMorts;

        public AutresDetailsFragment() {
        }

        public static AutresDetailsFragment newInstance(DossierSinistre dossierSinistre) {
            AutresDetailsFragment fragment = new AutresDetailsFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.autres_details_fragment, container, false);
                nombreTemoins = (ChampSaisieView) rootView.findViewById(R.id.nombreTemoins);
                nombreBlesses = (ChampSaisieView) rootView.findViewById(R.id.nombreBlesses);
                nombreMorts = (ChampSaisieView) rootView.findViewById(R.id.nombreMorts);
                if(getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)!=null )
                    chargerView((DossierSinistre)getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER));
            }
            return rootView;
        }

        public boolean isValideForm(){
            nombreTemoins.verifyChamp();
            nombreBlesses.verifyChamp();
            nombreMorts.verifyChamp();
            return  nombreTemoins.isValide() &&nombreBlesses.isValide()&&nombreMorts.isValide();
        }

        public String getText(){
            String text="";
            if(!nombreBlesses.isEmpty() || !nombreMorts.isEmpty() || !nombreTemoins.isEmpty()){
                text+="_____Détails Suplémentaires\n";
                if(!nombreTemoins.isEmpty() )
                    text+="Nombre de temoins : "+nombreTemoins+"\n";
                if(!nombreBlesses.isEmpty() )
                    text+="Nombre de blessées : "+nombreBlesses+"\n";
                if(!nombreMorts.isEmpty() )
                    text+="Nombre de morts : "+nombreMorts+"\n";
                text+="\n";

            }

            return text;

        }

        public void chargerView(DossierSinistre dossierSinistre){
            if(dossierSinistre.getNbTemoins()!=-1)
                this.nombreTemoins.setText(String.valueOf(dossierSinistre.getNbTemoins()));
            if(dossierSinistre.getNbBlesses()!=-1)
                this.nombreBlesses.setText(String.valueOf(dossierSinistre.getNbBlesses()));
            if(dossierSinistre.getNbMorts()!=-1)
                this.nombreMorts.setText(String.valueOf(dossierSinistre.getNbMorts()));
        }
    }

    /* Fragment pour ajout de photo*/

    public static class PrendrePhotoFragment extends Form {

        private View rootView;
        public static final int IMAGE_GALLERY_REQUEST = 20;
        private static final int CAMERA_REQUEST = 1888;
        private ImageView photosVoiture;

        private Uri photoUri;
        private boolean imageAdded=false;
        private Bitmap imageBitmap;

        public PrendrePhotoFragment() {
        }

        public static PrendrePhotoFragment newInstance(DossierSinistre dossierSinistre) {
            PrendrePhotoFragment fragment = new PrendrePhotoFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.prendre_photos_fragment, container, false);

                ImageButton bouton = (ImageButton) rootView.findViewById(R.id.imageButton);
                ImageButton bouton2 = (ImageButton) rootView.findViewById(R.id.imageButton1);
                photosVoiture = (ImageView) rootView.findViewById(R.id.photoVoiture);
                final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
                File newdir = new File(dir);
                newdir.mkdirs();


                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
                    }
                }

                bouton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });


                bouton2.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        String pictureDirectoryPath = pictureDirectory.getPath();
                        Uri data = Uri.parse(pictureDirectoryPath);
                        photoPickerIntent.setDataAndType(data, "image/*");
                        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

                    }
                });
                if(getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)!=null) {
                    Uri uri=Uri.parse(((DossierSinistre) getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)).getPhoto());
                    chargerView(uri);
                }
            }

            return rootView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if ( resultCode == RESULT_OK && data!=null) {
                switch (requestCode) {
                    case CAMERA_REQUEST:
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        imageBitmap=photo;
                        imageAdded=true;

                        photoUri=Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), photo, "photoSinistre_" + Calendar.getInstance().getTime().toString() + ".png", "any thing"));
                        chargerView(photoUri);
                        break;

                    case IMAGE_GALLERY_REQUEST :
                        photoUri=data.getData();
                        chargerView(photoUri);
                        break;

                }

            }
        }

        public Uri getPhotosUri() {
            return photoUri;
        }

        public boolean isValideForm(){
            if(!imageAdded){
                Toast.makeText(getContext(),"Vous devez ajouter une photo",Toast.LENGTH_LONG).show();
                return false;
            }
            else
            return true;
        }

        public void chargerView(Uri photo){
            InputStream inputStream;

            try {
                photoUri=photo;
                inputStream = getContext().getContentResolver().openInputStream(photoUri);
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                photosVoiture.setImageBitmap(imageBitmap);

                imageAdded=true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /* Fragment pour ajout de vidéo*/

    public static class PrendreVideoFragment extends Form {

        private View rootView;
        public static final int VIDEO_GALLERY_REQUEST = 15;
        private static final int VIDEO_CAPTURE = 101;
        private VideoView videoVoiture ;
        private  ImageView imageButtonNouvelleVideo;
        private ImageView imageButtonVideoExistante;
        private Uri videoUri;
        private boolean videoAdded = false;

        public PrendreVideoFragment() {
        }

        public static PrendreVideoFragment newInstance(DossierSinistre dossierSinistre) {
            PrendreVideoFragment fragment = new PrendreVideoFragment();
            Bundle args=new Bundle();
            args.putParcelable(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.prendre_video_fragment, container, false);
                videoVoiture = (VideoView) rootView.findViewById(R.id.videoVoiture);
                //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

                imageButtonNouvelleVideo = (ImageView) rootView.findViewById(R.id.imageButtonNouvelleVideo);
                imageButtonVideoExistante = (ImageView) rootView.findViewById(R.id.imageButtonVideoExistante);
                imageButtonNouvelleVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
                        }
                    }
                });

                imageButtonVideoExistante.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                        String pictureDirectoryPath = pictureDirectory.getPath();
                        Uri data = Uri.parse(pictureDirectoryPath);
                        photoPickerIntent.setDataAndType(data, "video/*");
                        startActivityForResult(photoPickerIntent, VIDEO_GALLERY_REQUEST);

                    }
                });
                if(getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)!=null) {
                    //chargerView(Uri.parse(((DossierSinistre)getArguments().getParcelable(ListeDeclarationsActivity.DOSSIER)).getVideo()));
                }
            }
            return rootView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode ==RESULT_OK) {
                if(requestCode == VIDEO_CAPTURE || requestCode == VIDEO_GALLERY_REQUEST ) {
                    videoUri = data.getData();
                    chargerView(videoUri);
                }
            }
        }

        public boolean isValideForm(){
            if(!videoAdded){
                Toast.makeText(getContext(),"Vous devez ajouter une vidéo",Toast.LENGTH_LONG).show();
                return false;
            }
            else
                return true;
        }

        public Uri getVideoUri(){
            return videoUri;
        }

        public void chargerView(Uri video){
            videoUri=video;
            videoAdded = true;
            MediaController mc = new MediaController(getContext());
            mc.setAnchorView(videoVoiture);
            mc.setMediaPlayer(videoVoiture);
            videoVoiture.setMediaController(mc);
            videoVoiture.setVideoURI(videoUri);
        }
    }


    /* Fragment pour envoie de déclaration*/

    public static class SendDeclarationFragment extends Form {
        private View rootView;
        private RadioGroup radioGroup;
        public SendDeclarationFragment() {
        }

        public static SendDeclarationFragment newInstance() {
            SendDeclarationFragment fragment = new SendDeclarationFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(rootView==null) {
                rootView = inflater.inflate(R.layout.send_declaration_fragment, container, false);
                radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
            }
            return rootView;
        }

        public boolean isValideForm(){
            return true;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ProprietaireVoitureFragment proprietaireVoitureFragment;
        private ConducteurVoitureFragment conducteurVoitureFragment;
        private DetailSinistreFragment detailSinistreFragment;
        private AutresDetailsFragment autresDetailsFragment;
        private VoitureAssuranceFragment voitureAssuranceFragment;
        private PrendrePhotoFragment prendrePhotoFragment;
        private PrendreVideoFragment prendreVideoFragment;
        private SendDeclarationFragment sendDeclarationFragment;
        private AdversaireFragment adversaireFragment;
        private String typeSinistre;

        private LinkedList<Form> listForms=new LinkedList<Form>();
        public SectionsPagerAdapter(FragmentManager fm,String typeSinistre) {

            super(fm);
            this.typeSinistre=typeSinistre;
            proprietaireVoitureFragment=ProprietaireVoitureFragment.newInstance(assure);
            conducteurVoitureFragment=ConducteurVoitureFragment.newInstance(dossierSinistre);
            detailSinistreFragment=DetailSinistreFragment.newInstance(dossierSinistre);
            autresDetailsFragment=AutresDetailsFragment.newInstance(dossierSinistre);
            voitureAssuranceFragment=VoitureAssuranceFragment.newInstance(assure);
            prendrePhotoFragment=PrendrePhotoFragment.newInstance(dossierSinistre);
            prendreVideoFragment=PrendreVideoFragment.newInstance(dossierSinistre);
            sendDeclarationFragment=SendDeclarationFragment.newInstance();

            listForms.add(proprietaireVoitureFragment);
            listForms.add(conducteurVoitureFragment);
            listForms.add(detailSinistreFragment);
            listForms.add(voitureAssuranceFragment);
            if(this.typeSinistre.equals(ListeDeclarationsActivity.TYPE_AVEC_TIERS)) {
                adversaireFragment=AdversaireFragment.newInstance(dossierSinistre);
                listForms.add(adversaireFragment);
            }
            listForms.add(autresDetailsFragment);
            listForms.add(prendrePhotoFragment);
            listForms.add(prendreVideoFragment);
            listForms.add(sendDeclarationFragment);
        }

        @Override
        public Form getItem(int position) {

            return listForms.get(position);
        }

        @Override
        public int getCount() {
            return listForms.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

              return "SECTION"+position;

        }

        public void sendDeclaration(Context context){
            String filename = "gmail.png";
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "minou.mano94@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Déclaration Sinistre");
            emailIntent.putExtra(Intent.EXTRA_TEXT,remplirBody());
            emailIntent.putExtra(Intent.EXTRA_STREAM, prendrePhotoFragment.getPhotosUri());
            emailIntent.putExtra(Intent.EXTRA_STREAM, prendreVideoFragment.getVideoUri());
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

        }

        public boolean declarationSent(){
            return false;
        }

        public String remplirBody() {
            String message = "";
            message=proprietaireVoitureFragment.getText()+conducteurVoitureFragment.getText()+
                    detailSinistreFragment.getText()+voitureAssuranceFragment.getText();
            if(adversaireFragment!=null)
                message+=adversaireFragment.getText();
            message+=autresDetailsFragment.getText()+"\n";

            return message;
        }

        public long mettreAJourDossierSinistre(){
            long idDossier;
            String nomConducteur=null;
            String prenomConducteur=null ;
            String nomAdversaire=null;
            String prenomAdversaire=null;


            String dateSinistre=null;
            String heureSinistre=null;
            String endroitSinistre=null;
            String description=null;

            int nbTemoins=-1;
            int nbBlesses = -1;
            int nbMorts=-1;
            String photo;
            String video;
            String assureurAdversaire=null;
            int assuranceAdversaire=-1;
            String matriculeAdversaire=null;

            DataSourceDossierOuvert dataSourceDossierOuvert =new DataSourceDossierOuvert(FormActivity.this);
            if(conducteurVoitureFragment.isConducteur.isChecked()){
                nomConducteur=conducteurVoitureFragment.nameConducteur.toString();
                prenomConducteur=conducteurVoitureFragment.prenomConducteur.toString();
            }

            endroitSinistre=detailSinistreFragment.endroitSinistre.toString();
            if(detailSinistreFragment.textDate.getText()!=null)
                dateSinistre=detailSinistreFragment.textDate.getText().toString();
            if(detailSinistreFragment.textTime.getText()!=null)
                heureSinistre=detailSinistreFragment.textTime.getText().toString();
            if(detailSinistreFragment.textDescription.getText()!=null)
                description=detailSinistreFragment.textDescription.getText().toString();

            if(!autresDetailsFragment.nombreTemoins.isEmpty())
                nbTemoins=Integer.valueOf(autresDetailsFragment.nombreTemoins.toString());

            if(!autresDetailsFragment.nombreBlesses.isEmpty())
                nbBlesses=Integer.valueOf(autresDetailsFragment.nombreBlesses.toString());

            if(!autresDetailsFragment.nombreMorts.isEmpty())
                nbMorts=Integer.valueOf(autresDetailsFragment.nombreMorts.toString());

            photo=prendrePhotoFragment.getPhotosUri().toString();
            video=prendreVideoFragment.getVideoUri().toString();

            if(adversaireFragment!=null) {
                nomAdversaire = adversaireFragment.nameAdversaire.toString();
                prenomAdversaire = adversaireFragment.prenomAdversaire.toString();
                assureurAdversaire = adversaireFragment.assureurSpinner.getSelectedItem().toString();
                if(!adversaireFragment.contratAssurance.isEmpty())
                    assuranceAdversaire = Integer.valueOf(adversaireFragment.contratAssurance.toString());
                if(!adversaireFragment.matricule.isEmpty())
                    matriculeAdversaire = adversaireFragment.matricule.toString();
            }

            long idContratAdversaire=-1;
            long  idAdversaire=-1;
            long  idConducteur=-1;

             dataSourceDossierOuvert.open();
            if(conducteurVoitureFragment.isConducteur.isChecked() && (dossierSinistre==null || dossierSinistre.getConducteur()==null))
                idConducteur = dataSourceDossierOuvert.insererPersonne(nomConducteur, prenomConducteur, -1);

            if(typeSinistre.equals(ListeDeclarationsActivity.TYPE_AVEC_TIERS) && (dossierSinistre==null || dossierSinistre.getAdversaire()==null)) {
                idContratAdversaire = dataSourceDossierOuvert.insererAssuranceAdversaire(assuranceAdversaire, matriculeAdversaire, assureurAdversaire);
                idAdversaire = dataSourceDossierOuvert.insererPersonne(nomAdversaire, prenomAdversaire, idContratAdversaire);
            }

            if(dossierSinistre!=null){
                dossierSinistre.setTypeSinistre(typeSinistre);
                if(conducteurVoitureFragment.isConducteur.isChecked()) {
                    if(dossierSinistre.getConducteur()!= null) {
                        dossierSinistre.getConducteur().setNom(nomConducteur);
                        dossierSinistre.getConducteur().setPrenom(prenomConducteur);
                    }
                    else{
                        dossierSinistre.setConducteur(new Personne(idConducteur,nomConducteur,prenomConducteur,null));
                    }
                }
                if(typeSinistre.equals(ListeDeclarationsActivity.TYPE_AVEC_TIERS)){
                    if(dossierSinistre.getAdversaire()!= null) {
                        dossierSinistre.getAdversaire().setNom(nomAdversaire);
                        dossierSinistre.getAdversaire().setPrenom(prenomAdversaire);
                        dossierSinistre.getAdversaire().getContratAssurance().setMatriculeVehiculeAssure(matriculeAdversaire);
                        dossierSinistre.getAdversaire().getContratAssurance().setNumContrat(assuranceAdversaire);
                        dossierSinistre.getAdversaire().getContratAssurance().setCompagnieAssurance(assureurAdversaire);
                    }
                    else{
                        dossierSinistre.setAdversaire(new Personne(idAdversaire,nomAdversaire,prenomAdversaire,new ContratAssurance(idContratAdversaire,assuranceAdversaire,matriculeAdversaire,assureurAdversaire,null)));
                    }
                }
                dossierSinistre.setDateSinistre(dateSinistre);
                dossierSinistre.setHeureSinistre(heureSinistre);
                dossierSinistre.setEndroitSinistre(endroitSinistre);

                dossierSinistre.setNbBlesses(nbBlesses);
                dossierSinistre.setNbMorts(nbMorts);
                dossierSinistre.setNbTemoins(nbTemoins);
                dossierSinistre.setDescription(description);
                dossierSinistre.setPhoto(photo);
                dossierSinistre.setVideo(video);
                //modifier le dossier dans sqlite;
                idDossier=dossierSinistre.getId();
                dataSourceDossierOuvert.openToUpdate();
                dataSourceDossierOuvert.updateDossierSinistre(dossierSinistre);
                Toast.makeText(FormActivity.this,"Modifiation du dossier termine",Toast.LENGTH_LONG).show();
                dataSourceDossierOuvert.close();
            }
             else {
                Toast.makeText(FormActivity.this,"Dossier enregistré",Toast.LENGTH_LONG).show();
                idDossier= dataSourceDossierOuvert.insererDossierSinistre(typeSinistre, assure.getId(), idConducteur, idAdversaire, dateSinistre, heureSinistre, endroitSinistre, description, nbTemoins, nbBlesses, nbMorts, photo, video);
            }
            dataSourceDossierOuvert.close();
            return idDossier;

        }

        public boolean isAcceptingSend(){
            int selectedRadioButton=sendDeclarationFragment.radioGroup.indexOfChild(findViewById(sendDeclarationFragment.radioGroup.getCheckedRadioButtonId()));
            return  selectedRadioButton==0;
        }

        public void sendDeclarationToFirebase(){
            boolean send=false;
            if(dossierSinistre!=null){
                //envoyer le dossier
                //si connexion ouvertes send=true et informer l'assuré sinon informer l'assuré que l'envoi se fait quand il sera connecté
            }
            else{
                long idDossier=mettreAJourDossierSinistre();
                DataSourceDossierOuvert dataSourceDossierOuvert =new DataSourceDossierOuvert(FormActivity.this);
                dataSourceDossierOuvert.open();
                dossierSinistre=dataSourceDossierOuvert.getDossierById(idDossier,assure);
                dataSourceDossierOuvert.close();
            }

            if(true/*tester la connectivité*/) {

                dossierSinistre.setAssure(null);
                dossierSinistre.setEtat("envoyé");
                databaseRef = FirebaseDatabase.getInstance().getReference();
                mUser=FirebaseAuth.getInstance().getCurrentUser();

                DatabaseReference newRef = databaseRef.child("Assure").child(mUser.getUid()).child("dossiers").push();
                newRef.setValue(dossierSinistre);
                newRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        DataSourceDossierOuvert dataSourceDossierOuvert =new DataSourceDossierOuvert(FormActivity.this);
                        dataSourceDossierOuvert.open();
                        dataSourceDossierOuvert.deleteDossierById(dossierSinistre.getId());
                        dataSourceDossierOuvert.close();


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //Stockage de photo
                StorageReference storageReferencePhoto=FirebaseStorage.getInstance().getReference().child(mUser.getUid()).child("photos");
                Uri photoFile = prendrePhotoFragment.getPhotosUri();

                StorageReference riversRefPhoto = storageReferencePhoto.child(photoFile.getLastPathSegment());
                UploadTask uploadTaskPhoto = riversRefPhoto.putFile(photoFile);

                uploadTaskPhoto.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

                //stockage de video
                StorageReference storageReferenceVideo=FirebaseStorage.getInstance().getReference().child(mUser.getUid()).child("videos");
                Uri videoFile = prendreVideoFragment.getVideoUri();

                StorageReference riversRefVideo = storageReferenceVideo.child(videoFile.getLastPathSegment());
                UploadTask uploadTaskStoreVideo = riversRefVideo.putFile(videoFile);

                uploadTaskStoreVideo.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });



            }else{
                Toast.makeText(FormActivity.this,"dossier seuvegardé dans brouillon",Toast.LENGTH_LONG).show();
                //proposer à l'utilisateur d'envoyer à la première connexion ou d'annuler
                //s'il accepte insérer le dossier dans le cache de firebase
            }
        }
    }

}
