package dz.esi.declaresinistreapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by asus on 20/05/2017.
 */

public class ChampSaisieView extends FrameLayout {

    private String defaultText;
    private String dataType;
    private boolean isRequired;
    private boolean isEnabled;
    private int idEditText;
    private int idImageView;
    private EditText champSaisie;
    private ImageView imageView;

    private void handleAttributes(Context context, AttributeSet attrs) {
        try {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ChampSaisieView);

            dataType=styledAttrs.getString(R.styleable.ChampSaisieView_dataType);
            defaultText = styledAttrs.getString(R.styleable.ChampSaisieView_defaultText);
            isRequired = styledAttrs.getBoolean(R.styleable.ChampSaisieView_isRequired,true);
            isEnabled= styledAttrs.getBoolean(R.styleable.ChampSaisieView_isEnabled,true);
            idEditText = styledAttrs.getInt(R.styleable.ChampSaisieView_idEditText,R.id.champSaisie);
            idImageView = styledAttrs.getInt(R.styleable.ChampSaisieView_idImageView,R.id.imageView);
            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChampSaisieView(Context context) {
        super(context);
    }

    public ChampSaisieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context, attrs);
    }

    public ChampSaisieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttributes(context, attrs);
    }

    @Override
    protected void onFinishInflate() {

        View champSaisieView = LayoutInflater.from(getContext())
                .inflate(R.layout.champs_saisie_item, this, false);
        addView(champSaisieView);
        champSaisie = (EditText) findViewById(R.id.champSaisie);
        imageView=(ImageView)findViewById(R.id.imageView) ;

        champSaisie.setId(idEditText);
        imageView.setId(idImageView);
        //Toast.makeText(getContext(),"i find this : "+champSaisie.getId(),Toast.LENGTH_LONG).show();

        champSaisie.setHint(defaultText);
        champSaisie.setEnabled(isEnabled);

        switch (dataType){
            case "int" :
                champSaisie.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "adresse":
                champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case "password":
                champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }


        champSaisie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isEmpty()){
                    setNothing();
                } else {
                    if((dataType.equals("String") && !isValideString())||(dataType.equals("int") && !isValideInteger())){
                        setFalse();
                    }
                    else {
                        setCorrect();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setFalse(){
        imageView.setImageResource(R.drawable.cross);
        imageView.setVisibility(View.VISIBLE);
        champSaisie.getBackground().setColorFilter(Color.parseColor("#FF6A1A"), PorterDuff.Mode.SRC_ATOP);

    }

    public void setCorrect(){
        imageView.setImageResource(R.drawable.correct);
        imageView.setVisibility(View.VISIBLE);
        champSaisie.getBackground().setColorFilter(Color.parseColor("#23E5F2"), PorterDuff.Mode.SRC_ATOP);

    }

    public void setNothing(){

        imageView.setVisibility(View.INVISIBLE);
        champSaisie.getBackground().setColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.SRC_ATOP);
    }

    public boolean isValideString(){
        char[] caracteres=champSaisie.getText().toString().toCharArray();
        int i=0;
        boolean valide=true;
        while(i<champSaisie.getText().toString().length() && valide){
            if(!Character.isLetter(caracteres[i]) && caracteres[i]!=' '){
                valide=false;
            }
            i++;
        }
        return valide;
    }

    public boolean isValideInteger(){
        if(!champSaisie.getText().toString().isEmpty()) {
            try{
                Integer.valueOf(champSaisie.getText().toString());
                return  true;

            }catch (Exception e){
                return false;
            }
        }
        else
            return true;
    }


    public void verifyChamp(){
        if(this.isEmpty() && this.isRequired){
            this.setFalse();
        }
        else{
            if((dataType.equals("String") && !isValideString())||(dataType.equals("int") && !isValideInteger())){
                this.setFalse();
            }
        }
    }

    public boolean isValide(){
        boolean valide=true;
        if(this.isEmpty()&& this.isRequired){
            this.setFalse();
            Toast.makeText(getContext(),"Veillez remplir les champs obligatoires",Toast.LENGTH_LONG).show();
            valide=false;
        }
        else{
            if((dataType.equals("String") && !isValideString())||(dataType.equals("int") && !isValideInteger())){
                Toast.makeText(getContext(),"Veillez vérifier les champs saisis",Toast.LENGTH_LONG).show();
                this.setFalse();
                valide=false;
            }
        }
        return valide;
    }


    public void setEnabled(boolean enabled){
        this.isEnabled=enabled;
        this.champSaisie.setEnabled(enabled);
    }

    @Override
    public String toString() {
        if(champSaisie.getText()!=null)
        return champSaisie.getText().toString();
        else return "";
    }

    public boolean isEmpty(){
        return (this.champSaisie.getText()==null || this.champSaisie.getText().toString().equals(""));
    }

    public EditText getChampSaisie() {
        return champSaisie;
    }


    public static class ViewIdGenerator {
        private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        @SuppressLint("NewApi")
        public static int generateViewId() {

            if (Build.VERSION.SDK_INT < 17) {
                for (;;) {
                    final int result = sNextGeneratedId.get();
                    // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                    int newValue = result + 1;
                    if (newValue > 0x00FFFFFF)
                        newValue = 1; // Roll over to 1, not 0.
                    if (sNextGeneratedId.compareAndSet(result, newValue)) {
                        return result;
                    }
                }
            } else {
                return View.generateViewId();
            }

        }
    }

    public void setText(String text){
        if(text!=null)
            this.champSaisie.setText(text);
    }
}

