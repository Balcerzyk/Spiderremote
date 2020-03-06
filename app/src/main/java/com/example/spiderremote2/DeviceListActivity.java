package com.example.spiderremote2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothState;

import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

public class DeviceListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList <String> macAdress = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        listView = (ListView) findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(macAdress.get(position) != null) {
                    Intent data = new Intent();
                    data.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, macAdress.get(position));
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
                else{
                    Intent data = new Intent();
                    setResult(Activity.RESULT_CANCELED, data);
                    finish();
                }
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setList();
    }

    protected void setList(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        ArrayList <String> arrayListName = new ArrayList<>();
        ArrayList <String> arrayListMac = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String mac = device.getAddress(); // MAC address
                arrayListName.add(deviceName);
                arrayListMac.add(mac);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListName);
            listView.setAdapter(arrayAdapter);
            macAdress = arrayListMac;
        }

    }

    public void buttonOnClick(View view) {
        setList();
    }
}
