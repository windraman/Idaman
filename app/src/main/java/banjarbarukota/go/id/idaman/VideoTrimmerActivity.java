//  The MIT License (MIT)

//  Copyright (c) 2018 Intuz Solutions Pvt Ltd.

//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
//  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
//  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package banjarbarukota.go.id.idaman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import banjarbarukota.go.id.idaman.Utility.Utility;
import banjarbarukota.go.id.idaman.customVideoViews.BackgroundTask;
import banjarbarukota.go.id.idaman.customVideoViews.BarThumb;
import banjarbarukota.go.id.idaman.customVideoViews.CustomRangeSeekBar;
import banjarbarukota.go.id.idaman.customVideoViews.OnRangeSeekBarChangeListener;
import banjarbarukota.go.id.idaman.customVideoViews.OnVideoTrimListener;
import banjarbarukota.go.id.idaman.customVideoViews.TileView;


public class VideoTrimmerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtVideoCancel;
    private TextView txtVideoUpload;
    private TextView txtVideoEditTitle;
    private TextView txtVideoTrimSeconds;
    private RelativeLayout rlVideoView;
    private TileView tileView;
    private CustomRangeSeekBar mCustomRangeSeekBarNew;
    private VideoView mVideoView;
    private ImageView imgPlay;
    private SeekBar seekBarVideo;
    private TextView txtVideoLength;

    private int mDuration = 0;
    private int mTimeVideo = 0;
    private int mStartPosition = 0;
    private int mEndPosition = 0;
    // set your max video trim seconds
    private int mMaxDuration = 60;
    private Handler mHandler = new Handler();

    private ProgressDialog mProgressDialog;
    String srcFile;
    String dstFile;

    OnVideoTrimListener mOnVideoTrimListener = new OnVideoTrimListener() {
        @Override
        public void onTrimStarted() {
            // Create an indeterminate progress dialog
            mProgressDialog = new ProgressDialog(VideoTrimmerActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("Saving....");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        public void getResult(Uri uri) {
            mProgressDialog.dismiss();
            Bundle conData = new Bundle();
            conData.putString("INTENT_VIDEO_FILE", uri.getPath());
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void cancelAction() {
            mProgressDialog.dismiss();
        }

        @Override
        public void onError(String message) {
            mProgressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trim);

        txtVideoCancel = (TextView) findViewById(R.id.txtVideoCancel);
        txtVideoUpload = (TextView) findViewById(R.id.txtVideoUpload);
        txtVideoEditTitle = (TextView) findViewById(R.id.txtVideoEditTitle);
        txtVideoTrimSeconds = (TextView) findViewById(R.id.txtVideoTrimSeconds);
        rlVideoView = (RelativeLayout) findViewById(R.id.llVideoView);
        tileView = (TileView) findViewById(R.id.timeLineView);
        mCustomRangeSeekBarNew = ((CustomRangeSeekBar) findViewById(R.id.timeLineBar));
        mVideoView = (VideoView) findViewById(R.id.videoView);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBarVideo);
        txtVideoLength = (TextView) findViewById(R.id.txtVideoLength);

        if (getIntent().getExtras() != null) {
            srcFile = getIntent().getExtras().getString("EXTRA_PATH");
        }
        dstFile = Environment.getExternalStorageDirectory() + "/idaman/video/" + getString(R.string.app_name) + new Date().getTime()
                + Utility.VIDEO_FORMAT;

        tileView.post(new Runnable() {
            @Override
            public void run() {
                setBitmap(Uri.parse(srcFile));
                mVideoView.setVideoURI(Uri.parse(srcFile));
            }
        });

        txtVideoCancel.setOnClickListener(this);
        txtVideoUpload.setOnClickListener(this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onVideoPrepared(mp);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onVideoCompleted();
            }
        });

        // handle your range seekbar changes
        mCustomRangeSeekBarNew.addOnRangeSeekBarListener(new OnRangeSeekBarChangeListener() {
            @Override
            public void onCreate(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeek(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                onSeekThumbs(index, value);
            }

            @Override
            public void onSeekStart(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                if (mVideoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    seekBarVideo.setProgress(0);
                    mVideoView.seekTo(mStartPosition * 1000);
                    mVideoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play);
                }
            }

            @Override
            public void onSeekStop(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                onStopSeekThumbs();
            }
        });

        imgPlay.setOnClickListener(this);

        // handle changes on seekbar for video play
        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mVideoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    seekBarVideo.setMax(mTimeVideo * 1000);
                    seekBarVideo.setProgress(0);
                    mVideoView.seekTo(mStartPosition * 1000);
                    mVideoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mVideoView.seekTo((mStartPosition * 1000) - seekBarVideo.getProgress());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == txtVideoCancel) {
            finish();
        } else if (view == txtVideoUpload) {
            int diff = mEndPosition - mStartPosition;
            if (diff < 3) {
                Toast.makeText(VideoTrimmerActivity.this, getString(R.string.video_length_validation),
                        Toast.LENGTH_LONG).show();
            } else {
                MediaMetadataRetriever
                        mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(VideoTrimmerActivity.this, Uri.parse(srcFile));
                final File file = new File(srcFile);

                //notify that video trimming started
                if (mOnVideoTrimListener != null)
                    mOnVideoTrimListener.onTrimStarted();

                BackgroundTask.execute(
                        new BackgroundTask.Task("", 0L, "") {
                            @Override
                            public void execute() {
                                try {
                                   Utility.startTrim(file, dstFile, mStartPosition * 1000, mEndPosition * 1000, mOnVideoTrimListener);
                                } catch (final Throwable e) {
                                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                }
                            }
                        }
                );
            }

        } else if (view == imgPlay) {
            if (mVideoView.isPlaying()) {
                if (mVideoView != null) {
                    mVideoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play);
                }
            } else {
                if (mVideoView != null) {
                    mVideoView.start();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_pause);
                    if (seekBarVideo.getProgress() == 0) {
                        txtVideoLength.setText("00:00");
                        updateProgressBar();
                    }
                }
            }
        }
    }

    private void setBitmap(Uri mVideoUri) {
        tileView.setVideo(mVideoUri);
    }

    private void onVideoPrepared(@NonNull MediaPlayer mp) {
        // Adjust the size of the video
        // so it fits on the screen
        //TODO manage proportion for video
        /*int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = rlVideoView.getWidth();
        int screenHeight = rlVideoView.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        mVideoView.setLayoutParams(lp);*/

        mDuration = mVideoView.getDuration() / 1000;
        setSeekBarPosition();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (seekBarVideo.getProgress() >= seekBarVideo.getMax()) {
                seekBarVideo.setProgress((mVideoView.getCurrentPosition() - mStartPosition * 1000));
                txtVideoLength.setText(milliSecondsToTimer(seekBarVideo.getProgress()) + "");
                mVideoView.seekTo(mStartPosition * 1000);
                mVideoView.pause();
                seekBarVideo.setProgress(0);
                txtVideoLength.setText("00:00");
                imgPlay.setBackgroundResource(R.drawable.ic_white_play);
            } else {
                seekBarVideo.setProgress((mVideoView.getCurrentPosition() - mStartPosition * 1000));
                txtVideoLength.setText(milliSecondsToTimer(seekBarVideo.getProgress()) + "");
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void setSeekBarPosition() {

        if (mDuration >= mMaxDuration) {
            mStartPosition = 0;
            mEndPosition = mMaxDuration;

            mCustomRangeSeekBarNew.setThumbValue(0, (mStartPosition * 100) / mDuration);
            mCustomRangeSeekBarNew.setThumbValue(1, (mEndPosition * 100) / mDuration);

        } else {
            mStartPosition = 0;
            mEndPosition = mDuration;
        }


        mTimeVideo = mDuration;
        mCustomRangeSeekBarNew.initMaxWidth();
        seekBarVideo.setMax(mMaxDuration * 1000);
        mVideoView.seekTo(mStartPosition * 1000);

        String mStart = mStartPosition + "";
        if (mStartPosition < 10)
            mStart = "0" + mStartPosition;

        int startMin = Integer.parseInt(mStart) / 60;
        int startSec = Integer.parseInt(mStart) % 60;

        String mEnd = mEndPosition + "";
        if (mEndPosition < 10)
            mEnd = "0" + mEndPosition;

        int endMin = Integer.parseInt(mEnd) / 60;
        int endSec = Integer.parseInt(mEnd) % 60;

        txtVideoTrimSeconds.setText(String.format(Locale.US, "%02d:%02d - %02d:%02d", startMin, startSec, endMin, endSec));
    }

    /**
     * called when playing video completes
     */
    private void onVideoCompleted() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        seekBarVideo.setProgress(0);
        mVideoView.seekTo(mStartPosition * 1000);
        mVideoView.pause();
        imgPlay.setBackgroundResource(R.drawable.ic_white_play);
    }

    /**
     * Handle changes of left and right thumb movements
     *
     * @param index index of thumb
     * @param value value
     */
    private void onSeekThumbs(int index, float value) {
        switch (index) {
            case BarThumb.LEFT: {
                mStartPosition = (int) ((mDuration * value) / 100L);
                mVideoView.seekTo(mStartPosition * 1000);
                break;
            }
            case BarThumb.RIGHT: {
                mEndPosition = (int) ((mDuration * value) / 100L);
                break;
            }
        }
        mTimeVideo = (mEndPosition - mStartPosition);
        seekBarVideo.setMax(mTimeVideo * 1000);
        seekBarVideo.setProgress(0);
        mVideoView.seekTo(mStartPosition * 1000);

        String mStart = mStartPosition + "";
        if (mStartPosition < 10)
            mStart = "0" + mStartPosition;

        int startMin = Integer.parseInt(mStart) / 60;
        int startSec = Integer.parseInt(mStart) % 60;

        String mEnd = mEndPosition + "";
        if (mEndPosition < 10)
            mEnd = "0" + mEndPosition;
        int endMin = Integer.parseInt(mEnd) / 60;
        int endSec = Integer.parseInt(mEnd) % 60;

        txtVideoTrimSeconds.setText(String.format(Locale.US, "%02d:%02d - %02d:%02d", startMin, startSec, endMin, endSec));

    }

    private void onStopSeekThumbs() {
//        mMessageHandler.removeMessages(SHOW_PROGRESS);
//        mVideoView.pause();
//        mPlayView.setVisibility(View.VISIBLE);
    }


    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;
        String minutesString;


        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
