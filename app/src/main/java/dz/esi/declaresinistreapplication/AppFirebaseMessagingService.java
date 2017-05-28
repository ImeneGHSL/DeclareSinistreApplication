package dz.esi.declaresinistreapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by DELL on 25/05/2017.
 */

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";
    Map<String, String> payload ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();

        }




        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if( remoteMessage.getNotification().getSound().equals(SignInActivity.mAuth.getCurrentUser().getUid().toString())){
            sendNotification(notificationTitle, notificationBody);
            Log.d(TAG, "Notification : " + remoteMessage.getNotification().getSound() );
        }
        else  Log.d(TAG, "Pas de Notification : " + remoteMessage.getNotification().getSound() );

    }
    private void sendNotification(String notificationTitle, String notificationBody) {

        Intent intent = new Intent(this, ListeDeclarationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String Experience = notificationBody ;

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Experience))
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.notification) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                //.setContentText(notificationBody)
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

   /* private static final String TAG = "Notification" ;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //if(remoteMessage.getData().size() > 0 ){
            Map<String, String> payload = remoteMessage.getData() ;

            sendNotification(payload);
        //}

        /*super.onMessageReceived(remoteMessage);
        Log.d(TAG , "From : "+ remoteMessage.getFrom()) ;

        if(remoteMessage.getData().size() > 0 ){
            Log.d(TAG , "Message data : " + remoteMessage.getData()) ;
        }

        if(remoteMessage.getNotification() != null ){
            Log.d(TAG ,"Message body : " + remoteMessage.getNotification().getBody() ) ;
            sendNotification(remoteMessage.getNotification().getBody()) ;
        }*/


   /* }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(Map<String,String> payload)  {

        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(payload.get("username"))
                .setContentText(payload.get("email"))
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());*/
       /* Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) ;

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this) ;

        Intent resultIntent = new Intent(this, MainActivity.class) ;

        stackBuilder.addNextIntent(resultIntent) ;
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_ONE_SHOT) ;

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.accident)
                .setContentTitle(payload.get("username"))
                .setContentText(payload.get("email"))
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(resultPendingIntent) ;




        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
        notificationManager.notify(0 , builder.build());

    }
}*/
