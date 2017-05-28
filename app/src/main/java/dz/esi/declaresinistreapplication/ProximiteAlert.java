package dz.esi.declaresinistreapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by asus on 27/05/2017.
 */

public class ProximiteAlert extends BroadcastReceiver{

    private String destinationLongitude="0";
    private String destinationLatitude="0";
    private String expert="";
    private DossierSinistre dossierSinistre;
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(final Context context, Intent intent) {

        boolean proximity=intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,false);

        if(proximity) {
            Toast.makeText(context,"Recive",Toast.LENGTH_LONG).show();
            destinationLongitude = intent.getExtras().getString(ListeDeclarationsActivity.LONGITUDE);
            destinationLatitude = intent.getExtras().getString(ListeDeclarationsActivity.LATITUDE);
            expert=intent.getExtras().getString(ListeDeclarationsActivity.EXPERT);
            dossierSinistre=intent.getExtras().getParcelable(ListeDeclarationsActivity.DOSSIER);
            createNotification(context);
        }

    }
    private void createNotification(Context context) {

        Intent intent = new Intent(context, ProximityNotificationActivity.class);

        intent.putExtra(ListeDeclarationsActivity.LONGITUDE, destinationLongitude);
        intent.putExtra(ListeDeclarationsActivity.LATITUDE,destinationLatitude);
        intent.putExtra(ListeDeclarationsActivity.EXPERT,expert);
        intent.putExtra(ListeDeclarationsActivity.DOSSIER,dossierSinistre);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.logo)
                .setTicker("vous etes proche d'un expert qui possède traite votre dossier sinistre")
                .setContentTitle("")
                .setContentText("clicker pour plus de detail")
                 .addAction(R.drawable.ok, "afficher", pIntent)
                 .setContentIntent(pIntent)
                .setAutoCancel(true);

        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());


    }
}
