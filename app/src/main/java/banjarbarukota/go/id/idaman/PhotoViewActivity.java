package banjarbarukota.go.id.idaman;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        final PhotoView photoView = findViewById(R.id.photo_view);

        Intent i = getIntent();
        String photo_url = i.getStringExtra("photo_url");

        Picasso.get()
                .load(photo_url)
                .into(photoView);
    }
}
