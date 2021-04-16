package ca.bcit.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;

import ca.bcit.android_client.ServerConnection;
import ca.bcit.android_client.PayloadValues;
import ca.bcit.android_client.RequestContexts;
import ca.bcit.android_client.RequestTypes;
import ca.bcit.android_client.ResponseContexts;
import ca.bcit.android_client.ResponseTypes;

public class TicTacToeActivity extends AppCompatActivity {
    Button[] buttons = new Button[9];
    ServerConnection sc;
    TextView message;
    int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        sc = ServerConnection.getServerConnection();
        message = findViewById(R.id.responseMessage);
        buttons[0] = findViewById(R.id.topleft);
        buttons[1] = findViewById(R.id.top);
        buttons[2] = findViewById(R.id.topright);
        buttons[3] = findViewById(R.id.left);
        buttons[4] = findViewById(R.id.middle);
        buttons[5] = findViewById(R.id.right);
        buttons[6] = findViewById(R.id.bottomleft);
        buttons[7] = findViewById(R.id.bottom);
        buttons[8] = findViewById(R.id.bottomright);
        uid = getIntent().getIntExtra("uid", 0);
    }

    private void refreshButtonsEnabled() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(buttons[i].getText().toString().isEmpty());
        }
    }

    private void turnOffAllButtons() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(false);
        }
    }

    public void clickTile(View view, int pos) {
        sc.write(new int[]{
            0,0,0,0,
            RequestTypes.GAME_ACTION.getVal(),
            RequestContexts.MAKE_MOVE.getVal(),
            1,
            PayloadValues.MOVE_ROCK.getVal(),
        });
        sc.read(this::handleResponse);
    }

    public void topleft(View view) {
        clickTile(view, PayloadValues.MOVE_TOP_LEFT.getVal());
    }

    public void top(View view) {
        clickTile(view, PayloadValues.MOVE_TOP.getVal());
    }

    public void topright(View view) {
        clickTile(view, PayloadValues.MOVE_TOP_RIGHT.getVal());
    }

    public void left(View view) {
        clickTile(view, PayloadValues.MOVE_LEFT.getVal());
    }

    public void middle(View view) {
        clickTile(view, PayloadValues.MOVE_MIDDLE.getVal());
    }

    public void right(View view) {
        clickTile(view, PayloadValues.MOVE_RIGHT.getVal());
    }

    public void bottomleft(View view) {
        clickTile(view, PayloadValues.MOVE_BOTTOM_LEFT.getVal());
    }

    public void bottom(View view) {
        clickTile(view, PayloadValues.MOVE_BOTTOM.getVal());
    }

    public void bottomright(View view) {
        clickTile(view, PayloadValues.MOVE_BOTTOM_RIGHT.getVal());
    }

    public void handleResponse(ByteBuffer buffer) {
        byte status = buffer.get(0);
        byte context = buffer.get(1);
        byte length = buffer.get(2);
        byte[] payload = new byte[length];
        for (int i = 0; i < length; i++) {
            payload[i] = buffer.get(3 + i);
        }
        if (status == ResponseTypes.SUCCESS.getVal()) {
            if (context == ResponseContexts.CONFIRMATION.getVal()) {
                message.setText("Valid game!");
            }
            if (context == ResponseContexts.INFORMATION.getVal()) {
                message.setText("Unknown or unused response");
            }
            if (context == ResponseContexts.META_ACTION.getVal()) {
                message.setText("Unknown or unused response");
            }
            if (context == ResponseContexts.GAME_ACTION.getVal()) {
                buttons[payload[0]].setText("X");
                message.setText("Awaiting other player's move...");
            }
            turnOffAllButtons();
            sc.read((ByteBuffer buff) -> {
                refreshButtonsEnabled();
                handleResponse(buff);
            });
        }
        if (status == ResponseTypes.UPDATE.getVal()) {
            if (context == ResponseContexts.START_GAME.getVal()) {
                message.setText("Starting game...");
            }
            if (context == ResponseContexts.MOVE_MADE.getVal()) {
                buttons[payload[0]].setText("O");
                message.setText("Move made by opponent.");
            }
            if (context == ResponseContexts.END_OF_GAME.getVal()) {
                int winLoss = payload[0];
                String winLossMsg = (winLoss == 1 ? "Win!" : winLoss == 2 ? "Tie!" : "Loss!");

                message.setText("GAME OVER\n" +winLossMsg);
            }
            if (context == ResponseContexts.OPPONENT_DISCONNECTED.getVal()) {
                message.setText("Opponent disconnected! Please quit the game.");
            }
            refreshButtonsEnabled();
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

    public void quit(View view) {
        sc.write(new int[]{
            0,0,0,0,
            RequestTypes.META_ACTION.getVal(),
            RequestContexts.QUIT.getVal(),
            0,
        });
        sc.disconnect();
        finish();
    }
}