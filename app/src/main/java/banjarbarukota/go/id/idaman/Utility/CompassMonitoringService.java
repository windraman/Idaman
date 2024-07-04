package banjarbarukota.go.id.idaman.Utility;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CompassMonitoringService extends Service implements SensorEventListener {

    private static final String TAG = CompassMonitoringService.class.getSimpleName();
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    public static final String ACTION_COMPASS_BROADCAST = CompassMonitoringService.class.getName() + "CompassBroadcast";
    public static final String EXTRA_DEGREE = "extra_degree";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        return  START_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "Compass changed");
        float degree = Math.round(event.values[0]);

        if (event != null) {
            Log.d(TAG, "== location != null");
            //Send result to activities
            sendMessageToUI(String.valueOf(event.values[0]));
        }
        currentDegree = -degree;
        // get the angle around the z-axis rotated


       // tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)
//        RotateAnimation ra = new RotateAnimation(
//                currentDegree,
//                -degree,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f);
//
//        // how long the animation will take place
//        ra.setDuration(210);
//
//        // set the animation after the end of the reservation status
//        ra.setFillAfter(true);
//
//        // Start the animation
//        image.startAnimation(ra);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    private void sendMessageToUI(String degree) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_COMPASS_BROADCAST);
        intent.putExtra(EXTRA_DEGREE, degree);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
