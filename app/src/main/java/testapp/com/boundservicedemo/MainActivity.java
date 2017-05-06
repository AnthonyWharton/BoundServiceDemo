package testapp.com.boundservicedemo;

import android.icu.util.Output;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText dataBox;
    private Button submitButton;
    private ListView outputListView;

    private ArrayList<String> log = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureAndInitialiseUI();
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
            // Get the data from the input and add to log.
            String data = dataBox.getText().toString();
            addToLog("Upper Case: " + data.toUpperCase() + "\nLower Case: " + data.toLowerCase());
            // Clear the text boxes after code completed.
            dataBox.setText("");
            }
        });

        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, log);
        outputListView.setAdapter(adapter);
    }

    /**
     * Updates the log on the screen.
     * @param logMessage The log message to add to the logging screen.
     */
    public void addToLog(String logMessage) {
        log.add(logMessage);
        adapter.notifyDataSetChanged();
        outputListView.setSelection(adapter.getCount() - 1);
    }
}
