package com.logan.pacekeeper;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.DataInputStream;


public class Internal extends Activity {

    boolean stopped = false;
    AudioTrack at;
    Thread audioThread;
    int playbackRate = 8000;

    Runnable audioPlay = new Runnable() {
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            String filePath = Environment.getExternalStorageDirectory().getPath();

            int bufferSize = 512;
            int i;
            byte[] s = new byte[bufferSize];
            FileInputStream fin;
            DataInputStream dis;

            try {
                fin = new FileInputStream(filePath + "/test.wav");
                dis = new DataInputStream(fin);

                while(!stopped && (i = dis.read(s, 0, bufferSize)) > -1) {
                    at.write(s, 0, i);
                }

                dis.close();
                fin.close();

                at.flush();
                at.release();
                if (at.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    at.stop();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    void start() {
        stopped = false;

        int minBufferSize = AudioTrack.getMinBufferSize(
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        at = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);

        at.play();

        audioThread = new Thread(audioPlay);
        audioThread.start();
    }

    void stop() {
        stopped = true;
        at.stop();
    }

    public void changeSpeed(double factor) {
        Log.d("playbackRate", Double.toString(factor));
        at.setPlaybackRate((int) (playbackRate * factor));
    }
}