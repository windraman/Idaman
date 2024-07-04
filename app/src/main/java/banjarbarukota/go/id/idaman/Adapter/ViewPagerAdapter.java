package banjarbarukota.go.id.idaman.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.idaman.MediaPlayerActivity;
import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.WebViewActivity;

import static banjarbarukota.go.id.idaman.LokasiWizardActivity.getAppContext;
//import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;


public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageUrls;
    LayoutInflater inflater;


    public ViewPagerAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @SuppressLint("JavascriptInterface")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {

//        inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_slider, view,
                false);

        assert itemView != null;
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imgItem);
        final VideoView videoView = (VideoView) itemView.findViewById(R.id.videoItem);
        final ImageButton btnPLay = (ImageButton) itemView.findViewById(R.id.imbPlayItem);
        final ConstraintLayout lVideoItem = (ConstraintLayout) itemView.findViewById(R.id.lVideoItem);
        WebView wvItem = new WebView(context);
        wvItem = (WebView) itemView.findViewById(R.id.wvItem);

//        ImageView imageView = new ImageView(context);
//        VideoView videoView = new VideoView(context);
 //       Object obj = new Object();

            if (getFileMimeType(imageUrls.get(position)).contains("image")) {
                Picasso.get()
                        .load(imageUrls.get(position))
                        .fit()
                        .centerCrop()
                        .into(imageView);

               imageView.setImageResource(R.mipmap.bg);

                imageView.setVisibility(View.VISIBLE);
                lVideoItem.setVisibility(View.GONE);
                wvItem.setVisibility(View.GONE);

                view.addView(itemView,0);
            } else if (getFileMimeType(imageUrls.get(position)).contains("video")) {
                Log.d("videoadapter", imageUrls.get(position));
                String url = imageUrls.get(position);
                Uri video = Uri.parse(url);
                String fileName=url.substring(url.lastIndexOf("/")+1);
//                MediaController mc = new MediaController(context);
//                videoView.setMediaController(mc);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        videoView.setVideoURI(video);
//                    }
//                });
                //videoView.setVideoURI(video);
                lVideoItem.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                wvItem.setVisibility(View.VISIBLE);
                //videoView.start();
//                btnPLay.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent iplay = new Intent(context, MediaPlayerActivity.class);
//                        iplay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        iplay.putExtra("url",url);
//                       // iplay.putExtra("media_name",url);
//                        context.startActivity(iplay);
//                    }
//                });
//                wvItem = new WebView(context);
//                wvItem = (WebView) findViewById(R.id.mWV);

                WebSettings s = wvItem.getSettings();
                s.setJavaScriptEnabled(true);
                s.setLoadWithOverviewMode(true);
                s.setAllowFileAccess(true);
                wvItem.setWebViewClient (new WebViewClient());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    s.setAllowUniversalAccessFromFileURLs(true);
                    s.setAllowFileAccessFromFileURLs(true);
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    wvItem.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }
                else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
                    wvItem.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }

                wvItem.addJavascriptInterface(this, "Android");
                wvItem.loadUrl("https://wahyu.top?" +url+"&live&android");
                //wvItem.loadUrl("https://wahyu.top?balaikota&balai&bjb");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
                    { wvItem.setWebContentsDebuggingEnabled(true); }
                }

                view.addView(itemView,0);
            }

        //Log.d("pat", imageUrls.get(position));
        return itemView;
    }

//    public void startMediaPlayer(String url) {
//        Intent intent = new Intent();
//        intent.setAction(MediaPlayerService.BROADCAST_TO_SERVICE);
//        intent.putExtra(MediaPlayerService.PLAYER_FUNCTION_TYPE, MediaPlayerService.PLAY_MEDIA_PLAYER);
//        intent.putExtra(MediaPlayerService.PLAYER_TRACK_URL, url);
//        sendBroadcast(intent);
//    }

    public String getFileMimeType(String url){
        Uri uri = Uri.parse(url);
        String mimeType = "none";
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getAppContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            if (fileExtension.equals("flv")) {
                mimeType = "video/flv";
            }else {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
        }


        return mimeType;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public class myWebViewClient extends WebViewClient {
        ProgressDialog progressDialog;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("mailto:")) {
                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                // Here we can open new activity
                return true;
            }else {
                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Then show progress  Dialog
            // in standard case YourActivity.this
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }
        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            try {
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
//                if (view.getUrl().equals("https://siapkk.banjarbarukota.go.id/admin"))
//                {
//                    myWebView.clearHistory();
//                    Log.d("Bersih","yes");
//                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
