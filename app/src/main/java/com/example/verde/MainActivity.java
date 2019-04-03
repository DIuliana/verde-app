package com.example.verde;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView wifiList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        wifiList = findViewById(R.id.list);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadcastReceiver receiver = new MyBroadcastReceiver(wifiManager, WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        if (receiver.startScan()) {
            wifiListAction();
        }
    }

    private void wifiListAction() {
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ssid = ((TextView) view).getText().toString();
                Toast.makeText(MainActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();

            }
        });
    }


    class MyBroadcastReceiver extends BroadcastReceiver {

        private WifiManager wifiManager;
        String intentAction;

        public MyBroadcastReceiver(WifiManager wifiManager, String intentAction) {
            this.wifiManager = wifiManager;
            this.intentAction = intentAction;

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(intentAction);
            getApplicationContext().registerReceiver(this, intentFilter);
        }

        public boolean startScan() {
            return wifiManager.startScan();
        }

        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                List<String> results = getScanResults();
                setScanResultToUI(results);

            } else {
                // scan failure handling
                scanFailure();
            }
        }

        private void setScanResultToUI(List<String> results) {
            wifiList.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, R.id.label, results));
        }

        private List<String> getScanResults() {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            List<String> display = new ArrayList<>(wifiScanList.size());
            for (ScanResult scanResult : wifiScanList) {
                display.add(scanResult.SSID);
            }
            return display;
        }

        private void scanFailure() {
            //TODO - please try again button
        }

    }

}
