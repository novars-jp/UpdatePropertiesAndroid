package jp.novars.updateproperties;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.novars.mabeee.sdk.App;
import jp.novars.mabeee.sdk.Device;
import jp.novars.mabeee.sdk.ui.ScanActivity;

public class MainActivity extends AppCompatActivity {

    private String mText = "";
    private TextView mTextView;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (App.MABEEE_RSSI_DID_UPDATE_NOTIFICATION.equals(intent.getAction())) {
                Long identifier = intent.getLongExtra("Identifier", 0);
                Device device = App.getInstance().getDevice(identifier);
                mText += "RSSI " + device.getRssi() + "\n";
                mTextView.setText(mText);
                return;
            }
            if (App.MABEEE_BATTERY_VOLTAGE_DID_UPDATE_NOTIFICATION.equals(intent.getAction())) {
                Long identifier = intent.getLongExtra("Identifier", 0);
                Device device = App.getInstance().getDevice(identifier);
                mText += "Battery Voltage " + device.getBatteryVoltage() + "\n";
                mTextView.setText(mText);
                return;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getInstance().initializeApp(getApplicationContext());

        Button scanButton = (Button)findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        Button updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Device[] devices = App.getInstance().getDevices();
                for (Device device : devices) {
                    device.updateRssi();
                    device.updateBatteryVoltage();
                }
            }
        });

        mTextView = (TextView)findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getInstance().registerBroadcastReceiver(mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getInstance().unregisterBroadcastReceiver(mReceiver);
    }
}
