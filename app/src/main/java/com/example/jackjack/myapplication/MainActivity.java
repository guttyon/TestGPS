package com.example.jackjack.myapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private TextView text;
    private boolean is_tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView)findViewById(R.id.text1);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
       // text.setText("saru");
        is_tracking = false;


    }
    // 回転させたときなど一旦破棄されるとき。
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        TextView textView1 = (TextView)findViewById(R.id.text1);
        String value = textView1.getText().toString();
        outState.putString("TEXT_VIEW_STR", value);
        outState.putBoolean("IS_TRACKING", is_tracking);
    }
    // 復帰処理
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String value = savedInstanceState.getString("TEXT_VIEW_STR");
        TextView textView1 = (TextView)findViewById(R.id.text1);
        textView1.setText(value);
        is_tracking = savedInstanceState.getBoolean("IS_TRACKING");
        if(is_tracking){
            EnTracking();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            is_tracking = true;
            EnTracking();
            return true;
        }else if (id == R.id.action_save) {
            ;
            OutputStream out;
            try {
                out = openFileOutput("tracklog.txt",MODE_PRIVATE|MODE_APPEND);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

                //追記する
                writer.append(text.getText().toString());
                writer.close();
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    private void EnTracking() {

        // This verification should be done during onStart() because the system calls
        // this method when the user returns to the activity, which ensures the desired
        // location provider is enabled each time the activity resumes from the stopped state.
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);


        }
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double x = loc.getLatitude();

        Date d = new Date(loc.getTime());
        text.append(d.toString() + ", x:" + loc.getLatitude() + ", y:" + loc.getLongitude() + "\n"); // 別スレッドだから無理？

// 重要：requestLocationUpdatesしたままアプリを終了すると挙動がおかしくなる。
        locationManager.removeUpdates(listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,          // 10-second interval.
                10,             // 10 meters.
                listener);
    }
    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location loc) {
            // A new location update is received.  Do something useful with it.  In this case,
            // we're sending the update to a handler which then updates the UI with the new
            // location.
            Date d = new Date(loc.getTime());
            text.append(d.toString() + ", x:" + loc.getLatitude() + ", y:" + loc.getLongitude() + "\n"); // 別スレッドだから無理？
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}

        /**
         * Called when the provider is enabled by the user.
         *
         * @param provider the name of the location provider associated with this
         * update.
         */
        @Override
        public void onProviderEnabled(String provider){}

        /**
         * Called when the provider is disabled by the user. If requestLocationUpdates
         * is called on an already disabled provider, this method is called
         * immediately.
         *
         * @param provider the name of the location provider associated with this
         * update.
         */
        @Override
        public void onProviderDisabled(String provider){}
    };

}
