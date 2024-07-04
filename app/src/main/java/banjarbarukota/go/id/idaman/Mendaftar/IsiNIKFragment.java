package banjarbarukota.go.id.idaman.Mendaftar;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import banjarbarukota.go.id.idaman.R;

/**
 * Created by Wahyu on 5/7/2018.
 */

public class IsiNIKFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    Button btnLanjut;

    // newInstance constructor for creating fragment with arguments
    public static IsiNIKFragment newInstance(int page, String title) {
        IsiNIKFragment fragmentFirst = new IsiNIKFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_isi_nik, container, false);
        final EditText etNIK = (EditText) view.findViewById(R.id.etNIK);
        etNIK.setText(title);

        etNIK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DaftarActivity.NIK = etNIK.getText().toString();
            }
        });

        btnLanjut = (Button) view.findViewById(R.id.btnLanjut);

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaftarActivity.gotoPosition(1);
            }
        });

        return view;
    }

    public void HideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}