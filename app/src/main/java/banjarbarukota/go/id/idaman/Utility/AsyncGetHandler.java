package banjarbarukota.go.id.idaman.Utility;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.MAIN_SERVER;

public class AsyncGetHandler extends AsyncTask<String, Void, JSONObject> {

    OkHttpClient client = new OkHttpClient
            .Builder()
            .addInterceptor(new LoggingInterceptor())
            .build();

    long unixTime = System.currentTimeMillis() / 1000L;

    @Override
    protected JSONObject doInBackground(String... params) {

        String stringUrl = params[0];
        String token = params[1];
        String time = params[2];
        JSONObject result = null;
        Request request = new Request.Builder()
                .url(stringUrl)
                .addHeader("X-Authorization-Token", token)
                .addHeader("X-Authorization-Time",time)
                .build();

        //Log.d("Muka",token + " for ur : " + stringUrl);
        //if(isServerAvailable()) {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String jsondata = response.body().string();

                result = new JSONObject(jsondata);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }else{
//            Log.d("server","gagal");
//        }

        return result;
    }

        @Override
    protected void onPostExecute(JSONObject result){
        super.onPostExecute(result);
    }

//    public JSONObject noAsync(String url){
//        OkHttpClient client = new OkHttpClient();
//        JSONObject res = null;
//        Request request = new Request.Builder()
//        .url(url)
//        .build();
//
//        if(isServerAvailable()) {
//            try (Response response = client.newCall(request).execute()) {
//                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//                String jsondata = response.body().string();
//
//                res = new JSONObject(jsondata);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else{
//            Log.d("server","gagal");
//        }

       // return res;
   // }
       class LoggingInterceptor implements Interceptor {
           @Override public Response intercept(Interceptor.Chain chain) throws IOException {
               Request request = chain.request();

               Request compressedRequest = request.newBuilder()
                       .header("User-Agent", System.getProperty("http.agent"))
                       .build();

               return chain.proceed(compressedRequest);
           }
       }
}


