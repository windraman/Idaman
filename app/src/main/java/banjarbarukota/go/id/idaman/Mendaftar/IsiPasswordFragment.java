package banjarbarukota.go.id.idaman.Mendaftar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import banjarbarukota.go.id.idaman.R;

/**
 * Created by Wahyu on 5/7/2018.
 */

public class IsiPasswordFragment extends Fragment {
    // Store instance variables
    private static String title;
    private int page;
    private static Activity thus;
    private static EditText etPassword;
    private static EditText etPassword2;

    Button btnLanjut;

    // newInstance constructor for creating fragment with arguments
    public static IsiPasswordFragment newInstance(int page, String title) {
        IsiPasswordFragment fragmentPassword = new IsiPasswordFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentPassword.setArguments(args);
        return fragmentPassword;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        thus = getActivity();


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_isi_password, container, false);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etPassword2 = (EditText) view.findViewById(R.id.etPassword2);
        btnLanjut = (Button) view.findViewById(R.id.btnLanjut);

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifPassword();
            }
        });


        return view;
    }

    public static void verifPassword(){
        if(etPassword.getText().length()>0) {
            if(etPassword.getText().toString().equals(etPassword2.getText().toString())){
                DaftarActivity.PASSWORD = etPassword.getText().toString();
                DaftarActivity.gotoPosition(4);
                HideSoftKeyboard();
            }else{
                Toast.makeText(thus, "Password Tidak Sama !", Toast.LENGTH_SHORT).show();
                DaftarActivity.gotoPosition(3);
            }
        }else{
            Toast.makeText(thus, "Isi Password Anda !", Toast.LENGTH_SHORT).show();
            DaftarActivity.gotoPosition(3);
        }
    }


    public static void HideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                thus.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(thus.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}