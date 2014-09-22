package htn.logan.runningdj;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.View;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.DataInputStream;


public class Internal extends Activity {

    public Internal(View rootView) {
        this.start();
    }

    boolean m_stop = false;
    AudioTrack at;
    Thread m_audioThread;
    int playbackRate = 8000;

    public void changeSpeed(double factor) {
       Log.d("playbackRate", Double.toString(factor));
       at.setPlaybackRate((int) (playbackRate * factor));
    }

    Runnable m_audioGenerator = new Runnable() {
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            String filepath = "/sdcard";

            int bufferSize = 512;
            int i = 0;
            byte[] s = new byte[bufferSize];
            FileInputStream fin;
            DataInputStream dis;

            try {
                fin = new FileInputStream(filepath + "/test.wav");
                dis = new DataInputStream(fin);

                while(!m_stop && (i = dis.read(s, 0, bufferSize)) > -1) {
                    at.write(s, 0, i);
                }

                dis.close();
                fin.close();

                at.flush();
                at.release();
                if (at.getPlayState() == at.PLAYSTATE_PLAYING) {
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
        m_stop = false;

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

        m_audioThread = new Thread(m_audioGenerator);
        m_audioThread.start();
    }

    void stop() {
        m_stop = true;
        at.stop();
    }
}