package ca.bcit.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ca.bcit.android_client.ServerConnection;

public class RockPaperScissorsActivity extends AppCompatActivity {
    ServerConnection sc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rock_paper_scissors);
        sc = ServerConnection.getServerConnection();
    }

    public void rock(View view) {
        sc.write(new int[]{
            0,0,0,0,
            RequestTypes.GAME_ACTION.getVal(),
            RequestContexts.MAKE_MOVE.getVal(),
            1,
            PayloadValues.MOVE_ROCK.getVal(),
        });
        sc.read((ByteBuffer buffer) -> {
            Log.w("Rock: ", StandardCharsets.UTF_8.decode(buffer).toString());
        });
    }

    public void paper(View view) {
        sc.write(new int[]{
            0,0,0,0,
            RequestTypes.GAME_ACTION.getVal(),
            RequestContexts.MAKE_MOVE.getVal(),
            1,
            PayloadValues.MOVE_PAPER.getVal(),
        });
        sc.read((ByteBuffer buffer) -> {
            Log.w("Paper: ", StandardCharsets.UTF_8.decode(buffer).toString());
        });
    }

    public void scissors(View view) {
        sc.write(new int[]{
            0,0,0,0,
            RequestTypes.GAME_ACTION.getVal(),
            RequestContexts.MAKE_MOVE.getVal(),
            1,
            PayloadValues.MOVE_SCISSORS.getVal(),
        });
        sc.read((ByteBuffer buffer) -> {
            Log.w("Scissors: ", StandardCharsets.UTF_8.decode(buffer).toString());
        });
    }

    public void quit(View view) {
        sc.write(new int[]{
            0,0,0,0,
            RequestTypes.META_ACTION.getVal(),
            RequestContexts.QUIT.getVal(),
            0,
        });
        sc.read((ByteBuffer buffer) -> {
            Log.w("Quit: ", StandardCharsets.UTF_8.decode(buffer).toString());
        });
    }
}