package com.zy.mapclock.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.MapView;
import com.zy.mapclock.R;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 重写onBackPressed()方法，弹出退出提示框
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "onBackPressed: access");
        AlertDialog dialog = createDialog();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * @return 创建退出dialog
     */
    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle("确定要退出吗？")
                .setMessage("点击确定退出当前应用")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: here");
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }
}
