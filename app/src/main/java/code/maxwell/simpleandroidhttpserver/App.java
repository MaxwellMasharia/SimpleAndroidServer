package code.maxwell.simpleandroidhttpserver;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {
    public static final String NOTIFICATION_CHANNEL_ID = "NotificationChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "ChannelOne", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This is the channel through which the app sends notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
