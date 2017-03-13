package com.markypq.gpshook;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by yupeiquan on 2017/3/13 0013.
 */

public class GPSHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedBridge.hookAllConstructors(LocationManager.class,new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.args.length==2) {
                    Context context = (Context) param.args[0]; //这里的 context
                    XposedBridge.log(" 对 "+getProgramNameByPackageName(context)+" 模拟位置");
                    //把权限的检查 hook掉
                    XposedHelpers.findAndHookMethod(context.getClass(), "checkCallingOrSelfPermission", String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (param.args[0].toString().contains("INSTALL_LOCATION_PROVIDER")){
                                param.setResult(PackageManager.PERMISSION_GRANTED);
                            }
                        }
                    });
                    XposedBridge.log("LocationManager : " + context.getPackageName() + " class:= " + param.args[1].getClass().toString());
                  //获取到  locationManagerService 主动调用 对象的 reportLocation 方法  可以去模拟提供位置信息
                    //这里代码中并没有涉及到主动调用
                  Object   locationManagerService = param.args[1];
                }
            }
        });
        //主要代码  将系统的数据替换掉
        XposedHelpers.findAndHookMethod("com.android.server.LocationManagerService", lpparam.classLoader, "reportLocation", Location.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Location location = (Location) param.args[0];
                XposedBridge.log("实际 系统 经度"+location.getLatitude() +" 系统 纬度"+location.getLongitude() +"系统 加速度 "+location.getAccuracy());
                XSharedPreferences xsp =new XSharedPreferences("com.markypq.gpshook","markypq");
               if (xsp.getBoolean("enableHook",true)){
                   double latitude = Double.valueOf(xsp.getString("lan","117.536246"))+ (double) new Random().nextInt(1000) / 1000000 ;
                   double longtitude = Double.valueOf(xsp.getString("lon","36.681752"))+ (double) new Random().nextInt(1000) / 1000000 ;
                   location.setLongitude(longtitude);
                   location.setLatitude(latitude);
                   XposedBridge.log("hook 系统 经度"+location.getLatitude() +" 系统 纬度"+location.getLongitude() +"系统 加速度 "+location.getAccuracy());
               }

            }
        });
       if (lpparam.packageName.contains("tencent") || lpparam.packageName.contains("mark") ){ //注意 不加包名过滤 容易把手机干的开不了机
           LocationHook.HookAndChange(lpparam.classLoader,0,0);
       }

    }
    /**
     * 通过包名获取应用程序的名称。
     * @param context
     *            Context对象。
     *            包名。
     * @return 返回包名所对应的应用程序的名称。
     */
    public static String getProgramNameByPackageName(Context context) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

}
