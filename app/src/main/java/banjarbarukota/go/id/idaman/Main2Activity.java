package banjarbarukota.go.id.idaman;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.MenuDepan;
import banjarbarukota.go.id.idaman.Adapter.MenuDepanAdapter;
import banjarbarukota.go.id.idaman.Adapter.ViewPagerAdapter;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.ItemClickSupport;
import banjarbarukota.go.id.idaman.Utility.NotifService;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import banjarbarukota.go.id.idaman.Utility.ServerCheck;
import pub.devrel.easypermissions.EasyPermissions;


import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AbsListView.OnScrollListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks  {
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    ArrayList<String> images = new ArrayList<>();
    TextView tvUsername,tvLevel;
    ImageView userNavImage;
    ViewPagerAdapter viewPagerAdapter;

    public List<MenuDepan> menuList = new ArrayList<>();
    private RecyclerView menuRecyclerView;
    private MenuDepanAdapter mAdapter;

    static MenuKategoriActivity instance;

    ArrayList<Boolean> isUp;

    NestedScrollView am2;

    private SwipeRefreshLayout srl1;

    private Integer LIVE_REQUEST = 12;

    private boolean doubleBackToExitPressedOnce = false;

    OkHttpGetHandler getHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_depan2);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_idaman_logo);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //View navheadview = navigationView.inflateHeaderView(R.layout.nav_header_main);
        tvUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navUsername);
        tvLevel = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navUserLevel);
        userNavImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.userImageView);


        if(((GlobalClass) getApplication()).getStringPref("nama_tampilan").equals("null")){
            tvUsername.setText(((GlobalClass) getApplication()).getStringPref("nama_lengkap"));
        }else{
            tvUsername.setText(((GlobalClass) getApplication()).getStringPref("nama_tampilan"));
        }
        tvLevel.setText(((GlobalClass) getApplication()).getStringPref("cms_privileges_name"));
        // Log.d("fbook",AccessToken.getCurrentAccessToken().getUserId());

        //new DownloadImageFromInternet(userNavImage).execute(((GlobalClass) getApplication()).getStringPref("foto"));
        if(((GlobalClass) getApplication()).getStringPref("login_pakai").equals("nik") & !((GlobalClass) getApplication()).getStringPref("foto").equals("")) {
            Log.d("Muka",((GlobalClass) getApplication()).getStringPref("foto"));
            Picasso.get()
                    .load( ((GlobalClass) getApplication()).getStringPref("foto"))
                    .transform(new CircleTransform())
                    .into(userNavImage);
        }

        userNavImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(),"pop", Toast.LENGTH_LONG).show();
                if(((GlobalClass) getApplication()).getStringPref("login_pakai").equals("nik")) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                    Intent profilintent = new Intent(Main2Activity.this, ProfilUserActivity.class);
                    profilintent.putExtra("user_id", ((GlobalClass) getApplication()).getStringPref("user_id"));
                    startActivityForResult(profilintent, 333);
                }else{
                    Toast.makeText(Main2Activity.this,"Silahkan Logout dan Login Kembali Menggunankan NIK",Toast.LENGTH_LONG).show();
                }
            }
        });


        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("B Buzz"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        am2 = (NestedScrollView)findViewById(R.id.nsvMain2);

        viewPager = (ViewPager) findViewById(R.id.vpImage);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        viewPagerAdapter = new ViewPagerAdapter(this,images);

        ServerCheck sc = new ServerCheck();

        //if(sc.isServerAvailable(this.viewPager)) {
            getSlider();
       // }

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.noactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        if(images.size()>0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        }else{
           // viewPager.setVisibility(View.GONE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.noactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        menuRecyclerView = (RecyclerView) findViewById(R.id.rvMenuUtama);

        mAdapter = new MenuDepanAdapter(menuList,getApplicationContext());
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(Main2Activity.this,3);
            menuRecyclerView.setLayoutManager(mLayoutManager);
            menuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        menuRecyclerView.setAdapter(mAdapter);

       // menuRecyclerView.addItemDecoration(new DividerItemDecoration(menuRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        //if(sc.isServerAvailable(this.viewPager)) {
            prepareMenuData("1");
        //}
        menuRecyclerView.setNestedScrollingEnabled(false);
        menuRecyclerView.setFocusable(false);



        this.srl1 = ((SwipeRefreshLayout)findViewById(R.id.swiperefresh));
        this.srl1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            public void onRefresh()
            {
                Main2Activity.this.srl1.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        getSlider();
                        prepareMenuData("1");
                        Main2Activity.this.srl1.setRefreshing(false);
                        Main2Activity.this.srl1.invalidate();
                    }
                }, 2000L);
            }
        });

        //this.srl1.setEnabled(false);

        //am2.fullScroll(View.FOCUS_UP);

        am2.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    srl1.setEnabled(scrollX==0);
            }
        });


        //periksa notif service
        if (!isMyServiceRunning(NotifService.class)) {
            Intent notifService = new Intent(this, NotifService.class);
            startService(notifService);
        }
    }

    public void getSlider() {
        getHandler = new OkHttpGetHandler();
        try {
            JSONObject jobj =  getHandler.getSlider();
            if(jobj!=null) {
                String success = jobj.getString("api_status");
                if (success.equals("1")) {
                    images.clear();
                    JSONArray dataar = jobj.getJSONArray("data");
                    if (dataar.length() > 0) {
                        for (int i = 0; i < dataar.length(); i++) {
                            JSONObject jdata = dataar.getJSONObject(i);

                            images.add(jdata.getString("photo"));

                        }

                        viewPagerAdapter.notifyDataSetChanged();
                    }

                } else {
                    Log.d("sukses", "tidak");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareMenuData(String utama) {
        isUp = new ArrayList<>();

        OkHttpGetHandler okget = new OkHttpGetHandler();

        try {
            JSONObject jobj = okget.getListMenu(utama);

            if(jobj.getJSONArray("data").length()>0){
                Log.d("Muka",jobj.toString());
                JSONArray dataArray = (JSONArray) jobj.getJSONArray("data");

                ArrayList<ContentValues> anak;

                if (dataArray.length() > 0) {
                    menuList.clear();
                    for (int d = 0; d < dataArray.length(); d++) {
                        JSONObject jdata = dataArray.getJSONObject(d);
                        //Log.d("menudep", jdata.getString("nama") + " - "  + jdata.getString("showText"));
                        //Log.d("menudepan", jdata.toString());
                        JSONArray jsonArray = (JSONArray) jdata.getJSONArray("ortunya");
                        anak = new ArrayList<ContentValues>();
                        ContentValues values;
                        if (jsonArray.length() > 0) {
                            anak = new ArrayList<ContentValues>();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                values = new ContentValues();
                                JSONObject adata = jsonArray.getJSONObject(j);

                                Iterator<String> keys = adata.keys();
                                while (keys.hasNext()) {
                                    String key = (String) keys.next();
                                    String value = adata.get(key).toString();
                                    values.put(key, value);
                                }
                                anak.add(values);
                            }
                            if (anak.size() > 0) {
                                // onMember(data);

                            }
                        }

                        MenuDepan menuDepan = new MenuDepan(jdata.getString("id"), jdata.getString("nama"), jdata.getString("icon"), anak, jdata.getString("show_text"), jdata.getString("aksi"));
                        menuList.add(menuDepan);
                        isUp.add(false);
                        mAdapter.notifyDataSetChanged();
                    }
                    am2.scrollTo(0, 0);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ItemClickSupport.addTo(menuRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("kiliklik",menuList.get(position).getAksi());
                        String[] aksis = menuList.get(position).getAksi().split(";");
                        String c = "banjarbarukota.go.id.idaman.Utility.Jumper";
                        String methodName = aksis[0];
                        Method method = null;
                        try {
                            Class<?> dc = Class.forName(c); // convert string classname to class
                            Object dog = dc.newInstance(); // invoke empty constructor
                            if(aksis.length==2) {
                                Class<?>[] paramTypes = {Context.class, String.class};
                                Method printDogMethod = dog.getClass().getMethod(methodName, paramTypes);
                                printDogMethod.invoke(dog, getApplicationContext(), aksis[1]);
                            }else if(aksis.length==1){
                                Class<?>[] paramTypes = {Context.class};
                                Method printDogMethod = dog.getClass().getMethod(methodName, paramTypes);
                                printDogMethod.invoke(dog, getApplicationContext());
                            }else if(aksis.length==3){
                                Class<?>[] paramTypes = {Context.class, String.class, String.class};
                                Method printDogMethod = dog.getClass().getMethod(methodName, paramTypes);
                                printDogMethod.invoke(dog, getApplicationContext(), aksis[1], aksis[2]);
                            }

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        //jangan hapus
//                        View vitem = recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.anak_recycler_view);
//                        if (isUp.get(position)) {
//                            slideDown(vitem);
//                        } else {
//                            slideUp(vitem);
//                        }
//                        isUp.set(position,!isUp.get(position));
                    }

                }
        );
    }



    // slide the view from below itself to the current position
    public void slideUp(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    private void Logout() {
        // dm.updateSetup("", "", "", "", "", "", "", "", (long) 1);
        Intent i = new Intent(Main2Activity.this, MainLoginActivity.class);
        i.putExtra("state","logout");
        startActivity(i);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_About) {
            Intent i = new Intent(this, TentangActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_list_chat) {
            if(((GlobalClass) getApplicationContext()).getStringPref("login_pakai").equals("nik")){
                Intent i = new Intent(this, PilihChatActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(), "Silahkan Logout dan Login kembali menggunakan NIK anda.", Toast.LENGTH_LONG).show();
            }
        }else if (id == R.id.nav_list_bbuzz) {
            Intent i = new Intent(this, NotifikasiActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_setting) {
            Intent i = new Intent(this, PengaturanActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            Logout();

        }else if (id == R.id.nav_scan) {
           //startScanner();
        }
//        else if (id == R.id.nav_live) {
//            String[] perms = { "android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.RECORD_AUDIO"};
//            if (EasyPermissions.hasPermissions(this, perms)) {
//                Intent inlive = new Intent(this, RtmpActivity.class);
//                startActivity(inlive);
//            }else{
//                EasyPermissions.requestPermissions(this, "Meminta Ijin Akses", LIVE_REQUEST, perms);
//            }
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Sentuh kembali untuk keluar", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        srl1.setEnabled(i == 0);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == LIVE_REQUEST){
            Intent inlive = new Intent(this, RtmpActivity.class);
            startActivity(inlive);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }


}

