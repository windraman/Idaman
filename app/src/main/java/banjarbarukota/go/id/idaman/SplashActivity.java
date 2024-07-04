package banjarbarukota.go.id.idaman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Utility.ServerCheck;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.SHOW_SPLASH;

public class SplashActivity extends Activity {
	
	protected int _splashTime = 600;
	
	private Thread splashTread;
	
	//DatabaseManager dm;
	
	ArrayList<Object> setup;
	int logged;

	View contextView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

//		if(!SHOW_SPLASH){
//			Intent i = new Intent();
//			i.setClass(SplashActivity.this, Main2Activity.class);
//			startActivity(i);
//			finish();
//		}
	    
//	    dm = new DatabaseManager(getBaseContext());
//	    dm.cekSetupReady();
//
//	    setup = dm.ambilSetup();
	    
 	     //Intent intent = new Intent(SplashActivity.this, AndroidNotificationsExampleActivity.class);
	     //startService(intent);
	     
	     //Toast.makeText(getBaseContext(), "Start", Toast.LENGTH_LONG).show();
	  //Remove title bar

	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	   this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);

		contextView = findViewById(R.id.SplashLayout);
		logged = 0;
//
//		if(((GlobalClass) getApplication()).getStringPref("user_id").length()>0){
//			//logged =1;
//		}

		//ServerCheck sc = new ServerCheck();

	    
	    final SplashActivity sPlashScreen = this;


		//if(sc.isServerAvailable(contextView)) {
			// thread for displaying the SplashScreen
			splashTread = new Thread() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					try {
						synchronized (this) {
							wait(_splashTime);
						}

					} catch (InterruptedException e) {
					} finally {

						if (logged == 0) {
							Intent i = new Intent();
							i.setClass(sPlashScreen, MainLoginActivity.class);
							i.putExtra("state", "");
							startActivity(i);
						} else {
							Intent i = new Intent();
							i.setClass(sPlashScreen, Main2Activity.class);
							startActivity(i);
						}

						finish();

						//this.stop();
					}
				}
			};

			splashTread.start();
		//}

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	synchronized(splashTread){
	    		splashTread.notifyAll();
	    	}
	    }
	    return true;
	}
	
}
