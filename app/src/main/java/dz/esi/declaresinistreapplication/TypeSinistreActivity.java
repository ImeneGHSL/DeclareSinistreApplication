package dz.esi.declaresinistreapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TypeSinistreActivity extends AppCompatActivity {

    ImageButton avecTiersButton;
    ImageButton sansTiersButton;
    ImageButton stationnaireButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_sinistre);

        avecTiersButton = (ImageButton) findViewById(R.id.avecTiersButton) ;
        sansTiersButton = (ImageButton) findViewById(R.id.sansTiersButton) ;
        stationnaireButton = (ImageButton) findViewById(R.id.stationnaireButton) ;

        avecTiersButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startFormIntent();

            }
        });

        sansTiersButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startFormIntent();
            }
        });

        stationnaireButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startFormIntent();
            }
        });
    }

    public void startFormIntent(){
        Intent intent = new Intent(TypeSinistreActivity.this,FormActivity.class) ;
        startActivity(intent);
    }
}
