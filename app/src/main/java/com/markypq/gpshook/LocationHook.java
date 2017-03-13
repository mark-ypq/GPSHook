package com.markypq.gpshook;

import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class LocationHook {
    public static void HookAndChange(ClassLoader classLoader,final int lac, final int cid) {

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                "getCellLocation", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        GsmCellLocation gsmCellLocation = new GsmCellLocation();
                        gsmCellLocation.setLacAndCid(lac, cid);
                        param.setResult(gsmCellLocation);
                      //  param.setResult(null);
                        XposedBridge.log("getCellLocation");
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.PhoneStateListener", classLoader,
                "onCellLocationChanged", CellLocation.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        GsmCellLocation gsmCellLocation = new GsmCellLocation();
                        gsmCellLocation.setLacAndCid(lac, cid);
                       param.setResult(gsmCellLocation);
                       // param.setResult(null);
                        XposedBridge.log("onCellLocationChanged");
                    }
                });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getPhoneCount", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(1);
                        }
                    });
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getNeighboringCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(new ArrayList<>());
                          //  param.setResult(null);
                            XposedBridge.log("getNeighboringCellInfo");
                        }
                    });
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getAllCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(getCell(460, 0, lac, cid, 0, 0));
                          //  param.setResult(null);
                        }
                    });
            XposedHelpers.findAndHookMethod("android.telephony.PhoneStateListener", classLoader,
                    "onCellInfoChanged", List.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                          //  param.setResult(null);
                           param.setResult(getCell(460, 0, lac, cid, 0,0));
                        }
                    });
        }

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(new ArrayList<>());
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "getWifiState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(1);
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "isWifiEnabled", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", classLoader, "getMacAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("00-00-00-00-00-00-00-00");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", classLoader, "getSSID", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("null");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", classLoader, "getBSSID", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("00-00-00-00-00-00-00-00");
            }
        });


        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "getTypeName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("WIFI");
                    }
                });
        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isConnectedOrConnecting", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isConnected", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isAvailable", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.CellInfo", classLoader,
                "isRegistered", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location location = new Location(LocationManager.GPS_PROVIDER);
                XSharedPreferences xsp =new XSharedPreferences("com.markypq.gpshook","markypq");
                double latitude = Double.valueOf(xsp.getString("lan"," 117.536246"))+ (double) new Random().nextInt(1000) / 1000000 ;
                double longtitude = Double.valueOf(xsp.getString("lon","36.681752"))+ (double) new Random().nextInt(1000) / 1000000  ;
                location.setLatitude(latitude);
                location.setLongitude(longtitude);
                location.setAccuracy(Float.valueOf(xsp.getString("acc","1050")));
                location.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                XposedBridge.log("getLastLocation");
                param.setResult(location);
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location location = new Location(LocationManager.GPS_PROVIDER);
                XSharedPreferences xsp =new XSharedPreferences("com.markypq.gpshook","markypq");
                double latitude = Double.valueOf(xsp.getString("lan"," 117.536246"))+ (double) new Random().nextInt(1000) / 1000000  ;
                double longtitude = Double.valueOf(xsp.getString("lon","36.681752"))+ (double) new Random().nextInt(1000) / 1000000  ;
                location.setLatitude(latitude);
                location.setLongitude(longtitude);
                location.setAccuracy(Float.valueOf(xsp.getString("acc","1050")));
                location.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                XposedBridge.log("getLastKnownLocation");
                param.setResult(location);
            }
        });


        XposedBridge.hookAllMethods(LocationManager.class, "getProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("gps");
                param.setResult(arrayList);
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("gps");
                XposedBridge.log("getBestProvider");
            }

        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "addGpsStatusListener", GpsStatus.Listener.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] != null) {
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 1);
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 3);
                }
                XposedBridge.log("addGpsStatusListener");
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "addNmeaListener", GpsStatus.NmeaListener.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });

        XposedHelpers.findAndHookMethod("android.location.LocationManager", classLoader,
                "getGpsStatus", GpsStatus.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        GpsStatus gss = (GpsStatus) param.getResult();
                        if (gss == null)
                            return;
                        XposedBridge.log("getGpsStatus");
                        Class<?> clazz = GpsStatus.class;
                        Method m = null;
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.getName().equals("setStatus")) {
                                if (method.getParameterTypes().length > 1) {
                                    m = method;
                                    break;
                                }
                            }
                        }
                        if (m == null)
                            return;

                        //access the private setStatus function of GpsStatus
                        m.setAccessible(true);

                        //make the apps belive GPS works fine now
                        int svCount = 5;
                        int[] prns = {1, 2, 3, 4, 5};
                        float[] snrs = {0, 0, 0, 0, 0};
                        float[] elevations = {0, 0, 0, 0, 0};
                        float[] azimuths = {0, 0, 0, 0, 0};
                        int ephemerisMask = 0x1f;
                        int almanacMask = 0x1f;

                        //5 satellites are fixed
                        int usedInFixMask = 0x1f;

                        XposedHelpers.callMethod(gss, "setStatus", svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                        param.args[0] = gss;
                        param.setResult(gss);
                        try {
                            m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                            param.setResult(gss);
                        } catch (Exception e) {
                            XposedBridge.log(e);
                        }
                    }
                });

        for (Method method : LocationManager.class.getDeclaredMethods()) {
            if (method.getName().equals("requestLocationUpdates")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())) {
                XposedBridge.log("找到requestLocationUpdates");
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args.length >= 4 && (param.args[3] instanceof LocationListener)) {

                            LocationListener ll = (LocationListener) param.args[3];

                            Class<?> clazz = LocationListener.class;
                            Method m = null;
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.getName().equals("onLocationChanged") && !Modifier.isAbstract(method.getModifiers())) {
                                    m = method;
                                    XposedBridge.log("调用onLocationChanged");
                                    break;
                                }
                            }
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            XSharedPreferences xsp =new XSharedPreferences("com.markypq.gpshook","markypq");
                            double latitude = Double.valueOf(xsp.getString("lan"," 117.536246"))+ (double) new Random().nextInt(1000) / 1000000  ;
                            double longtitude = Double.valueOf(xsp.getString("lon","36.681752"))+ (double) new Random().nextInt(1000) / 1000000  ;
                            location.setLatitude(latitude);
                            location.setLongitude(longtitude);
                            location.setAccuracy(Float.valueOf(xsp.getString("acc","100")));
                            location.setTime(System.currentTimeMillis());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }
                            XposedHelpers.callMethod(ll, "onLocationChanged", location);
                            try {
                                if (m != null) {
                                    m.invoke(ll, location);
                                    XposedBridge.log("调用onLocationChanged");
                                    XposedBridge.log("调用onLocationChanged:"  + ",经度" + latitude + ",纬度" + longtitude);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                });
            }

            if (method.getName().equals("requestSingleUpdate")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args.length >= 3 && (param.args[1] instanceof LocationListener)) {

                            LocationListener ll = (LocationListener) param.args[3];

                            Class<?> clazz = LocationListener.class;
                            Method m = null;
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.getName().equals("onLocationChanged") && !Modifier.isAbstract(method.getModifiers())) {
                                    m = method;
                                    XposedBridge.log("onLocationChanged");
                                    break;
                                }
                            }

                            try {
                                if (m != null) {
                                    Location location = new Location(LocationManager.GPS_PROVIDER);
                                    XSharedPreferences xsp =new XSharedPreferences("com.markypq.gpshook","markypq");
                                    double latitude = Double.valueOf(xsp.getString("lan"," 117.536246"))+ (double) new Random().nextInt(1000) / 1000000  ;
                                    double longtitude = Double.valueOf(xsp.getString("lon","36.681752"))+ (double) new Random().nextInt(1000) / 1000000  ;
                                    location.setLatitude(latitude);
                                    location.setLongitude(longtitude);
                                     location.setAccuracy(Float.valueOf(xsp.getString("acc","1050")));
                                    location.setTime(System.currentTimeMillis());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                                    }
                                    m.invoke(ll, location);
                                    XposedBridge.log("onLocationChanged  :"  + ",经度" + latitude + ",纬度" + longtitude);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                });
            }
        }
    }

    private static ArrayList getCell(int mcc, int mnc, int lac, int cid, int sid, int networkType) {
        ArrayList arrayList = new ArrayList();
        CellInfoGsm cellInfoGsm = (CellInfoGsm) XposedHelpers.newInstance(CellInfoGsm.class);
        XposedHelpers.callMethod(cellInfoGsm, "setCellIdentity", XposedHelpers.newInstance(CellIdentityGsm.class, new Object[]{Integer.valueOf(mcc), Integer.valueOf(mnc), Integer.valueOf(
                lac), Integer.valueOf(cid)}));
        CellInfoCdma cellInfoCdma = (CellInfoCdma) XposedHelpers.newInstance(CellInfoCdma.class);
        XposedHelpers.callMethod(cellInfoCdma, "setCellIdentity", XposedHelpers.newInstance(CellIdentityCdma.class, new Object[]{Integer.valueOf(lac), Integer.valueOf(sid), Integer.valueOf(cid), Integer.valueOf(0), Integer.valueOf(0)}));
        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) XposedHelpers.newInstance(CellInfoWcdma.class);
        XposedHelpers.callMethod(cellInfoWcdma, "setCellIdentity", XposedHelpers.newInstance(CellIdentityWcdma.class, new Object[]{Integer.valueOf(mcc), Integer.valueOf(mnc), Integer.valueOf(lac), Integer.valueOf(cid), Integer.valueOf(300)}));
        CellInfoLte cellInfoLte = (CellInfoLte) XposedHelpers.newInstance(CellInfoLte.class);
        XposedHelpers.callMethod(cellInfoLte, "setCellIdentity", XposedHelpers.newInstance(CellIdentityLte.class, new Object[]{Integer.valueOf(mcc), Integer.valueOf(mnc), Integer.valueOf(cid), Integer.valueOf(300), Integer.valueOf(lac)}));
        if (networkType == 1 || networkType == 2) {
            arrayList.add(cellInfoGsm);
        } else if (networkType == 13) {
            arrayList.add(cellInfoLte);
        } else if (networkType == 4 || networkType == 5 || networkType == 6 || networkType == 7 || networkType == 12 || networkType == 14) {
            arrayList.add(cellInfoCdma);
        } else if (networkType == 3 || networkType == 8 || networkType == 9 || networkType == 10 || networkType == 15) {
            arrayList.add(cellInfoWcdma);
        }
        return arrayList;
    }
}