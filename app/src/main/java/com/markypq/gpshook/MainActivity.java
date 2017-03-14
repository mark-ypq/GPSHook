package com.markypq.gpshook;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class MainActivity  extends AppCompatActivity {
    TextView tv;
    EditText lan, lon,acc;
    CheckBox enableHook;
    TestLocationListener mlistener = new TestLocationListener();
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.show);
        lan = (EditText) findViewById(R.id.lan);
        lon = (EditText) findViewById(R.id.lon);
        acc= (EditText) findViewById(R.id.acc);
        enableHook = (CheckBox) findViewById(R.id.enableHook);
        initView();
        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        getLocation(null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==0x01&&resultCode==Activity.RESULT_OK){
            SharedPreferences sp = getSharedPreferences("markypq", MODE_WORLD_READABLE);
            SharedPreferences.Editor e = sp.edit();
            e.putString("lan", data.getDoubleExtra("lan",0)+"");
            e.putString("lon", data.getDoubleExtra("lon",0)+"");
            e.commit();
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        SharedPreferences sp = getSharedPreferences("markypq", MODE_WORLD_READABLE);
        lan.setText(sp.getString("lan", ""));
        lon.setText(sp.getString("lon", ""));
        acc.setText(sp.getString("acc",""));
        enableHook.setChecked(sp.getBoolean("enableHook",true));
    }

    @TargetApi(23)
    public void getLocation(View view) {

        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 1, mlistener);
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null)
            tv.setText(location.getLongitude() + "|" + location.getLatitude());
      /*  try {
            Field localField = Class.forName("android.os.ServiceManager")
                    .getDeclaredField("sCache");
            localField.setAccessible(true);
            HashMap<String, IBinder>   map =      (HashMap<String, IBinder>) localField.get(null);
            for (String s:map.keySet()){
                Log.d("local",s+map.get(s).toString());
            }
        }catch (Exception e){

        }*/
    }

    public void save(View view) {
        SharedPreferences sp = getSharedPreferences("markypq", MODE_WORLD_READABLE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("lan", lan.getText().toString());
        e.putString("lon", lon.getText().toString());
        e.putString("acc",acc.getText().toString());
        e.putBoolean("enableHook",enableHook.isChecked());
        e.commit();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
       // getLocation(null);
    }

    public void change(View view) {
        if (Build.VERSION.SDK_INT >= 23)
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(mlistener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200,1, mlistener);
    }

    public void map(View view) {
        Intent intent =new Intent(MainActivity.this,MapActivity.class);
        SharedPreferences sp = getSharedPreferences("markypq", MODE_WORLD_READABLE);
            double latitude = Double.valueOf(sp.getString("lan","117.536246"));
            double longtitude = Double.valueOf(sp.getString("lon","36.681752"));
        intent.putExtra("lan",latitude);
        intent.putExtra("lon",longtitude);
        startActivityForResult(intent,0x01 );
    }

    private class TestLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location){
            //当位置发生改变时调用
            tv.setText("经度: " + location.getLatitude() + " ,纬度: " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String provider){
            //当适配器禁用时调用
        }

        @Override
        public void onProviderEnabled(String provider){
            //当适配器有效时调用
        }

        public void onStatusChanged(String provider){
            //当状态改变时调用
        }
    }

  /*  * 返回查询条件
    *
            * @return
            */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:

            case R.id.about:
                try {
                    String url="mqqwpa://im/chat?chat_type=wpa&uin=2039609991";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }catch (Exception e){
                    ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);;
                    myClipboard.setPrimaryClip(ClipData.newPlainText("text", "2039609991"));
                    Toast.makeText(MainActivity.this,"作者QQ已经复制",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.donate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("HTTPS://QR.ALIPAY.COM/FKX04652BHHIVRVZIABFB1".toLowerCase())));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
