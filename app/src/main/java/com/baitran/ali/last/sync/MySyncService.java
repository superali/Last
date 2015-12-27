package com.baitran.ali.last.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by ali on 25/12/15.
 */
public class MySyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static MySyncAdapter sSyncAdapter = null;
    @Override
    public void onCreate() {
        Log.d("my sync service","oncreate") ;
    synchronized (sSyncAdapterLock){
    if (sSyncAdapter == null){
    sSyncAdapter = new MySyncAdapter(getApplicationContext(),true);
    }
    }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
