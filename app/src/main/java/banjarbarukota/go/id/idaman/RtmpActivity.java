package banjarbarukota.go.id.idaman;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Utility.OkHttpPostHandler;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.Camera1Base}
 * {@link com.pedro.rtplibrary.rtmp.RtmpCamera1}
 */
public class RtmpActivity extends AppCompatActivity
    implements Button.OnClickListener, ConnectCheckerRtmp, SurfaceHolder.Callback,
    View.OnTouchListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

  private Integer[] orientations = new Integer[] { 0, 90, 180, 270 };

  private RtmpCamera1 rtmpCamera1;
  private Button bStartStop, bRecord;
  private EditText etUrl;
  private String currentDateAndTime = "";
  private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
      + "/" + R.string.app_name);
  //options menu
  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private ActionBarDrawerToggle actionBarDrawerToggle;
  private RadioGroup rgChannel;
  private Spinner spResolution;
  private CheckBox cbEchoCanceler, cbNoiseSuppressor, cbHardwareRotation;
  private EditText etVideoBitrate, etFps, etAudioBitrate, etSampleRate, etWowzaUser,
      etWowzaPassword;
  private String lastVideoBitrate;
  private TextView tvBitrate;

  OkHttpPostHandler postHandler;
  String streamname, hlsUrl, recUrl, postId;

  int CAMERA_REQUEST_CODE = 50;
  int VIDEO_TRIM = 51;
  String isi;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    Intent i = getIntent();
    String pwd = i.getStringExtra("pwd");
    isi = i.getStringExtra("isi");

    hlsUrl = "https://streaming.banjarbarukota.go.id/cctv/publikhls/";
    recUrl = "https://streaming.banjarbarukota.go.id/recorder/";

      setContentView(R.layout.activity_custom);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      SurfaceView surfaceView = findViewById(R.id.surfaceView);
      surfaceView.getHolder().addCallback(this);
      surfaceView.setOnTouchListener(this);

      String[] perms = { "android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
      if (!EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
          EasyPermissions.requestPermissions(RtmpActivity.this, "Meminta Ijin Akses ", CAMERA_REQUEST_CODE, perms);
      }

      rtmpCamera1 = new RtmpCamera1(surfaceView, this);
      prepareOptionsMenuViews();
      tvBitrate = findViewById(R.id.tv_bitrate);
      etUrl = findViewById(R.id.et_rtp_url);
      //etUrl.setHint(R.string.hint_rtmp);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
      String skrg = sdf.format(new Date());
      streamname = ((GlobalClass) getApplication()).getStringPref("username") + skrg ;
      etUrl.setText("rtmp://103.133.56.188:1935/idaman/live" + streamname);
      //etUrl.setVisibility(View.GONE);

      bStartStop = findViewById(R.id.b_start_stop);
      bStartStop.setOnClickListener(this);
      bRecord = findViewById(R.id.b_record);
      bRecord.setOnClickListener(this);
      Button switchCamera = findViewById(R.id.switch_camera);
      switchCamera.setOnClickListener(this);

  }

  private void prepareOptionsMenuViews() {
    drawerLayout = findViewById(R.id.activity_custom);
    navigationView = findViewById(R.id.nv_rtp);

    navigationView.inflateMenu(R.menu.options_rtmp);
    actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.rtsp_streamer,
        R.string.rtsp_streamer) {

      public void onDrawerOpened(View drawerView) {
        actionBarDrawerToggle.syncState();
        lastVideoBitrate = etVideoBitrate.getText().toString();
      }

      public void onDrawerClosed(View view) {
        actionBarDrawerToggle.syncState();
        if (lastVideoBitrate != null && !lastVideoBitrate.equals(
            etVideoBitrate.getText().toString()) && rtmpCamera1.isStreaming()) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int bitrate = Integer.parseInt(etVideoBitrate.getText().toString()) * 1024;
            rtmpCamera1.setVideoBitrateOnFly(bitrate);
            Toast.makeText(RtmpActivity.this, "New bitrate: " + bitrate, Toast.LENGTH_SHORT).
                show();
          } else {
            Toast.makeText(RtmpActivity.this, "Bitrate on fly ignored, Required min API 19",
                Toast.LENGTH_SHORT).show();
          }
        }
      }
    };
    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    //checkboxs
    cbEchoCanceler =
        (CheckBox) navigationView.getMenu().findItem(R.id.cb_echo_canceler).getActionView();
    cbNoiseSuppressor =
            (CheckBox) navigationView.getMenu().findItem(R.id.cb_noise_suppressor).getActionView();
    cbHardwareRotation =
        (CheckBox) navigationView.getMenu().findItem(R.id.cb_hardware_rotation).getActionView();
    //radiobuttons
    RadioButton rbTcp =
        (RadioButton) navigationView.getMenu().findItem(R.id.rb_tcp).getActionView();
    rgChannel = (RadioGroup) navigationView.getMenu().findItem(R.id.channel).getActionView();
    rbTcp.setChecked(true);
    //spinners
    spResolution = (Spinner) navigationView.getMenu().findItem(R.id.sp_resolution).getActionView();

    ArrayAdapter<Integer> orientationAdapter =
        new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
    orientationAdapter.addAll(orientations);

    ArrayAdapter<String> resolutionAdapter =
        new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
    List<String> list = new ArrayList<>();
    for (Camera.Size size : rtmpCamera1.getResolutionsBack()) {
      list.add(size.width + "X" + size.height);
    }
    resolutionAdapter.addAll(list);
    spResolution.setAdapter(resolutionAdapter);
    //edittexts
    etVideoBitrate = (EditText) navigationView.getMenu().findItem(R.id.et_video_bitrate).getActionView();
    etFps = (EditText) navigationView.getMenu().findItem(R.id.et_fps).getActionView();
    etAudioBitrate =
        (EditText) navigationView.getMenu().findItem(R.id.et_audio_bitrate).getActionView();
    etSampleRate = (EditText) navigationView.getMenu().findItem(R.id.et_samplerate).getActionView();
    etVideoBitrate.setText("2500");
    etFps.setText("30");
    etAudioBitrate.setText("128");
    etSampleRate.setText("44100");
    etWowzaUser = (EditText) navigationView.getMenu().findItem(R.id.et_wowza_user).getActionView();
    etWowzaPassword = (EditText) navigationView.getMenu().findItem(R.id.et_wowza_password).getActionView();

  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_streaming, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
          drawerLayout.openDrawer(GravityCompat.START);
        } else {
          drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
      case R.id.microphone:
        if (!rtmpCamera1.isAudioMuted()) {
          item.setIcon(getResources().getDrawable(R.drawable.ic_mic_off_black_24dp));
          rtmpCamera1.disableAudio();
        } else {
          item.setIcon(getResources().getDrawable(R.drawable.ic_mic_black_24dp));
          rtmpCamera1.enableAudio();
        }
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.b_start_stop:
        Log.d("TAG_R", "b_start_stop: ");
        if (!rtmpCamera1.isStreaming()) {
          bStartStop.setText(getResources().getString(R.string.stop_button));
          String user = etWowzaUser.getText().toString();
          String password = etWowzaPassword.getText().toString();
          if (!user.isEmpty() && !password.isEmpty()) {
            rtmpCamera1.setAuthorization(user, password);
          }
          if (rtmpCamera1.isRecording() || prepareEncoders()) {
            //kirimberita
            String si;
            if(isi.length()>0){
                si = isi;
            }else{
                si = "Sedang Siaran Langsung.";
            }
            postHandler = new OkHttpPostHandler();
            postId = postHandler.addPost(((GlobalClass) getApplication()).getStringPref("user_id"),"live","tes",hlsUrl+"live"+streamname + ".m3u8",si,"7");
            if(!postId.equals("null")) {
              rtmpCamera1.startStream(etUrl.getText().toString());
            }else{
              Toast.makeText(this, "Gagal mengirim data.",
                      Toast.LENGTH_SHORT).show();
              bStartStop.setText(getResources().getString(R.string.start_button));
            }
          } else {
            //If you see this all time when you start stream,
            //it is because your encoder device dont support the configuration
            //in video encoder maybe color format.
            //If you have more encoder go to VideoEncoder or AudioEncoder class,
            //change encoder and try
            Toast.makeText(this, "Error preparing stream, This device cant do it",
                Toast.LENGTH_SHORT).show();
            bStartStop.setText(getResources().getString(R.string.start_button));
          }
        } else {
          bStartStop.setText(getResources().getString(R.string.start_button));
          rtmpCamera1.stopStream();
        }
        break;
      case R.id.b_record:
        Log.d("TAG_R", "b_start_stop: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          if (!rtmpCamera1.isRecording()) {
            try {
              if (!folder.exists()) {
                folder.mkdir();
              }
              SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
              currentDateAndTime = sdf.format(new Date());
              if (!rtmpCamera1.isStreaming()) {
                if (prepareEncoders()) {
                  rtmpCamera1.startRecord(
                      folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                  bRecord.setText(R.string.stop_record);
                  Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(this, "Error preparing stream, This device cant do it",
                      Toast.LENGTH_SHORT).show();
                }
              } else {
                rtmpCamera1.startRecord(
                    folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                bRecord.setText(R.string.stop_record);
                Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
              }
            } catch (IOException e) {
              //rtmpCamera1.stopRecord();
              bRecord.setText(R.string.start_record);
              Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          } else {
            rtmpCamera1.stopRecord();
            bRecord.setText(R.string.start_record);

            // kirim ke berita activity

            Toast.makeText(this,
                "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                Toast.LENGTH_SHORT).show();
            currentDateAndTime = "";
          }
        } else {
          Toast.makeText(this, "You need min JELLY_BEAN_MR2(API 18) for do it...",
              Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.switch_camera:
        try {
          rtmpCamera1.switchCamera();
        } catch (final CameraOpenException e) {
          Toast.makeText(RtmpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        break;
    }
  }

  private boolean prepareEncoders() {
    Camera.Size resolution =
        rtmpCamera1.getResolutionsBack().get(spResolution.getSelectedItemPosition());
    int width = resolution.width;
    int height = resolution.height;
    return rtmpCamera1.prepareVideo(width, height, Integer.parseInt(etFps.getText().toString()),
        Integer.parseInt(etVideoBitrate.getText().toString()) * 1024,
        cbHardwareRotation.isChecked(), CameraHelper.getCameraOrientation(this))
        && rtmpCamera1.prepareAudio(Integer.parseInt(etAudioBitrate.getText().toString()) * 1024,
        Integer.parseInt(etSampleRate.getText().toString()),
        rgChannel.getCheckedRadioButtonId() == R.id.rb_stereo, cbEchoCanceler.isChecked(),
        cbNoiseSuppressor.isChecked());
  }

  @Override
  public void onConnectionSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(RtmpActivity.this, "Connection success id:" + postId, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onConnectionFailedRtmp(final String reason) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(RtmpActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
            .show();
        rtmpCamera1.stopStream();
        bStartStop.setText(getResources().getString(R.string.start_button));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
            && rtmpCamera1.isRecording()) {
          rtmpCamera1.stopRecord();
          bRecord.setText(R.string.start_record);
          Toast.makeText(RtmpActivity.this,
              "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
              Toast.LENGTH_SHORT).show();
          currentDateAndTime = "";
        }
      }
    });
  }

  @Override
  public void onNewBitrateRtmp(final long bitrate) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        tvBitrate.setText(bitrate + " bps");
      }
    });
  }

  @Override
  public void onDisconnectRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        postHandler = new OkHttpPostHandler();
        Integer stats = postHandler.updateGambarUtama(postId,recUrl+"live"+streamname+".mp4");
        Log.d("ustat",String.valueOf(stats));
        Toast.makeText(RtmpActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
            && rtmpCamera1.isRecording()) {
          rtmpCamera1.stopRecord();
          bRecord.setText(R.string.start_record);
          Toast.makeText(RtmpActivity.this,
              "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
              Toast.LENGTH_SHORT).show();
          currentDateAndTime = "";
        }
      }
    });
  }

  @Override
  public void onAuthErrorRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(RtmpActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(RtmpActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    drawerLayout.openDrawer(GravityCompat.START);
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    rtmpCamera1.startPreview();
    // optionally:
    //rtmpCamera1.startPreview(CameraHelper.Facing.BACK);
    //or
    //rtmpCamera1.startPreview(CameraHelper.Facing.FRONT);
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && rtmpCamera1.isRecording()) {
      rtmpCamera1.stopRecord();
      bRecord.setText(R.string.start_record);
      Toast.makeText(this,
          "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
          Toast.LENGTH_SHORT).show();
      currentDateAndTime = "";
    }
    if (rtmpCamera1.isStreaming()) {
      rtmpCamera1.stopStream();
      bStartStop.setText(getResources().getString(R.string.start_button));
    }
    rtmpCamera1.stopPreview();
  }

  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    int action = motionEvent.getAction();
    if (motionEvent.getPointerCount() > 1) {
      if (action == MotionEvent.ACTION_MOVE) {
        rtmpCamera1.setZoom(motionEvent);
      }
    } else {
      if (action == MotionEvent.ACTION_UP) {
        // todo place to add autofocus functional.
      }
    }
    return true;
  }

  @Override
  public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
      if(requestCode==CAMERA_REQUEST_CODE){
        finish();
      }
  }

  @Override
  public void onRationaleAccepted(int requestCode) {

  }

  @Override
  public void onRationaleDenied(int requestCode) {

  }
}
