package ca.bcit.android_client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ca.bcit.android_client.PayloadValues;
import ca.bcit.android_client.RequestContexts;
import ca.bcit.android_client.RequestTypes;
import ca.bcit.android_client.ResponseContexts;
import ca.bcit.android_client.ResponseTypes;

import androidx.appcompat.app.AppCompatActivity;

import static androidx.core.content.ContextCompat.startActivity;

public class ServerConnection {
    private static String ipaddress;
    private static int port;
    private static Context context;
    private static Intent intent;
    static Socket s;
    DataInputStream in;
    DataOutputStream out;
    ByteBuffer buffer;
    static ServerConnection sc;

    public ServerConnection(String ipaddress, int port, Context context, Intent intent) {
        ServerConnection.ipaddress = ipaddress;
        ServerConnection.port = port;
        ServerConnection.context = context;
        ServerConnection.intent = intent;
        buffer = ByteBuffer.allocate(130);
        this.connect();
        sc = this;
    }

    public static ServerConnection getServerConnection() {
        if (s.isConnected()) {
            return sc;
        } else {
            sc = new ServerConnection(ipaddress, port, context, intent);
        }
        return sc;
    }

    public static int[] prefixUID(int[] arr, int uid) {
        int[] newArr = new int[arr.length + 4];
        for (int i = 3; i >= 0; i--) {
            int thisByte = uid & 0b1111;
            newArr[i] = thisByte;
            uid <<= 4;
        }
        for (int i = 4; i < newArr.length; i++) {
            newArr[i] = arr[i-4];
        }
        return newArr;
    }

    public void connect() {
        new AsyncQuery().execute();
    }

    public void read(readCallback rc) {
        new ReadQuery(rc).execute();
    }

    public void write(int[] arr) {
        new WriteQuery(arr).execute();
    }

    public void disconnect() {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface readCallback {
        void doCallback(ByteBuffer buffer);
    }

    private class ReadQuery extends AsyncTask<Void, Void, Boolean> {
        readCallback rc;
        public ReadQuery(readCallback rc) {
            this.rc = rc;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                buffer.clear();
                in = new DataInputStream(s.getInputStream());
                int readc = in.read(buffer.array());
                Log.w("Count: ", String.valueOf(readc));
                Log.w("Read: ", StandardCharsets.UTF_8.decode(buffer).toString());
                //in.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            try {
                rc.doCallback(buffer);
            } catch(Exception e) {
                Log.e("Error", e.toString());
            }
        }
    }

    private class WriteQuery extends AsyncTask<Void, Void, Boolean> {
        int[] arr;
        public WriteQuery(int[] arr) {
            this.arr = arr;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                buffer.clear();
                out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
                for (int i = 0; i < this.arr.length; i++) {
                    buffer.put((byte)arr[i]);
                }
                Log.w("Write count: ", String.valueOf(this.arr.length));
                out.write(buffer.array(), 0, this.arr.length);
                out.flush();
               // out.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {

        }
    }

    private class AsyncQuery extends AsyncTask<Void, Void, Boolean> {
        int uid;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                InetSocketAddress isa = new InetSocketAddress(ipaddress, port);
                s = new Socket();
                s.connect(isa, 1000);
                write(new int[]{
                    0,0,0,0,
                    RequestTypes.CONFIRMATION.getVal(),
                    RequestContexts.CONFIRM_RULESET.getVal(),
                    2,
                    PayloadValues.PROTOCOL_VERSION.getVal(),
                    PayloadValues.ROCK_PAPER_SCISSORS_ID.getVal()
                });
                read((ByteBuffer buffer) -> {
                    handleResponse(buffer);
                    //if (uid > 0) {
                        Log.w("uid", String.valueOf(uid));
                        intent.putExtra("uid", this.uid);
                        context.startActivity(intent);
                   // }
                });
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {

        }

        public void handleResponse(ByteBuffer buffer) {
            byte status = buffer.get(0);
            byte context = buffer.get(1);
            byte length = buffer.get(2);
            int uid = buffer.getInt(3);

            if (status == ResponseTypes.SUCCESS.getVal()) {
                if (context == ResponseContexts.CONFIRMATION.getVal()) {
                    if (length >= 4) {
                        this.uid = uid;
                    }
                }
            }
        }
    }
}
