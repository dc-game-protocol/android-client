package ca.bcit.android_client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private String ipaddress;
    private int port;
    private final Context context;
    private final Intent intent;
    Socket s;
    DataInputStream in;
    DataOutputStream out;
    ByteBuffer buffer;

    public ServerConnection(String ipaddress, int port, Context context, Intent intent) {
        this.ipaddress = ipaddress;
        this.port = port;
        this.context = context;
        this.intent = intent;
        buffer = ByteBuffer.allocate(10);
        this.connect();
    }

    public void connect() {
        new AsyncQuery().execute();
    }

    public void read() {
        new ReadQuery().execute();
    }

    public void write(int[] arr) {
        new WriteQuery(arr).execute();
    }

    /**
     * Makes a service call to the API to initialize the list of articles.
     */
    private class ReadQuery extends AsyncTask<Void, Void, Boolean> {
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

        }
    }

    /**
     * Makes a service call to the API to initialize the list of articles.
     */
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

    /**
     * Makes a service call to the API to initialize the list of articles.
     */
    private class AsyncQuery extends AsyncTask<Void, Void, Boolean> {
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
                read();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                context.startActivity(intent);
            }
        }
    }
}
