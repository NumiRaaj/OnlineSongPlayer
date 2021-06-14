package com.codeempire.jetplayer.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.codeempire.jetplayer.R;
import com.noman.ads.AdConfig;
import com.noman.ads.AppData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    public static String adType = "1";
    String adBanner = "", adInterstital = "", adNative = "";
    String apiUrl = "https://mavrixtrends.com/numiraaj/AndroidAds/api/getAppsData.php";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        setContentView(R.layout.activity_splash);
    /*    new Handler().postDelayed(new Runnable() {
            public void run() {
                nextActivity();
            }
        }, 1500);
*/

        JsonTask myAsyncTasks = new JsonTask();
        myAsyncTasks.execute(apiUrl);

    }

    public boolean checkPermission(String str) {
        return ContextCompat.checkSelfPermission(this, str) == 0;
    }


    public void nextActivity() {

        if (Build.VERSION.SDK_INT < 23) {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (!checkPermission("android.permission.READ_EXTERNAL_STORAGE") || !checkPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
        } else {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void initiateAdData() {
        //Initiate Add data
        AppData appData = new AppData(adBanner,
                adInterstital,
                adNative,
                adBanner,
                adInterstital,
                adNative);
        AdConfig.getAppData = appData;
        nextActivity();
    }

    class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.e("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                initiateAdData();
                return;
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject objects = array.getJSONObject(i);

                    String appName = objects.getString("appname");
                    if (appName.equals("Video Player")) {
                        if (objects.getString("isAdmob").equals("1")) {
                            adBanner = objects.getString("admobBanner");
                            adInterstital = objects.getString("admobInterstital");
                            adNative = objects.getString("admobNative");
                            adType = "1";
                        } else {
                            adBanner = objects.getString("fbBanner");
                            adInterstital = objects.getString("fbInterstital");
                            adNative = objects.getString("fbNative");
                            adType = "2";
                        }
                    }


                }


                initiateAdData();


            } catch (JSONException e) {
                e.printStackTrace();
                initiateAdData();
            }


        }


    }


}
