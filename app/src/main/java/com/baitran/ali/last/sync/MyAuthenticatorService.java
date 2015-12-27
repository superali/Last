package com.baitran.ali.last.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ali on 24/12/15.
 */
public class MyAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private MyAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MyAuthenticator(this);

        super.onCreate();
    }


    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
       return mAuthenticator.getIBinder();
    }
}


