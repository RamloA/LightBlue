package com.example.lightblue;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.internal.BeanMessageID;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.DeviceInfo;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;

import auto.parcel.AutoParcel;
import okio.Buffer;


public class MainActivity extends AppCompatActivity {
    final List<Bean>  beans = new ArrayList<>();
    LocationManager locationManager;

    //Bean beaN = beans[0];

    String Name = "98:7B:F3:5A:CE:D9";

    Bean beaN;

    TextView  tv1, tv2, tv3;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        int REQUEST_ENABLE_BT = 1;
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        checkLocationPermission();
        tv1= findViewById(R.id.tv1);
        tv2= findViewById(R.id.tv2);
        tv2= findViewById(R.id.tv3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BeanManager.getInstance().setScanTimeout(50);
        BeanManager.getInstance().startDiscovery(listener);
    }

    BeanDiscoveryListener listener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            beans.add(bean);
            Log.d("Bluetooth", String.valueOf(bean.getDevice().getName()));
            Log.d("Address", String.valueOf(bean.getDevice().getAddress()));
        }

        @Override
        public void onDiscoveryComplete() {
            // This is called when the scan times out, defined by the .setScanTimeout(int seconds) method

            for (Bean bean : beans) {
                Log.d("Bluetooth", String.valueOf(bean.getDevice().getName()));
                //System.out.println(bean.getDevice().getName());   // "Bean"              (example)
                Log.d("Address", String.valueOf(bean.getDevice().getAddress()));
                // System.out.println(bean.getDevice().getAddress());    // "B4:99:4C:1E:BC:75" (example);
            }
        }
    };
    BeanListener beanListener = new BeanListener() {

        @Override
        public void onConnected() {
            //System.out.println("connected to Bean!");
            Context context = getApplicationContext();
            CharSequence text = "connected to Bean!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            beaN.readDeviceInfo(new Callback<DeviceInfo>() {
                @Override
                public void onResult(DeviceInfo deviceInfo) {
                    System.out.println(deviceInfo.hardwareVersion());
                    System.out.println(deviceInfo.firmwareVersion());
                    System.out.println(deviceInfo.softwareVersion());
                }
            });
        }

        @Override
        public void onConnectionFailed() {
            if (!beaN.isConnected()) {
                System.out.println("Could not connect to Bean!");
                Context context = getApplicationContext();
                CharSequence text = "Could not connect to Bean!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }

        @Override
        public void onDisconnected() {
            beaN.disconnect();
            Context context = getApplicationContext();
            CharSequence text = "Disconnected to Bean!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        @Override
        public void onError(BeanError error) {

        }

        @Override
        public void onReadRemoteRssi(int rssi) {
            Context context = getApplicationContext();
            int SS= beaN.getDevice().getBondState();
            //CharSequence text = "SS";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, SS, duration);
            toast.show();
        }

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value) {

        }

        @Override
        public void onSerialMessageReceived(byte[] data) {

        }

    };

    public void On_Scan(View view) {
       /* for (int i = 0; i < beans.size(); i++) {
            if (i == 0) {
               // tv1.setText(beans.get(0).getDevice().getAddress());
            }
        }*/
      Acceleration();
    }

        public void Onconnect(View view) {
            for (int i = 0; i < beans.size(); i++) {
                if (beans.get(i).getDevice().getAddress().equals(Name)) {
                    beaN = beans.get(i);
                    beaN.connect(this, beanListener);
                }
            }
        }

       /*   public void ont_v1(View view) {
            for (int i = 0; i < beans.size(); i++) {
                if (beans.get(i).getDevice().getAddress().equals(tv1.getText())) {
                    beaN = beans.get(i);
                    beaN.connect(this, beanListener);
                }
            }
        }*/

       @AutoParcel
       public abstract class Acceleration implements Parcelable {
           public static Acceleration fromPayload(Buffer buffer) {
               int x = buffer.readShortLe();
               int y = buffer.readShortLe();
               int z = buffer.readShortLe();
               int sensitivity = buffer.readByte() & 0xff;
               double lsbGConversionFactor = sensitivity / 512.0;
               return new AutoParcel_Acceleration(x * lsbGConversionFactor, y * lsbGConversionFactor, z * lsbGConversionFactor);

           }

           public abstract double x();

           public abstract double y();

           public abstract double z();
       }

   // BeanMessageID beanMessage = new BeanMessageID(){
    /* public void Acceleration(){
          /*  beaN.readAcceleration(new Callback<Acceleration>(){
                @Override
                public void onResult(Acceleration result) {
                    Log.i("Acceleration", String.copyValueOf(result));
                }
            });*/

   // };

}
