package tech.yashtiwari.networkconnectivity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements InternetConnection.InternetListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // One Line of code
        getLifecycle().addObserver(new InternetConnection((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE), this));
    }

    @Override
    public void onInternetConnectionAvailable() {
        Log.d(TAG, "onInternetConnectionAvailable: ");
    }

    @Override
    public void onInternetConnectLost() {
        Log.d(TAG, "onInternetConnectLost: ");
    }
}
