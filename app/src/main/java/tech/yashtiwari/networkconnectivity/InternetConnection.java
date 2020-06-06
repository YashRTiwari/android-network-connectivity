package tech.yashtiwari.networkconnectivity;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class InternetConnection extends ConnectivityManager.NetworkCallback implements LifecycleObserver {

    ConnectivityManager cm;
    private  NetworkRequest networkRequest;
    private static final String TAG = "CheckInternetConnection";
    private InternetListener listener;
    private boolean isAvailable = false;

    /**
     * Listener that is going to be used in your activity/fragment
     */
    public interface InternetListener {

        // Called once on ON_START and when network changes from not connected to connected
        public void onInternetConnectionAvailable();
        // Called when network is not connected
        public void onInternetConnectLost();
    }

    /**
     *
     * @param cm - Connectivity Manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)
     * @param list - this, this needs to implemented in you activity/fragment
     */
    private void checkParameter( ConnectivityManager cm, InternetListener list){
        if (cm == null) throw new NullPointerException("ConnectivityManager Cannot be null");
        if (list == null) throw new NullPointerException("InternetCallbackListener cannot be null");
    }


    /**
     *
     * @param cm - Connectivity Manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)
     * @param list - this, this needs to implemented in you activity/fragment
     */
    public InternetConnection(ConnectivityManager cm, InternetListener list) {
        checkParameter( cm, list);
        this.cm = cm;
        this.listener = list;
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }


    /**
     *  This function will only be called when the activity is created.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onPreCheck() {

        boolean result = false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (networkCapabilities == null) result = false;
            else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.d(TAG, "onPrecheck: WIFI");
                result = true;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.d(TAG, "onPrecheck: Cellular Data");
                result = true;
            }
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null) result = false;
            else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "onPrecheck: WIFI");
                result = true;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "onPrecheck: Cellular Data");
                result = true;
            }
        }
        listen(result);
    }

    /**
     *  Function that needs to be called whenever network state is changed
     * @param isAvailable = true/false
     */
    private void listen(boolean isAvailable){
        if (!isAvailable && this.isAvailable){ // is not connected, was connected
            listener.onInternetConnectLost();
        } else if (isAvailable  && this.isAvailable) { // is connected and was connected
            //Do nothing, network is in connected state
        }
        else if (isAvailable && !this.isAvailable) {  // is connected and was not connected
            listener.onInternetConnectionAvailable();
        }
        else {  // was not connected and is not connected
            listener.onInternetConnectLost();
        }
        this.isAvailable = isAvailable;
    }

    /**
     *  Attaches the callback, only when ON_RESUME is called.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void createAndRegisterNetworkRequest() {
        if (cm == null || networkRequest == null) return;
        cm.registerNetworkCallback(networkRequest, this);
    }

    /**
     *  Removes the callback, only when ON_PAUSE is called for better battery management.
     * @throws Exception when the callback was not attached.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void unregisterNetworkCallback() {
        try {
            cm.unregisterNetworkCallback(this);
        } catch (Exception e) { // Do not change.
            Log.d(TAG, "unregisterNetworkCallback: Callback was not attached");
        }
    }


    /**
     *  This is called whenever we have internet connection.
     * @param network - type of network connected.
     */
    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.d(TAG, "onAvailable: " + network.toString()); // Provides the id of the network.
        listen(true);
//        if(!isAvailable) listener.onInternetConnectionAvailable();
//        isAvailable = true;
    }

    /**
     *  When network is making a transition to other/no network
     * @param network
     * @param maxMsToLive
     */
    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
        Log.d(TAG, "onLosing: ");
    }

    /**
     * Called whenever we lost connection via particular network - WIFI/CELLULAR
     * @param network
     */
    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.d(TAG, "onLost: " + network.toString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAvailable) listener.onInternetConnectLost();
                isAvailable = false;
            }
        }, 2000); // Added to handle WIFI -> Data transition and vice-versa

    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
    }
}
