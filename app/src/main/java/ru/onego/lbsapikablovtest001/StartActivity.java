package ru.onego.lbsapikablovtest001;

import java.util.UUID;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import java.io.ByteArrayOutputStream;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field; /* Рефлексия (от позднелат. reflexio - обращение назад) - это механизм исследования данных о программе во время её выполнения. Рефлексия позволяет исследовать информацию о полях, методах и конструкторах классов. Можно также выполнять операции над полями и методами которые исследуются. Рефлексия в Java осуществляется с помощью Java Reflection API. Этот интерфейс API состоит из классов пакетов java.lang и java.lang.reflect. С помощью интерфейса Java Reflection API можно делать следующее:  */
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;


public class StartActivity extends AppCompatActivity  implements LbsLocationListener {
    private AppCompatActivity instance;
    private WifiAndCellCollector wifiAndCellCollector;
    private Context context;
    private ProgressDialog progressDialog;
    private TelephonyManager tm;



    private Button btnDoLbs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        SharedPreferences settings = getPreferences(AppCompatActivity.MODE_PRIVATE);
        String uuid = settings.getString("UUID", null);
        Log.d("Test uuid", String.valueOf(uuid));
        if (uuid == null) {
            uuid = generateUUID();
            Editor edit = settings.edit();
            edit.putString("UUID", uuid);
            edit.commit();
        }
        setContentView(R.layout.activity_start);
        instance = this;

        //Log.d("Test", tm.toString());
        btnDoLbs = (Button) findViewById(R.id.btn_do_lbs);

        wifiAndCellCollector = new WifiAndCellCollector(this, this, uuid);

        btnDoLbs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.hide();
                }
                progressDialog = ProgressDialog.show(instance, null, "Ожидайте ...");
                progressDialog.setCancelable(false); // Окно не отменяется кнопкой назад.
                progressDialog.setCanceledOnTouchOutside(false); /* Устанавливает, отменяется ли это диалоговое окно при касании вне границ окна. Если установлено значение true, диалоговое окно будет отменено, если оно еще не установлено. */

                progressDialog.show();
                (new Thread() {
                    @Override
                    public void run() {
                        Log.d("Test", "run requestMyLocation");
                        wifiAndCellCollector.requestMyLocation(); // запрос месторасположения.
                    }
                }).start();

                tm = (TelephonyManager) instance.getSystemService( Context.TELEPHONY_SERVICE );

                if (tm != null) {
                    //networkType = networkTypeStr.get(tm.getNetworkType());
                    //Log.d("Test", networkType);
                    //radioType = getRadioType(tm.getNetworkType());
                    //Log.d("Test", radioType);
                    //String mccAndMnc = tm.getNetworkOperator();
                    //Log.d("Test mccAndMnc", mccAndMnc);
                    ///cellInfos = new ArrayList<CellInfo>();
                    //if (mccAndMnc != null && mccAndMnc.length() > 3) {
                    //    mcc = mccAndMnc.substring(0, 3);
                    //    mnc = mccAndMnc.substring(3);
                    //} else {
                    //    mcc = mnc = null;
                    //}
                    //Log.d("Test mcc", mcc);
                    //Log.d("Test mnc", mnc);

                    try {
                        Log.d("Test", Build.MODEL.getBytes("UTF-8").toString());
                        /*
                        * encodeUrl функция, которая получает на вход массив байт в кодировке utf 8 для
                        * формирования проавильного URL*/
                        //model = new String(encodeUrl(Build.MODEL.getBytes("UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        //model = new String(encodeUrl(Build.MODEL.getBytes()));
                    }
                    //Log.d("Test model", model);
                    //try {
                        //Log.d("Test", getDeviceManufacturer().getBytes("UTF-8").toString());
                        //manufacturer = new String(encodeUrl(getDeviceManufacturer().getBytes("UTF-8")));
                    //} catch (UnsupportedEncodingException e) {
                        //manufacturer = new String(encodeUrl(getDeviceManufacturer().getBytes()));
                    //}
                    //Log.d("Test manufacturer", manufacturer);
                }
                //LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_SCAN_TIMEOUT, 1, this);
                //(new Thread(this)).start();
                //Log.d("Test", "6789");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        wifiAndCellCollector.startCollect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiAndCellCollector.stopCollect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChange(final LbsInfo lbsInfo) {
        Log.d("Test", "LocationChange");
        if (lbsInfo != null) {

        }

    }
    /**
     * RFC UUID generation
     */
    public String generateUUID() {
        UUID uuid = UUID.randomUUID();
        StringBuilder str = new StringBuilder(uuid.toString());
        int index = str.indexOf("-");
        while (index > 0) {
            str.deleteCharAt(index);
            index = str.indexOf("-");
        }
        return str.toString();
    }
}
