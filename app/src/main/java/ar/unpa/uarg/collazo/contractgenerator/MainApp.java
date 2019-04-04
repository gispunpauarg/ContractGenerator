package ar.unpa.uarg.collazo.contractgenerator;

import android.app.Application;

import com.appizona.yehiahd.fastsave.FastSave;

public class MainApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FastSave.init(getApplicationContext());
    }
}
