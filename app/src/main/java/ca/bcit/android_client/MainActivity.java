package ca.bcit.android_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText ipEditText;
    EditText portEditText;
    Button rpsButton;
    ServerConnection sc;
    boolean voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipEditText = findViewById(R.id.iptext);
        portEditText = findViewById(R.id.porttext);
        rpsButton = findViewById(R.id.rpsButton);
        voice = false;
    }

    private int connectToServer(Intent intent, int gameId) {
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
        sc = new ServerConnection(ipaddress, port, MainActivity.this, intent, voice, gameId);
        return 1;
    }

    public void startRockpaperScissors (View view){
        Intent intent = new Intent(this, RockPaperScissorsActivity.class);
        int connectionStatus = connectToServer(intent, PayloadValues.ROCK_PAPER_SCISSORS_ID.getVal());
        if (connectionStatus <= 0) {
            return;
        }
    }

    public void startTicTacToe (View view){
        Intent intent = new Intent(this, TicTacToeActivity.class);
        int connectionStatus = connectToServer(intent, PayloadValues.TIC_TAC_TOE_ID.getVal());
        if (connectionStatus <= 0) {
            return;
        }
    }

    public void enableVoice(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1234);
        }
        voice = !voice;
    }
}