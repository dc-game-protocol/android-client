package ca.bcit.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import ca.bcit.android_client.ServerConnection;

public class MainActivity extends AppCompatActivity {
    EditText ipEditText;
    EditText portEditText;
    Button rpsButton;
    ServerConnection sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipEditText = findViewById(R.id.iptext);
        portEditText = findViewById(R.id.porttext);
        rpsButton = findViewById(R.id.rpsButton);
    }

    private int connectToServer(Intent intent) {
        String ipaddress = ipEditText.getText().toString();
        String portText = portEditText.getText().toString();
        if (ipaddress.equals("") || portText.equals("")) {
            return -1;
        }
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            return -1;
        }
        sc = new ServerConnection(ipaddress, port, MainActivity.this, intent);
        return 1;
    }

    public void startRockpaperScissors (View view){
        Intent intent = new Intent(this, RockPaperScissorsActivity.class);
        int connectionStatus = connectToServer(intent);
        if (connectionStatus <= 0) {
            return;
        }
    }

    public void startTicTacToe (View view){
        Intent intent = new Intent(this, TicTacToeActivity.class);
        int connectionStatus = connectToServer(intent);
        if (connectionStatus <= 0) {
            return;
        }
    }
}