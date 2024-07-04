package banjarbarukota.go.id.idaman.Mendaftar;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import banjarbarukota.go.id.idaman.R;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DaftarActivity extends AppCompatActivity {
    // ...

    FragmentPagerAdapter adapterViewPager;

    public static String NIK;
    public static String NAMA;
    public static String TEMPAT_LAHIR;
    public static String PASSWORD;
    public static ArrayList<String> NAMA_ITEMS;
    public static ArrayList<String> TL_ITEMS;
    public static ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_daftar);
        vpPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(vpPager, true);



        NIK = "";

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if(position==1 && NIK.length()>0){
//                    Toast.makeText(DaftarActivity.this, "hasil untuk " + NIK + " = " +
//                            ambilNama(NIK).toString(), Toast.LENGTH_SHORT).show();
                    NAMA_ITEMS = ambilNama(NIK);
                    PilihNamaFragment.loadListNama();
                    HideSoftKeyboard();
                }else  if(position==2){
//                    Toast.makeText(DaftarActivity.this, "hasil untuk " + NIK + " = " +
//                            ambilTL(NIK).toString(), Toast.LENGTH_SHORT).show();
                    TL_ITEMS = ambilTL(NIK);
                    PilihTLFragment.loadListTL();
                    HideSoftKeyboard();
                }else  if(position==3){
                    //IsiPasswordFragment.verifPassword();
                }else  if(position==4){
                    KonfirmasiFragment.isiKonfirmasi();
                }else  if(position==1 && NIK.length()==0){
                    Toast.makeText(DaftarActivity.this, "Isi NIK anda", Toast.LENGTH_SHORT).show();
                    vpPager.setCurrentItem(0);
                }

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }



    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return IsiNIKFragment.newInstance(0, "");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return PilihNamaFragment.newInstance(1, "Yang Mana Nama Anda ?");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return PilihTLFragment.newInstance(2, "Yang Mana Tempat Lahir Anda ?");
                case 3:
                    return IsiPasswordFragment.newInstance(3, "");
                case 4:
                    return KonfirmasiFragment.newInstance(4, "");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Page " + position;
//        }

    }

    public static void gotoPosition(int position){
        vpPager.setCurrentItem(position);
    }

    public static ArrayList<String> ambilNama(String NIK){
        ArrayList<String> items = new ArrayList<String>();
        JSONArray names = null;

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nik", NIK)
                .build();

        Request request = new Request.Builder()
                //.url("http://kayapa.banjarbarukota.go.id/aset/get_name.php")
                .url("http://siapkk.banjarbarukota.go.id/api/pilih_nama")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String hasil = response.body().string();
            JSONObject jobj = new JSONObject(hasil);
            names = jobj.getJSONArray("nama_lgkp");

            for (int i=0; i < names.length(); i++) {
               items.add(names.getJSONObject(i).getString("nama"));
            }

            String success = jobj.getString("success");
            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  items;
    }

    public static Integer tambahPengguna(){
        Integer success = 0;

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nip", NIK)
                .addFormDataPart("password", PASSWORD)
                .addFormDataPart("email", "")
                .addFormDataPart("nama_lengkap", NAMA)
                .addFormDataPart("tmpt_lhr", TEMPAT_LAHIR)
                .addFormDataPart("tgl_lhr", "")
                .addFormDataPart("jk", "")
                .addFormDataPart("alamat", "")
                .build();

        Request request = new Request.Builder()
                //.url("http://kayapa.banjarbarukota.go.id/aset/add_user.php")
                .url("http://10.20.30.110:81/idaman/aset/add_user.php")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String hasil = response.body().string();
            JSONObject jobj = new JSONObject(hasil);

            success = Integer.parseInt(jobj.getString("success"));
            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  success;
    }

    public static ArrayList<String> ambilTL(String NIK){
        ArrayList<String> items = new ArrayList<String>();
        JSONArray tls = null;

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nik", NIK)
                .build();

        Request request = new Request.Builder()
                .url("http://siapkk.banjarbarukota.go.id/api/pilih_alamat")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String hasil = response.body().string();
            JSONObject jobj = new JSONObject(hasil);
            tls = jobj.getJSONArray("alamat");

            for (int i=0; i < tls.length(); i++) {
                items.add(tls.getJSONObject(i).getString("alamat"));
            }

            String success = jobj.getString("success");
            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  items;
    }

    public void HideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
