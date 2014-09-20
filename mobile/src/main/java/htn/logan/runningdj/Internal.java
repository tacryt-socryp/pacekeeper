package htn.logan.runningdj;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.View;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.DataInputStream;
import android.os.Environment;


public class Internal extends Activity
{
    public View rootView;
    public Internal(View rootView){
        rootView = this.rootView;
        this.start();
    }

    boolean m_stop = false;
    AudioTrack at;
    Thread m_noiseThread;

    Runnable m_noiseGenerator = new Runnable()
    {
        public void run()
        {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            /* 8000 bytes per second, 1000 bytes = 125 ms */

            String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
            filepath = "/sdcard";

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

                at.flush();
                at.stop();
                at.release();
                dis.close();
                fin.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    void start()
    {
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

        m_noiseThread = new Thread(m_noiseGenerator);
        m_noiseThread.start();
    }

    void stop()
    {
        m_stop = true;
        at.stop();
    }
}