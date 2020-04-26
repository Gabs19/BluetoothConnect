package com.example.bluetoothconnect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final  int REQUEST_CONECTION_BT = 1;

    ConnectedThread connectedThread;

    private static String addressMac = null;

    UUID uuidCode = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    List <String> comands = new ArrayList<String>();

    boolean conection = false;

    TextView StatusBlue,StatusAdress;
    ImageView bluetooth;
    Button OnBtn, Offbtn, listenbtn,sendBtn;
    ImageButton go,back,left,right;

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
        sendBtn = findViewById(R.id.enviar);

        go = findViewById(R.id.frente);
        back = findViewById(R.id.tras);
        left = findViewById(R.id.esquerda);
        right = findViewById(R.id.direita);

        go.setImageResource(R.drawable.ic_action_up);
        back.setImageResource(R.drawable.ic_action_back);
        left.setImageResource(R.drawable.ic_action_left);
        right.setImageResource(R.drawable.ic_action_rigth);

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


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (conection) {
                    comands.add("f");
//                } else {
//                    message("Ocorreu um erro : Não está conectado");
//                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (conection) {
                    comands.add("t");
//                } else {
//                    message("Ocorreu um erro : Não está conectado");
//                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (conection) {
                    comands.add("d");
//                } else {
//                    message("Ocorreu um erro : Não está conectado");
//                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (conection) {
                    comands.add("e");
//                } else {
//                    message("Ocorreu um erro : Não está conectado");
//                }
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(comands);
                System.out.println(comandLine(comands));
                comands.clear();
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
                        connectedThread = new ConnectedThread(bluetoothSocket);
                        connectedThread.start();
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

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = bluetoothSocket.getInputStream();
                tmpOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                message("erro" + e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte [] buffer = new byte[1024];
            int numBytes; // bytes returned from read()

            while (true) {
                try {
                    numBytes = mmInStream.read(buffer);
                    //Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1, mmBuffer);
                    //readMsg.sendToTarget();
                } catch (IOException e) {
                    message("Input stream was disconnected" + e);
                    break;
                }
            }
        }

        public void enviar(String comando) {
            byte[] enviarDados = comando.getBytes();

            try {
                mmOutStream.write(enviarDados);
            } catch (IOException e) {

            }
        }
    }

    private String comandLine(List<String> comands) {
        String comandLines = " ";
        for (String comand : comands) {
            comandLines+=comand;
        }
        return comandLines;
    }
    
    private void message(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
