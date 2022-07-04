package com.example.firstflow;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioCaptureService extends Service {

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection = null;

    private Thread audioCaptureThread;
    private AudioRecord audioRecord = null;

    private final int SERVICE_ID = 123;
    private final String NOTIFICATION_CHANNEL_ID = "AudioCapture channel";

    private final int NUM_SAMPLES_PER_READ = 1024;
    private final int BYTES_PER_SAMPLE = 2; // 2 bytes since we hardcoded the PCM 16-bit format
    private final int BUFFER_SIZE_IN_BYTES = NUM_SAMPLES_PER_READ * BYTES_PER_SAMPLE;

    public static final String ACTION_STOP = "AudioCaptureService:Stop";
    public static final String ACTION_START = "AudioCaptureService:Start";
    public static final String EXTRA_RESULT_DATA = "AudioCaptureService:Extra:ResultData";
    public static final int SAMPLING_RATE = 8000;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(
                SERVICE_ID,
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).build()
        );

        mediaProjectionManager =
                (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);


    }

    public AudioCaptureService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            serviceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Audio Capture Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = ContextCompat.getSystemService(this, NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_START: {
                    mediaProjection =
                            (MediaProjection) mediaProjectionManager.getMediaProjection(
                                    Activity.RESULT_OK,
                                    intent.getParcelableExtra(EXTRA_RESULT_DATA)
                            );
                    startAudioCapture();
                    return Service.START_STICKY;
                }
                case ACTION_STOP: {
                    stopAudioCapture();

                    return Service.START_NOT_STICKY;
                }
                default:
                    throw new IllegalArgumentException("Unexpected action received: ${intent.action}");
            }
        } else {
            return Service.START_NOT_STICKY;
        }
    }

    private void startAudioCapture() {
        AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA) // TODO provide UI options for inclusion/exclusion
                .build();

        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLING_RATE)
                .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                .build();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord.Builder()
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
                .setAudioPlaybackCaptureConfig(config)
                .build();

        audioRecord.startRecording();
        audioCaptureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                File outputFile = createAudioFile();
                writeAudioToFile(outputFile);
            }
        });
        audioCaptureThread.start();
    }

    private File createAudioFile() {

        File audioCapturesDirectory = new File(getExternalFilesDir(null), "/AudioCaptures");
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs();
        }
        String timestamp = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.US).format(new Date());
        String fileName = "Capture-" + timestamp + ".pcm";

        return new File(audioCapturesDirectory.getAbsolutePath() + "/" + fileName);
    }

    private void writeAudioToFile(File outputFile) {
        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(outputFile);

            short[] capturedAudioSamples =
                    new short[NUM_SAMPLES_PER_READ];

            while (!audioCaptureThread.isInterrupted()) {
                audioRecord.read(capturedAudioSamples, 0, NUM_SAMPLES_PER_READ);

                // This loop should be as fast as possible to avoid artifacts in the captured audio
                // You can uncomment the following line to see the capture samples but
                // that will incur a performance hit due to logging I/O.
                // Log.v(LOG_TAG, "Audio samples captured: ${capturedAudioSamples.toList()}")

                fileOutputStream.write(
                        shortArrayToByteArray(capturedAudioSamples),
                        0,
                        BUFFER_SIZE_IN_BYTES
                );
            }

            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudioCapture() {
        try {

            audioCaptureThread.interrupt();
            audioCaptureThread.join();

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;

            mediaProjection.stop();
            stopSelf();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] shortArrayToByteArray(short[] shortArr) {
        // Samples get translated into bytes following little-endianness:
        // least significant byte first and the most significant byte last
        byte[] bytes = new byte[shortArr.length * 2];
        for (int i = 0; i < shortArr.length; i++) {
            bytes[i * 2] = (byte) (shortArr[i] & 0x00FF);
            bytes[i * 2 + 1] = (byte) ((int) shortArr[i] >> 8);
            shortArr[i] = 0;
        }
        return bytes;
    }

}