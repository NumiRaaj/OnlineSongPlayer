package tcking.github.com.giraffeplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by tcking on 15/10/27.
 */
public class GiraffePlayerActivity extends AppCompatActivity {

    GiraffePlayer player;
    static String EXTRA_IS_FLOATING_VIDEO = "isFromFloating";

    public static Intent getInstance(Context context, boolean isFromFloating) {
        Intent intent = new Intent(context, GiraffePlayerActivity.class);
        intent.putExtra(GiraffePlayerActivity.EXTRA_IS_FLOATING_VIDEO, isFromFloating);
        return intent;
    }

    public static Intent getIntent(Context context, List<SongModel> arrayList, int postion, int seekBarPostion) {
        Intent intent = new Intent(context, GiraffePlayerActivity.class);
        intent.putExtra(GiraffePlayerActivity.EXTRA_IS_FLOATING_VIDEO, true);

        SharedPref.setVideoList(arrayList, context);
        SharedPref.save(context, SharedPref.PREF_CURRENT_PLAY_INDEX, postion);
        SharedPref.save(context, SharedPref.PREF_CURRENT_SEEK_POSITION, seekBarPostion);
        return intent;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.giraffe_player);


        boolean isFromFloating = false;
        if (getIntent() != null) {
            isFromFloating = getIntent().getBooleanExtra(EXTRA_IS_FLOATING_VIDEO, false);
        }


        player = new GiraffePlayer(this);
        Config config = getIntent().getParcelableExtra("config");
        if (config == null || TextUtils.isEmpty(config.url)) {
            if (isFromFloating) {

                setVideoPlayerAfterFloating();
                //  player.setTitle();
            } else {
                Toast.makeText(this, R.string.giraffe_player_url_empty, Toast.LENGTH_SHORT).show();
            }
        } else {
            player.setTitle(config.title);
            player.setDefaultRetryTime(config.defaultRetryTime);
            player.setFullScreenOnly(config.fullScreenOnly);

            player.setScaleType(TextUtils.isEmpty(config.scaleType) ? GiraffePlayer.SCALETYPE_FITPARENT : config.scaleType);
            player.setTitle(TextUtils.isEmpty(config.title) ? "" : config.title);
            player.setShowNavIcon(config.showNavIcon);
            player.play(config.url);


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    /**
     * play video
     *
     * @param context
     * @param url     url,title
     */
    public static void play(Activity context, String... url) {
        Intent intent = new Intent(context, GiraffePlayerActivity.class);
        intent.putExtra("url", url[0]);
        if (url.length > 1) {
            intent.putExtra("title", url[1]);
        }
        context.startActivity(intent);
    }

    public void setVideoPlayerAfterFloating() {
        player.setFullScreenOnly(false);
        player.playListPlayer(SharedPref.getVideoList(this),
                SharedPref.getInt(this, SharedPref.PREF_CURRENT_PLAY_INDEX));
        player.seekTo(SharedPref.getInt(this, SharedPref.PREF_CURRENT_SEEK_POSITION), true);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            //  player.onPause();
        }
    }

    public static Config configPlayer(Activity activity) {
        return new Config(activity);
    }

    public static class Config implements Parcelable {

        private Activity activity;
        private String scaleType;
        private boolean fullScreenOnly;
        private long defaultRetryTime = 5 * 1000;
        private String title;
        private String url;
        private boolean showNavIcon = true;

        private static boolean debug = true;

        public Config debug(boolean debug) {
            Config.debug = debug;
            return this;
        }

        public static boolean isDebug() {
            return debug;
        }

        public Config setTitle(String title) {
            this.title = title;
            return this;
        }


        public Config(Activity activity) {
            this.activity = activity;
        }

        public void play(String url) {
            this.url = url;
            Intent intent = new Intent(activity, GiraffePlayerActivity.class);
            intent.putExtra("config", this);
            activity.startActivity(intent);
        }

        public Config setDefaultRetryTime(long defaultRetryTime) {
            this.defaultRetryTime = defaultRetryTime;
            return this;
        }

        public Config setScaleType(String scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public Config setFullScreenOnly(boolean fullScreenOnly) {
            this.fullScreenOnly = fullScreenOnly;
            return this;
        }

        private Config(Parcel in) {
            scaleType = in.readString();
            fullScreenOnly = in.readByte() != 0;
            defaultRetryTime = in.readLong();
            title = in.readString();
            url = in.readString();
            showNavIcon = in.readByte() != 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(scaleType);
            dest.writeByte((byte) (fullScreenOnly ? 1 : 0));
            dest.writeLong(defaultRetryTime);
            dest.writeString(title);
            dest.writeString(url);
            dest.writeByte((byte) (showNavIcon ? 1 : 0));
        }

        public static final Parcelable.Creator<Config> CREATOR = new Parcelable.Creator<Config>() {
            public Config createFromParcel(Parcel in) {
                return new Config(in);
            }

            public Config[] newArray(int size) {
                return new Config[size];
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
