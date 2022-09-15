package com.udhaivi.udhaivihealthcare;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.blesdk2025.model.AutoMode;
import com.jstyle.blesdk2025.model.MyDeviceTime;
import com.udhaivi.udhaivihealthcare.Util.BleData;
import com.udhaivi.udhaivihealthcare.Util.RxBus;
import com.udhaivi.udhaivihealthcare.Util.SDUtil;
import com.udhaivi.udhaivihealthcare.activity.BaseActivity;
import com.udhaivi.udhaivihealthcare.activity.DeviceScanActivity;
import com.udhaivi.udhaivihealthcare.adapter.MainAdapter;
import com.udhaivi.udhaivihealthcare.adapter.OxyAdapter;
import com.udhaivi.udhaivihealthcare.app.LoginSplash;
import com.udhaivi.udhaivihealthcare.ble.BleManager;
import com.udhaivi.udhaivihealthcare.ble.BleService;
import com.udhaivi.udhaivihealthcare.databinding.ActivityDashboardBinding;
import com.udhaivi.udhaivihealthcare.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Dashboard extends BaseActivity implements MainAdapter.onItemClickListener {

    private ActivityDashboardBinding binding;
    private String deviceaddress, devicename;
    byte ModeStart=0x00;
    byte ModeContinue=0x02;
    List<Map<String, String>> listhrv = new ArrayList<>();
    List<Map<String, String>> listtemp = new ArrayList<>();
    List<Map<String, String>> listspo = new ArrayList<>();
    private OxyAdapter heartRateDataAdapter = new OxyAdapter();
    String TEMP, HRTRATE, STEP, CALORIES, DISTANCE;
    int dataCount = 0;
    ArrayList<String> x = new ArrayList<>();
    public String address;
    HomeFragment frag = new HomeFragment();
    private ProgressDialog progressDialog;
    private Disposable subscription;
    boolean isStartReal;
    public static int phoneDataLength = 200;//手机一个包能发送的最多数据
    private ListenerReceiver receiver;
    protected static  final  int MY_PERMISSIONS_REQUEST_CALL_PHONE=321;
    private MainAdapter mainAdapter;
    int type=1;
    boolean open=true;
    int setinterval = 0;
    private BluetoothAdapter mBluetoothAdapter;
    int syncheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

//        fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_bottom_nav);

        SharedPreferences editor = getSharedPreferences("User_Details", MODE_PRIVATE);
        String name = editor.getString("firstname", "");
        String phone = editor.getString("phone", "");

//        if( name == "" && phone == ""){
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    // Do something after 5s = 5000ms
//
//                    Intent i = new Intent(Dashboard.this, LoginSplash.class);
//                    startActivity(i);
//                    finish();
//                }
//            }, 3000);
//        }

        init();
        connectDevice();
        registerReceiver();
        requestPermission(this);


        frag = (HomeFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_home);

        if(setinterval == 0) {

            setinterval = 1;

            sendValue(BleSDK.SetAutomatic(true, 1, AutoMode.AutoHeartRate));
            sendValue(BleSDK.SetAutomatic(true, 2, AutoMode.AutoSpo2));
            sendValue(BleSDK.SetAutomatic(true, 3, AutoMode.AutoTemp));
            sendValue(BleSDK.SetAutomatic(true, 4, AutoMode.AutoHrv));
        }

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // Add your code here.
//                        getalldata();

                        MyAsyncTask myAsyncTask = new MyAsyncTask();
                        myAsyncTask.execute();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 300000,300000);


    }

    public Object getaddress(){
        return address;
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            tvInfo.setText("Start");
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Your background method
            Log.d("hjgjjk", "vjvv");
            sendValue(BleSDK.StartDeviceMeasurementWithType(1,open));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            tvInfo.setText("Finish");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("gvjn", "gcghf");
//                    HomeFragment fragment1 = new HomeFragment();
//                    ((HomeFragment) fragment1).changesync();
                    syncheck = 0;
                    getalldata();
                }
            }, 90000);

        }
    }


    public void getalldata(){

        listhrv.clear();
        listtemp.clear();
//        listspo.clear();
//        TEMP = "";
//        HRTRATE = "";
//        STEP = "";
//        CALORIES = "";
//        DISTANCE = "";
        dataCount = 0;

        GetStaticHRWithMode(ModeStart, "HRV");
        GetStaticHRWithMode(ModeStart, "SPO2");
        GetStaticHRWithMode(ModeStart, "TEMP");

//        sendValue(BleSDK.StartDeviceMeasurementWithType(1,open));
//        sendValue(BleSDK.StartDeviceMeasurementWithType(2,open));
//        sendValue(BleSDK.StartDeviceMeasurementWithType(3,open));



    }

    private static final String TAG = "HeartRateInfoActivity";
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);

        switch (dataType) {

            case BleConst.GetHRVData:
                dataCount++;

                listhrv.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
//                Log.d("sjbsj646", String.valueOf(listhrv.get(0)));

                if (finish) {

                }
                if (dataCount == 50) {
                    if (finish) {
                        //  hrvDataAdapter.setData(list);
                    } else {
                        GetStaticHRWithMode(ModeContinue, "HRV");
                    }
                }
                break;

            case BleConst.RealTimeStep:
                Map<String, String> mmp = getData(maps);
                TEMP = mmp.get(DeviceKey.TempData);//温度 temperature
                HRTRATE = mmp.get(DeviceKey.HeartRate);//温度 temperature
                STEP = mmp.get(DeviceKey.Step);//温度 temperature
                CALORIES = mmp.get(DeviceKey.Calories);//温度 temperature
                DISTANCE = mmp.get(DeviceKey.Distance);//温度 temperature

//                TextView hrtrate = frag.getView().findViewById(R.id.hrtrate);
//                hrtrate.setText(mmp.get(DeviceKey.HeartRate));

//                Log.d("tfhyghukilo", String.valueOf(mmp.get(DeviceKey.HeartRate)));

                break;
            case BleConst.GetAutomaticSpo2Monitoring:

//                listspo.addAll((maps.toString()));
                Log.d("jiojlk588", String.valueOf(maps.toString()));
                break;

            case BleConst.MeasurementHrvCallback:
            case BleConst.MeasurementHeartCallback:
            case BleConst.MeasurementOxygenCallback:
                Log.d("xvdv", String.valueOf(maps.toString()));
                break;

            case BleConst.SetAutomatic://设置  Set
                Log.d("lkjsdk", dataType);
                break;
        }

        getlistdata();
    }


    public void GetStaticHRWithMode(byte mode, String type){

        switch (type){
            case "HRV":
                sendValue(BleSDK.GetHRVDataWithMode(mode,""));
                break;
            case "TEMP":
                sendValue(BleSDK.RealTimeStep(true,true));
                break;
            case "SPO2":
                sendValue(BleSDK.Obtain_The_data_of_manual_blood_oxygen_test(ModeStart));
                break;
        }
    }

    public ArrayList<String> getlistdata(){

        if(listhrv.size() == 0){

            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");

            Log.d("jhfjyu22", String.valueOf(x));

        }else {

            if(listspo.size() > 0) {
                x.add(String.valueOf(listspo.get(0).get("Blood_oxygen")));
            }
            else{
                x.add("0");
            }

            if(listhrv.size() > 0) {
                x.add(String.valueOf(listhrv.get(0).get("date")));
                x.add(String.valueOf(listhrv.get(0).get("highBP")));
                x.add(String.valueOf(listhrv.get(0).get("lowBP")));
                x.add(String.valueOf(listhrv.get(0).get("stress")));
                x.add(String.valueOf(listhrv.get(0).get("hrv")));
            }
            else{
                x.add("0");
                x.add("0");
                x.add("0");
                x.add("0");
                x.add("0");
            }
        }

        x.add(TEMP);
        x.add(HRTRATE);
        x.add(STEP);
        x.add(CALORIES);
        x.add(DISTANCE);

        Log.d("s5aca5as15", String.valueOf(x));

        Intent intent = new Intent("BDATA");
        intent.putStringArrayListExtra("peerId", x);
        intent.putExtra("syncheck", syncheck);
        LocalBroadcastManager.getInstance(Dashboard.this).sendBroadcast(intent);

        syncheck = 1;

        Intent intent1 = new Intent("DDATA");
        intent1.putStringArrayListExtra("peerIdD", x);
        LocalBroadcastManager.getInstance(Dashboard.this).sendBroadcast(intent1);

        dissMissDialog();

        return x;
    }


    @Override
    protected void onDestroy() {
        listhrv.clear();
        listtemp.clear();
        listspo.clear();
        dataCount = 0;

        super.onDestroy();

        unsubscribe();
        if (BleManager.getInstance().isConnected()) BleManager.getInstance().disconnectDevice();
    }

    @Override
    public void onItemClick(int position) {

    }

    private void connectDevice() {
        address = getIntent().getStringExtra("address");
        if (TextUtils.isEmpty(address)) {
            Log.i(TAG, "onCreate: address null ");
            return;
        }
        Log.i(TAG, "onCreate: ");
        BleManager.getInstance().connectDevice(address);
//        showConnectDialog(getString(R.string.connectting));
        getalldata();


    }

    public void showConnectDialog(String string) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(string);
        if (!progressDialog.isShowing()) progressDialog.show();

    }

    public void dissMissDialog() {

        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void init() {
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_GATT_onDescriptorWrite)) {
//                    dissMissDialog();
                    getalldata();
                    setTime();

                } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {

                    isStartReal = false;
                    dissMissDialog();
                }
            }
        });
    }

    private void setTime() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);//年 YEAR
        int month=calendar.get(Calendar.MONTH)+1;//月 MONTH
        int day=calendar.get(Calendar.DAY_OF_MONTH);//日 DAY_OF_MONTH
        int hour=calendar.get(Calendar.HOUR_OF_DAY);//时 HOUR_OF_DAY
        int min=calendar.get(Calendar.MINUTE);//分 MINUTE
        int second=calendar.get(Calendar.SECOND);//秒 SECOND
        MyDeviceTime setTime=new MyDeviceTime();
        setTime.setYear(year);
        setTime.setMonth(month);
        setTime.setDay(day);
        setTime.setHour(hour);
        setTime.setMinute(min);
        setTime.setSecond(second);
        sendValue(BleSDK.SetDeviceTime(setTime));//发送 Send
    }



    protected  class ListenerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON://Bluetooth on
                        if (TextUtils.isEmpty(address)) {
                            Log.i(TAG, "onCreate: address null ");
                            return;
                        }
                        BleManager.getInstance().connectDevice(address);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        if(null!=mainAdapter){
                            mainAdapter.setEnable(false);
                        }
                        BleManager.getInstance().disconnectDevice();
                        break;
                }
            }
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new ListenerReceiver();
        registerReceiver(receiver, filter);
    }

    private void requestPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                SDUtil.createFile("log");
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 0);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                SDUtil.createFile("log");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }else{
            SDUtil.createFile("log");
        }
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            Log.i(TAG, "unSubscribe: ");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                SDUtil.createFile("log");
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        if(address.equals("")) return;
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

                    ((DeviceScanActivity) getApplicationContext()).statusCheck();
                }
        }
        connectDevice();

    }


}