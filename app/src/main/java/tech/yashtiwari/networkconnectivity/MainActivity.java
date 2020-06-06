package tech.yashtiwari.networkconnectivity;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;


public class MainActivity extends AppCompatActivity implements InternetConnection.InternetListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().addObserver(new InternetConnection((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE), this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onInternetConnectionAvailable() {
        Toast.makeText(this, "Internet is available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInternetConnectLost() {
        Toast.makeText(this, "Internet is not available", Toast.LENGTH_SHORT).show();
    }
}
