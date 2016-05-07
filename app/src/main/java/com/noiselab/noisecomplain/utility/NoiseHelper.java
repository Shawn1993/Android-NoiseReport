package com.noiselab.noisecomplain.utility;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class NoiseHelper {
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_DEFAULT;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private boolean controllFlag;

    private AudioRecord mAudioRecord;

    private final Handler mHandler = new Handler();

    private RecordThread mRecordThread;

    private Callback mCallback;

    private long mLastCount = 0;

    private double mAverageVolume;

    private long mDuration = Long.MAX_VALUE;

    public NoiseHelper(Callback callback) {
        mCallback = callback;
    }

    public void setmDuration(long millisecond) {
        mDuration = millisecond;
    }

    public void start() {
        if (controllFlag) {
            return;
        }
        controllFlag = true;
        if (mRecordThread == null) {
            mRecordThread = new RecordThread();
            mRecordThread.start();
        }
    }

    public void stop() {
        controllFlag = false;
        mRecordThread = null;
    }

    public void updateState() {

    }

    private void initRecord() {
        if (mAudioRecord == null) {
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        }
        if (mAudioRecord == null) {
            Log.e("AudioRecord", "initial error");
        }
    }

    private void startRecord() {
        initRecord();
        mAudioRecord.startRecording();
    }

    private void stopRecord() {
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord = null;
    }


    private class RecordThread extends Thread {

        private double calculateVolume(short[] buffer, int length) {
            int num = 0;
            for (int i = 0; i < length; i++) {
                num += Math.abs(buffer[i]);
            }
            if (length > 0 && num > 0) {
                return 20 * Math.log10((double) num / (double) length) + AppConfig.DB_OFFSET;
            }
            return 0;
        }

        private double calAverageVolume(double lastAvg, long lastCount, double thisVolume) {
            double averageAmplitude = Math.pow(10, lastAvg / 20) + (Math.pow(10, thisVolume / 20) - Math.pow(10, lastAvg / 20)) / (lastCount + 1);
            return 20 * Math.log10(averageAmplitude);
        }


        @Override
        public void run() {
            super.run();

            startRecord();
            short[] buffer = new short[BUFFER_SIZE];
            long usedTime = System.currentTimeMillis();
            while (controllFlag) {
                if (((System.currentTimeMillis() - usedTime) >= mDuration)) {
                    controllFlag = false;
                }
                int length = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                final double volume = calculateVolume(buffer, length);
                final double avgVolume = calAverageVolume(mAverageVolume, mLastCount, volume);
                mAverageVolume = avgVolume;
                mLastCount++;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (controllFlag) {
                            if (mCallback != null) {
                                mCallback.onUpdate(volume, avgVolume);
                            }
                        } else {
                            if (mCallback != null) {
                                mCallback.onStop(volume, avgVolume);
                            }
                        }
                    }
                });
                try {
                    // 每0.1秒取一次数据
                    Thread.sleep(AppConfig.DB_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopRecord();
        }
    }

    public interface Callback {
        public void onUpdate(double volume, double avgVolume);

        public void onStop(double volume, double avgVolume);
    }
}
