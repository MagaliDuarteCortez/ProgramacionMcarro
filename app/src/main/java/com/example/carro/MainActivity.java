package com.example.carro;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button connectButton;
    private Button disconnectButton;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private boolean isConnected = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.connect_button);
        disconnectButton = findViewById(R.id.disconnect_button);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent,0);
                } else {
                    connectToDevice();
                }
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectFromDevice();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice() {
        // Cambia "DEVICE_ADDRESS" por la dirección MAC del carrito Bluetooth
        String deviceAddress = "HKKQ89258";
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID para el módulo Bluetooth SPP

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            isConnected = true;

            Toast.makeText(MainActivity.this, "Conectado al dispositivo", Toast.LENGTH_SHORT).show();

            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;

            Toast.makeText(MainActivity.this, "Error al conectar al dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnectFromDevice() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }

            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }

            isConnected = false;

            Toast.makeText(MainActivity.this, "Desconectado del dispositivo", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromDevice();
    }
}
