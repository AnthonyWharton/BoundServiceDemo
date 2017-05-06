package testapp.com.boundservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class BoundService extends Service {

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default: // Default Case
                    super.handleMessage(msg);
            }
        }
    }

    private static final String LOG_NAME = "BoundService";
    private final Messenger replyTo = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_NAME, "Binding...");

        return replyTo.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_NAME, "Rebinding...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_NAME, "Unbinding...");
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_NAME, "Destroyed.");
    }

}
