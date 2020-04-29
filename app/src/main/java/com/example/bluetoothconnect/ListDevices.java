package com.example.bluetoothconnect;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Set;

public class ListDevices extends ListActivity {

    private BluetoothAdapter bluetoothAdapter = null;

    static  String ENDEREÇO_MAC = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> arrayBluetooth = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> devicesPaired = bluetoothAdapter.getBondedDevices();

        if (devicesPaired.size() > 0){
            for (BluetoothDevice devices : devicesPaired ) {
                String nameBT = devices.getName();
                String macBt = devices.getAddress();
                arrayBluetooth.add(nameBT + "\n" + macBt);
            }

        }
        setListAdapter(arrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String info = ((TextView) v).getText().toString();

        String addressMac = info.substring(info.length() - 17);

        Intent addreess = new Intent();
        addreess.putExtra(ENDEREÇO_MAC,addressMac);
        setResult(RESULT_OK,addreess);
        finish();
    }
}
