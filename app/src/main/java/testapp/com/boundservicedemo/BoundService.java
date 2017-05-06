package testapp.com.boundservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static testapp.com.boundservicedemo.MainActivity.OUTPUT_DATA;

public class BoundService extends Service {

    public static final int    MESSAGE_DATA_INPUT = 0;
    public static final String RAW_DATA           = "raw";

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DATA_INPUT:
                    if (msg.getData().containsKey(RAW_DATA)) {
                        String data = msg.getData().getString(RAW_DATA);
                        data = "Upper Case: " + data.toUpperCase() +
                               "\nLower Case: " + data.toLowerCase();
                        replyToService(data, msg.replyTo);
                    }
                default: // Default Case
                    super.handleMessage(msg);
            }
        }
    }

    private void replyToService(String output, Messenger replyTo) {
        // Make a new message and set the reply handler and what fields.
        Message msg = new Message();
        msg.what = MESSAGE_DATA_INPUT;

        // Add our data to the message.
        Bundle bundle = new Bundle();
        bundle.putString(OUTPUT_DATA, output);
        msg.setData(bundle);

        // Finally send the message.
        try {
            replyTo.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
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
