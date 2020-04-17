package com.example.bluetoothconnect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final  int REQUEST_CONECTION_BT = 1;

    private static String addressMac = null;

    UUID uuidCode = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    boolean conection = false;
    TextView StatusBlue,StatusAdress;
    ImageView bluetooth;
    Button OnBtn, Offbtn, listenbtn;

    BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice bluetoothDevice = null;
    BluetoothSocket bluetoothSocket = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBlue = findViewById(R.id.statusBluetooth);
        StatusAdress = findViewById(R.id.ende);

        bluetooth = findViewById(R.id.bluetooth);

        OnBtn = findViewById(R.id.turnOn);
        Offbtn = findViewById(R.id.turnOff);
        listenbtn = findViewById(R.id.listen);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            StatusBlue.setText("Bluetooth is not avaiable!");
        } else {
            StatusBlue.setText("Bluetooth is avaiable!");
        }

        if (bluetoothAdapter.isEnabled()) {
            bluetooth.setImageResource(R.drawable.ic_action_on);
        } else {
            bluetooth.setImageResource(R.drawable.ic_action_off);
        }

        OnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View on) {
                if (!bluetoothAdapter.isEnabled()) {
                    message("Turning On Bluetooth . . .");
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, REQUEST_ENABLE_BT);
                } else {
                    message("Bluetooth is already on");
                }
            }
        });

        Offbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View off) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    message("Turning Off Bluetooth . . .");
                    bluetooth.setImageResource(R.drawable.ic_action_off);
                } else {
                    message("Bluetooth is already off");
                }
            }
        });

        listenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listen) {
                if (conection){
                    try {
                        bluetoothSocket.close();
                        conection = false;
                        listenbtn.setText("Conectar");
                        StatusAdress.setText("");
                        message("bluetooth desconectado!");
                    } catch (IOException erro) {
                        message("Ocorreu um erro: Não estou me conectando ao um modulo/servidor bluetooth.");
                    }
                } else
                    {
                        Intent listDevices = new Intent(MainActivity.this,ListDevices.class);
                        startActivityForResult(listDevices,REQUEST_CONECTION_BT);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK ) {
                    bluetooth.setImageResource(R.drawable.ic_action_on);
                    message("Bluetooth is on");
                } else{
                    message("Could't on bluetooth!");
                }
                break;
            case REQUEST_CONECTION_BT:
                if (resultCode == RESULT_OK) {
                    addressMac = data.getExtras().getString(ListDevices.ENDEREÇO_MAC);
                    message("MAC: " + addressMac);

                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(addressMac);

                    try {
                        bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuidCode);
                        bluetoothSocket.connect();
                        conection = true;
                        message("Conectado ao:" + addressMac);

                        StatusAdress.setText(addressMac);
                        listenbtn.setText("Desconectar");
                    }catch (IOException erro) {

                        message("Ocorreu um erro: Não estou me conectando ao um modulo/servidor bluetooth.");
                    }
                } else {
                    message("Falha ao obter o endereço mac");

                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void message(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
