package testapp.com.boundservicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static testapp.com.boundservicedemo.BoundService.MESSAGE_DATA_INPUT;
import static testapp.com.boundservicedemo.BoundService.RAW_DATA;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_NAME = "MainActivity";

    private EditText dataBox;
    private Button submitButton;
    private ListView outputListView;

    private ArrayList<String> log = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Boolean isBound = false;
    private Messenger service = null;

    /**
     * Handles the connection to the service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = new Messenger(iBinder);
            isBound = true;
            Toast.makeText(getBaseContext(), "Connected to Service..", Toast.LENGTH_SHORT).show();
        }

        //NOTE: This function is can seem unreliable as android will tend to try keep the service
        //      open if resources allow it to, even if the app seems closed.
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
            service = null;
        }
    };

    public static final int    MESSAGE_DATA_OUTPUT = 0;
    public static final String OUTPUT_DATA         = "data";

    /**
     * Handles the replies from the service.
     */
    private final Messenger replyHandler = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getBaseContext(), "Received Response!", Toast.LENGTH_SHORT);
            switch (msg.what) {
                case MESSAGE_DATA_OUTPUT:
                    if (msg.getData().containsKey(OUTPUT_DATA))
                        addToLog(msg.getData().getString(OUTPUT_DATA));
                    break;

                default: // Default Case
                    super.handleMessage(msg);
            }
        }
    });

    /**
     * Overridden onCreate method. Initialises the UI and binds to the service.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureAndInitialiseUI();
        Log.d(LOG_NAME, isBound.toString());
        if (!isBound) {
            Intent serviceIntent = new Intent(getApplicationContext(), BoundService.class);
            this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Overridden onDestroy method. Unbinds from the service, thus closing it.
     */
    @Override
    protected void onDestroy() {
        if (isBound) this.unbindService(serviceConnection);
        super.onDestroy();
    }

    /**
     * Captures all XML UI elements and initialises them.
     */
    private void captureAndInitialiseUI() {
        dataBox        = (EditText) findViewById(R.id.editBox);
        submitButton   = (Button)   findViewById(R.id.buttonSubmit);
        outputListView = (ListView) findViewById(R.id.outputListView);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Get the data from the input and send to the service.
            sendDataToService(dataBox.getText().toString());
            // Clear the text boxes after code completed.
            dataBox.setText("");
            }
        });

        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, log);
        outputListView.setAdapter(adapter);
    }

    private void sendDataToService(String data) {
        // Make a new message and set the reply handler and what fields.
        Message msg = new Message();
        msg.replyTo = replyHandler;
        msg.what = MESSAGE_DATA_INPUT;

        // Add our data to the message.
        Bundle bundle = new Bundle();
        bundle.putString(RAW_DATA, data);
        msg.setData(bundle);

        // Finally send the message.
        try {
            service.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the log on the screen.
     * @param logMessage The log message to add to the logging screen.
     */
    private void addToLog(String logMessage) {
        log.add(logMessage);
        adapter.notifyDataSetChanged();
        outputListView.setSelection(adapter.getCount() - 1);
    }
}
