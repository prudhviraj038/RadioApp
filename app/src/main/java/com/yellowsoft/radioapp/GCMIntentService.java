package com.yellowsoft.radioapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.yellowsoft.radioapp.CommonUtilities.SENDER_ID;
import static com.yellowsoft.radioapp.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", "registered");
        ServerUtilities.register(context, "tourism", "tourism", registrationId);
        Settings.set_gcmid(context,registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
        Settings.set_gcmid(context, "-1");
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String title = intent.getExtras().getString("title");
        String message = intent.getExtras().getString("message");
        String image_url = intent.getExtras().getString("image");
        String product_id = intent.getExtras().getString("product_id");
        String category_id = intent.getExtras().getString("category_id");
        displayMessage(context, message);
        generateCustomNotification(context,title, message,image_url,product_id,category_id);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
       // generateCustomNotification(context, message,image);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.app_icon;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, SplashScreenActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
       // notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }


    private static void generateCustomNotification(Context context,String title,String message,String image_url,String product_id,String category_id){

        //Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lock);

        //String imageurl = "http://mamacgroup.com//qatar//uploads//products//DayepIMG_20150512_213312516.jpg";
        Bitmap notify_bitmap = getBitmapFromURL(image_url);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setAutoCancel(true)
       . setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.app_icon).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                .setContentText(message);

        NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle();
        bigPicStyle.bigPicture(notify_bitmap);
        bigPicStyle.setBigContentTitle(title);
        bigPicStyle.setSummaryText(message);
        mBuilder.setStyle(bigPicStyle);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent;
        if(!category_id.equals("0")) {
            Log.e("sdsfsf", category_id);
            resultIntent = new Intent(context, SplashScreenActivity.class);
            resultIntent.putExtra("parent_id", category_id);
            resultIntent.putExtra("page_name", "category_name");
        }
        else if(!product_id.equals("0")){
            resultIntent = new Intent(context, SplashScreenActivity.class);
            resultIntent.putExtra("add_id", product_id);
        }
        else{
            resultIntent = new Intent(context, SplashScreen.class);
        }
        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack( SplashScreenActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}