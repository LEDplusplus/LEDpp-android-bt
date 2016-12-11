package de.lrapp.ledpp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 42;

    Spinner btDevsSpinner;
    BtComm btComm;
    ArrayAdapter<String> btDevsSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btDevsSpinner = (Spinner) findViewById(R.id.bt_devs_spinner);
        btDevsSpinner.setOnItemSelectedListener(this);
        btComm = new BtComm();

        permissionCheck();

    }

    /**
     * checks for the bluetooth permission
     */
    private void permissionCheck() {
        int btPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH);

        if (btPermissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH);
        } else {
            btComm.init();
            initUI();
        }
    }


    /**
     * handles the permission request response
     * @param requestCode the code to identify the request
     * @param permissions the requested permissions
     * @param grantResults the results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    btComm.init();
                    initUI();

                } else {

                    // permission denied - call permission check again
                    permissionCheck();
                }
            }
        }
    }

    /**
     * initializes the UI, fills the bt devices spinner
     */
    private void initUI() {
        // fill spinner adapter
        String[] bondedDevs = btComm.getBondedDevs();
        btDevsSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bondedDevs);
        btDevsSpinner.setAdapter(btDevsSpinnerAdapter);
    }


    /**
     * Item Selection listener callback method for btDevsSpinner
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param pos The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // connect to selected device
        if (btComm.connect(pos)) {
            makeToast("Connected to: " + parent.getItemAtPosition(pos).toString());
        } else {
            makeToast("Connection failed!");
        }
    }

    /**
     * callback method for btDevsSpinner
     * @param parent The AdapterView that now contains no selected item.
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    /**
     * Creates a Toast message
     * @param text Toast content text
     */
    private void makeToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
