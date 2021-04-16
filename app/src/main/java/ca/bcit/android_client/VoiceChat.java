package ca.bcit.android_client;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class VoiceChat {
    private static final int audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    private static final int sampleRateInHz = 10000;
    private static final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static final int channelOutConfig = AudioFormat.CHANNEL_OUT_MONO;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    private static final int buffSize = 5008;
    private static String ipAddress;
    private static int port;
    private static boolean activeRecording;
    private static boolean activeListening;
    DatagramSocket socket;

    public void setIpAddress(String ipAddress) {
        VoiceChat.ipAddress = ipAddress;
    }
    public void setPort(int port) {
        VoiceChat.port = port;
    }
    public static void stop() {
        activeRecording = false;
        activeListening = false;
    }

    public void start() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        startRecording();
        startListening();
    }

    private void startRecording() {
        if (!activeRecording) {
            activeRecording = true;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AudioRecord ar = new AudioRecord(audioSource,
                            sampleRateInHz,
                            channelConfig,
                            audioFormat,
                            bufferSizeInBytes);
                    byte[] buffer = new byte[buffSize];
                    try {
                        ar.startRecording();
                        while (activeRecording) {
                            int count = ar.read(buffer, 0, buffSize);
                            DatagramPacket packet = new DatagramPacket(buffer, count, InetAddress.getByName(ipAddress), port);
                            socket.send(packet);
                            Log.w("Sent...", String.valueOf(count));
                        }
                        ar.stop();
                        ar.release();
                        socket.disconnect();
                        socket.close();
                        activeRecording = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        ar.stop();
                        ar.release();
                        socket.disconnect();
                        socket.close();
                        activeRecording = false;
                    }

                }
            });
            thread.start();
        }
    }

    private void startListening() {
        if (!activeListening) {
            activeListening = true;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRateInHz,
                        channelOutConfig,
                        audioFormat,
                        buffSize,
                        AudioTrack.MODE_STREAM);
                    byte[] buffer = new byte[buffSize];
                    at.play();
                    try {
                        while (activeListening) {
                            DatagramPacket packet = new DatagramPacket(buffer, buffSize);
                            socket.receive(packet);
                            Log.w("Received...", String.valueOf(packet.getLength()));
                            at.write(packet.getData(), 0, buffSize);
                        }
                        at.stop();
                        at.release();
                        socket.disconnect();
                        socket.close();
                        activeListening = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        at.stop();
                        at.release();
                        socket.disconnect();
                        socket.close();
                        activeListening = false;
                    }

                }
            });
            thread.start();
        }
    }
}
