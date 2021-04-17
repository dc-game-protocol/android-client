package ca.bcit.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;

public class RockPaperScissorsActivity extends AppCompatActivity {
    Button rockButton;
    Button paperButton;
    Button scissorsButton;

    ServerConnection sc;
    TextView message;
    byte[] uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rock_paper_scissors);
        message = findViewById(R.id.responseMessage);
        sc = ServerConnection.getServerConnection();
        rockButton = findViewById(R.id.rockbutton);
        paperButton = findViewById(R.id.scissorsbutton);
        scissorsButton = findViewById(R.id.paperbutton);
        uid = getIntent().getByteArrayExtra("uid");
        toggleButtons(false);
        sc.read((ByteBuffer buffer) -> {
           handleResponse(buffer);
        });
    }

    private void toggleButtons(boolean status) {
        rockButton.setEnabled(status);
        paperButton.setEnabled(status);
        scissorsButton.setEnabled(status);
    }

    public void handleResponse(ByteBuffer buffer) {
        byte status = buffer.get(0);
        byte context = buffer.get(1);
        byte length = buffer.get(2);
        byte[] payload = new byte[length];
        for (int i = 0; i < length; i++) {
            payload[i] = buffer.get(3 + i);
        }
        toggleButtons(true);
        if (status == ResponseTypes.SUCCESS.getVal()) {
            if (context == ResponseContexts.CONFIRMATION.getVal()) {
                message.setText("Valid game! Awaiting other player...");
            }
            if (context == ResponseContexts.INFORMATION.getVal()) {
                message.setText("Unknown or unused response");
            }
            if (context == ResponseContexts.META_ACTION.getVal()) {
                message.setText("Unknown or unused response");
            }
            if (context == ResponseContexts.GAME_ACTION.getVal()) {
                message.setText("Move made! Awaiting other player's move...");
            }
            sc.read(this::handleResponse);
        }
        if (status == ResponseTypes.UPDATE.getVal()) {
            if (context == ResponseContexts.START_GAME.getVal()) {
                toggleButtons(true);
                message.setText("Starting game...");
            }
            if (context == ResponseContexts.END_OF_GAME.getVal()) {
                toggleButtons(false);
                int winLoss = payload[0];
                int opponentMove = payload[1];
                String winLossMsg = (winLoss == 1 ? "Win!" : winLoss == 2 ? "Loss!" : "Tie!");
                String opponentMoveMsg = "Opponent's Move: " + (opponentMove == 1 ? "Rock" : opponentMove == 2 ? "Paper" : "Scissors");

                message.setText("GAME OVER\n" +winLossMsg + "\n" + opponentMoveMsg);
            }
            if (context == ResponseContexts.OPPONENT_DISCONNECTED.getVal()) {
                message.setText("Opponent disconnected! Please quit the game.");
            }
        }
        if (status == ResponseTypes.INVALID_REQUEST.getVal()) {
            message.setText("Invalid Request");
        }
        if (status == ResponseTypes.INVALID_UID.getVal()) {
            message.setText("Invalid UID");
        }
        if (status == ResponseTypes.INVALID_TYPE.getVal()) {
            message.setText("Invalid Type");
        }
        if (status == ResponseTypes.INVALID_CONTEXT.getVal()) {
            message.setText("Invalid Context");
        }
        if (status == ResponseTypes.INVALID_PAYLOAD.getVal()) {
            message.setText("Invalid Payload");
        }
        if (status == ResponseTypes.SERVER_ERROR.getVal()) {
            message.setText("Server Error");
        }
        if (status == ResponseTypes.INVALID_ACTION.getVal()) {
            message.setText("Invalid Action");
        }
        if (status == ResponseTypes.ACTION_OUT_OF_TURN.getVal()) {
            message.setText("Action out of turn");
        }
    }

    public void clickButton(View view, int move) {
        rockButton.setEnabled(false);
        scissorsButton.setEnabled(false);
        paperButton.setEnabled(false);
        int[] arr = ServerConnection.prefixUID(new int[]{
            RequestTypes.GAME_ACTION.getVal(),
            RequestContexts.MAKE_MOVE.getVal(),
            1,
            move,
        }, uid);

        sc.write(arr);
        sc.read((ByteBuffer buffer) -> {
            handleResponse(buffer);
        });
    }

    public void rock(View view) {
        clickButton(view, PayloadValues.MOVE_ROCK.getVal());
    }

    public void paper(View view) {
        clickButton(view, PayloadValues.MOVE_PAPER.getVal());
    }

    public void scissors(View view) {
        clickButton(view, PayloadValues.MOVE_SCISSORS.getVal());
    }

    public void quit(View view) {
        int[] arr = ServerConnection.prefixUID(new int[]{
            RequestTypes.META_ACTION.getVal(),
            RequestContexts.QUIT.getVal(),
            0,
        }, uid);
        sc.write(arr);
        sc.disconnect();
        VoiceChat.stop();
        finish();
    }
}