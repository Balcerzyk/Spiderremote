package com.example.spiderremote2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {
    BluetoothSPP bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = new BluetoothSPP(this);

        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth nie jest dostępny", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        }

        if (!bluetooth.isServiceAvailable()) {
            bluetooth.setupService();
            bluetooth.startService(BluetoothState.DEVICE_OTHER);
        }

        Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        Button forward = findViewById(R.id.forwardButton);
        Button back = findViewById(R.id.backButton);
        Button left = findViewById(R.id.leftButton);
        Button right = findViewById(R.id.rightButton);

        setButtonsListener(forward);
        setButtonsListener(back);
        setButtonsListener(left);
        setButtonsListener(right);

    }

    public void onStart() {
        super.onStart();

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getBaseContext(),"Połączono",Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getBaseContext(),"Utracono połączenie",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getBaseContext(),"Brak połączenia",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
        bluetooth.send(new byte[] {29}, false);
        bluetooth.stopService();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!bluetooth.isBluetoothEnabled()) {
            Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            return;
        }
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.connect(data);
                return;
            }
        }
        Toast.makeText(getApplicationContext(), "Wystąpił błąd", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setButtonsListener(final Button button){
        button.setBackgroundColor(Color.GRAY);
        button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    button.setBackgroundColor(Color.RED);
                    sendData(button);
                }
                if ((event.getAction() == MotionEvent.ACTION_UP)) {
                    bluetooth.send(new byte[] {29}, false);
                    button.setBackgroundColor(Color.GRAY);
                }
                return true;
            }
        });
    }

    public void sendData(Button button){
        switch(button.getId()){
            case R.id.forwardButton:
                bluetooth.send(new byte[] {20}, false);
                break;
            case R.id.backButton:
                bluetooth.send(new byte[] {9}, false);
                break;
            case R.id.leftButton:
                bluetooth.send(new byte[] {17}, false);
                break;
            case R.id.rightButton:
                bluetooth.send(new byte[] {12}, false);
                break;
            default:
                bluetooth.send(new byte[] {29}, false);
        }
    }
}
