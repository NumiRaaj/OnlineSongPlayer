package com.codeempire.jetplayer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codeempire.jetplayer.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btnAllow;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_permission);
        initView();
        initListener();
    }

    private void initListener() {
        this.btnAllow.setOnClickListener(this);
    }

    private void initView() {
        this.btnAllow = (TextView) findViewById(R.id.btnAllow);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnAllow && Build.VERSION.SDK_INT >= 23) {
            checkPermissionAndThenLoad();
        }
    }

    private void checkPermissionAndThenLoad() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 101) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) {

            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Toast.makeText(this, "Allow this permission!", 0).show();
    }
}
