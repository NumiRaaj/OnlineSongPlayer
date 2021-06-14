package tcking.github.com.giraffeplayer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class FloatingWidgetService extends Service {
    public static final String EXTRA_VIDEO_POSITION = "video_position";
    public List<SongModel> videoList;
    public int videoPosition, seekbarPosition;
    ImageView closeWindow;
    View floatingView;
    ImageView floatingWindow;
    ImageView next;
    WindowManager.LayoutParams params;
    ImageView playPause;
    ImageView previous;
    SongModel video;
    IjkVideoView videoView;
    WindowManager windowManager;
    LinearLayout mp3View;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
//        this.videoPosition = intent.getIntExtra(EXTRA_VIDEO_POSITION, 0);
        return super.onStartCommand(intent, i, i2);
    }

    public void onCreate() {
        super.onCreate();
        this.floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, (ViewGroup) null);
        this.videoPosition = SharedPref.getInt(getApplicationContext(), SharedPref.PREF_CURRENT_PLAY_INDEX);
        this.videoList = SharedPref.getVideoList(getApplicationContext());//VideoPlayerManager.getVideoList();
        this.video = videoList.get(videoPosition);
        this.seekbarPosition = SharedPref.getInt(getApplicationContext(), SharedPref.PREF_CURRENT_SEEK_POSITION);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, Build.VERSION.SDK_INT >= 26 ? 2038 : 2002, 8, -3);
        this.params = layoutParams;
        layoutParams.gravity = 51;
        this.params.x = 0;
        this.params.y = 100;
        @SuppressLint("WrongConstant")
        WindowManager windowManager2 = (WindowManager) getSystemService("window");
        this.windowManager = windowManager2;
        windowManager2.addView(this.floatingView, this.params);
        this.playPause = (ImageView) this.floatingView.findViewById(R.id.playPause);
        this.closeWindow = (ImageView) this.floatingView.findViewById(R.id.closeWindow);
        this.floatingWindow = (ImageView) this.floatingView.findViewById(R.id.floatingWindow);
        this.next = (ImageView) this.floatingView.findViewById(R.id.next);
        this.previous = (ImageView) this.floatingView.findViewById(R.id.previous);
        IjkVideoView videoView2 = (IjkVideoView) this.floatingView.findViewById(R.id.videoView);
        this.mp3View = (LinearLayout) this.floatingView.findViewById(R.id.view_mp3);


        this.videoView = videoView2;
        SongModel media_Data = this.video;
        if (media_Data != null) {
            videoView2.setVideoPath(media_Data.getDATA());
            this.videoView.seekTo(seekbarPosition); //setseek poostion
            this.videoView.start();
        }

        if (media_Data.getIsMp3()) {
            mp3View.setVisibility(View.VISIBLE);
        } else {
            mp3View.setVisibility(View.GONE);
        }

        videoView2.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (videoPosition < videoList.size() - 1) {
                    videoPosition++;
                    setSongsCurrentValues();
                    videoView.setVideoPath(videoList.get(videoPosition).getDATA());
                    videoView.start();

                    if (videoList.get(videoPosition).getIsMp3()) {
                        mp3View.setVisibility(View.VISIBLE);
                    } else {
                        mp3View.setVisibility(View.GONE);
                    }

                    return;
                }
                videoView.pause();
                playPause.setImageResource(R.drawable.hplib_ic_play_download);

            }
        });

        this.next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (videoPosition < videoList.size() - 1) {
                    videoPosition++;
                    videoView.setVideoPath(videoList.get(videoPosition).getDATA());
                    videoView.start();
                    setSongsCurrentValues();
                    if (videoList.get(videoPosition).getIsMp3()) {
                        mp3View.setVisibility(View.VISIBLE);
                    } else {
                        mp3View.setVisibility(View.GONE);
                    }
                }
            }
        });
        this.previous.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (videoPosition > 0) {
                    FloatingWidgetService floatingWidgetService = FloatingWidgetService.this;
                    floatingWidgetService.videoPosition--;
                    videoView.setVideoPath(videoList.get(videoPosition).getDATA());
                    videoView.start();
                    setSongsCurrentValues();
                    if (videoList.get(videoPosition).getIsMp3()) {
                        mp3View.setVisibility(View.VISIBLE);
                    } else {
                        mp3View.setVisibility(View.GONE);
                    }
                }
            }
        });
        this.closeWindow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                videoView.release(false);
                stopSelf();
            }
        });
        this.playPause.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPause.setImageResource(R.drawable.hplib_ic_play_download);
                    return;
                }
                videoView.start();
                playPause.setImageResource(R.drawable.hplib_ic_pause);
            }
        });
        this.floatingWindow.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                setSongsCurrentValues();
                FloatingWidgetService floatingWidgetService = FloatingWidgetService.this;
                videoView.release(false);
                floatingWidgetService.startActivity(GiraffePlayerActivity.getInstance(floatingWidgetService.getApplicationContext(),
                        true).setFlags(268435456));
                stopSelf();
            }
        });
        this.floatingView.findViewById(R.id.mainParentRelativeLayout).setOnTouchListener(new View.OnTouchListener() {
            float mTouchX;
            float mTouchY;
            int mXAxis;
            int mYAxis;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action != 0) {
                    if (action != 1) {
                        if (action != 2) {
                            return false;
                        }
                        params.x = this.mXAxis + ((int) (motionEvent.getRawX() - this.mTouchX));
                        params.y = this.mYAxis + ((int) (motionEvent.getRawY() - this.mTouchY));
                        windowManager.updateViewLayout(floatingView, params);
                    }
                    return true;
                }
                this.mXAxis = params.x;
                this.mYAxis = params.y;
                this.mTouchX = motionEvent.getRawX();
                this.mTouchY = motionEvent.getRawY();
                return true;
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        View view = this.floatingView;
        if (view != null) {
            this.windowManager.removeView(view);
        }
    }

    public void setSongsCurrentValues() {
        SharedPref.setVideoList(videoList, this);
        SharedPref.save(this, SharedPref.PREF_CURRENT_PLAY_INDEX, videoPosition);
        SharedPref.save(this, SharedPref.PREF_CURRENT_SEEK_POSITION, videoView.getCurrentPosition());
        SharedPref.save(this, SharedPref.PREF_IS_FLOATING_SCREEN, true);
    }

}
