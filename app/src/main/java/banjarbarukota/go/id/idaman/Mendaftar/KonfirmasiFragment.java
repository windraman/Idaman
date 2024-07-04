package banjarbarukota.go.id.idaman.Mendaftar;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import banjarbarukota.go.id.idaman.R;

/**
 * Created by Wahyu on 5/7/2018.
 */

public class KonfirmasiFragment extends Fragment {
    // Store instance variables
    private static String title;
    private int page;
    private static Activity thus;
    private static TextView tvTanya;
    Button btnBenar;
    Button btnUlangi;
    private static TextView tvKNIK, tvKNama, tvKTL, tvKPassword;

    // newInstance constructor for creating fragment with arguments
    public static KonfirmasiFragment newInstance(int page, String title) {
        KonfirmasiFragment fragmentKonfirmasi = new KonfirmasiFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentKonfirmasi.setArguments(args);
        return fragmentKonfirmasi;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thus = getActivity();

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_konfirmasi, container, false);

        btnBenar = (Button) view.findViewById(R.id.btnBenar);
        btnUlangi = (Button) view.findViewById(R.id.btnUlangi);

        tvKNIK = view.findViewById(R.id.tvKNIK);
        tvKNama = view.findViewById(R.id.tvKNama);
        tvKTL = view.findViewById(R.id.tvKTL);
        tvKPassword = view.findViewById(R.id.tvKPassword);


        btnUlangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaftarActivity.gotoPosition(0);
            }
        });

        btnBenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int success = DaftarActivity.tambahPengguna();
                if(success==1){
                    Toast.makeText(thus, "Berhasil Mendaftar, Silahkan Login Kembali.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else if(success==2){
                    Toast.makeText(thus, "NIK sudah terdaftar.", Toast.LENGTH_SHORT).show();
                    DaftarActivity.gotoPosition(0);
                }else if(success==0){
                    Toast.makeText(thus, "Gagal Mendaftar, Silahkan Coba Lagi.", Toast.LENGTH_SHORT).show();
                };

            }
        });


        return view;
    }

    public static void isiKonfirmasi(){
        tvKNIK.setText(DaftarActivity.NIK);
        tvKNama.setText(DaftarActivity.NAMA);
        tvKTL.setText(DaftarActivity.TEMPAT_LAHIR);
        tvKPassword.setText(DaftarActivity.PASSWORD);
    }
    
}