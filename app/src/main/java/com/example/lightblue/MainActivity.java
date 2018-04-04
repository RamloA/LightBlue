package com.example.lightblue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BeanDiscoveryListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void Onconnect(View view) {
        BeanManager.getInstance().setScanTimeout(15);  // Timeout in seconds, optional, default is 30 seconds
        BeanManager.getInstance().startDiscovery(listener);

    }

    final List<Bean> beans = new ArrayList<>();

    BeanDiscoveryListener listener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            beans.add(bean);
        }

        @Override
        public void onDiscoveryComplete() {
            // This is called when the scan times out, defined by the .setScanTimeout(int seconds) method

            for (Bean bean : beans) {
                System.out.println(bean.getDevice().getName());   // "Bean"              (example)
                System.out.println(bean.getDevice().getAddress());    // "B4:99:4C:1E:BC:75" (example)
            }

        }
    };


}
