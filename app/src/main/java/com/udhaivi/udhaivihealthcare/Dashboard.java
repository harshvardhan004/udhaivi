package com.udhaivi.udhaivihealthcare;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.udhaivi.udhaivihealthcare.Util.BleData;
import com.udhaivi.udhaivihealthcare.Util.RxBus;
import com.udhaivi.udhaivihealthcare.Util.SDUtil;
import com.udhaivi.udhaivihealthcare.activity.BaseActivity;
import com.udhaivi.udhaivihealthcare.adapter.MainAdapter;
import com.udhaivi.udhaivihealthcare.adapter.OxyAdapter;
import com.udhaivi.udhaivihealthcare.app.LoginSplash;
import com.udhaivi.udhaivihealthcare.ble.BleManager;
import com.udhaivi.udhaivihealthcare.ble.BleService;
import com.udhaivi.udhaivihealthcare.databinding.ActivityDashboardBinding;
import com.udhaivi.udhaivihealthcare.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    String TEMP;
    int dataCount = 0;
    ArrayList<String> x = new ArrayList<>();
    String address;
    HomeFragment frag = new HomeFragment();
    private ProgressDialog progressDialog;
    private Disposable subscription;
    boolean isStartReal;
    public static int phoneDataLength = 200;//手机一个包能发送的最多数据
    private ListenerReceiver receiver;
    protected static  final  int MY_PERMISSIONS_REQUEST_CALL_PHONE=321;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        SharedPreferences editor = getSharedPreferences("User_Details", MODE_PRIVATE);
        String name = editor.getString("firstname", "");
        String phone = editor.getString("phone", "");

        if( name == "" && phone == ""){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    Intent i = new Intent(Dashboard.this, LoginSplash.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }

        init();
        connectDevice();
        registerReceiver();
        //创建文件 createFile
        requestPermission(this);


        frag = (HomeFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_home);

//        address = String.valueOf(getIntent().getStringExtra("address"));

//        getalldata();
//
//        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
//        exec.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                // code to execute repeatedly
//                getalldata();
//            }
//        }, 3, 2, TimeUnit.SECONDS); // execute every 60 seconds
////        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("BDATA"));

    }

    public Object getaddress(){
        return address;
    }


    public void getalldata(){

        listhrv.clear();
        listtemp.clear();
        listspo.clear();
        dataCount = 0;


        GetStaticHRWithMode(ModeStart, "HRV");
        GetStaticHRWithMode(ModeStart, "SPO2");
        GetStaticHRWithMode(ModeStart, "TEMP");

    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            Log.d("receiver", "Got message: " + intent.getStringExtra("peerId"));
//
//            Toast.makeText(context, "Got message:" + intent.getStringExtra("peerId") , Toast.LENGTH_SHORT).show();
//        }
//    };


    private static final String TAG = "HeartRateInfoActivity";
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);

        switch (dataType) {

            case BleConst.GetHRVData:
                listhrv.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
                Log.d("sjbsj646", String.valueOf(listhrv.get(0)));
                break;
            case BleConst.RealTimeStep:
                Map<String, String> mmp = getData(maps);
                TEMP = mmp.get(DeviceKey.TempData);//温度 temperature

//                TextView hrtrate = frag.getView().findViewById(R.id.hrtrate);
//                hrtrate.setText(mmp.get(DeviceKey.HeartRate));

//                Log.d("tfhyghukilo", String.valueOf(mmp.get(DeviceKey.HeartRate)));
                break;
            case BleConst.GetAutomaticSpo2Monitoring:

                listspo.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
                Log.d("jiojlk588", String.valueOf(listspo));
                break;
        }
    }


    public void GetStaticHRWithMode(byte mode, String type){
        /**
         * dateOfLastData 设备返回所有数据后最后的时间戳，第一次没有的情况为null，或者“”，同步数据后保存数据库
         *dateOfLastData The last timestamp after the device returns all data, null if there is no data for the first time, or "", save the database after synchronizing the data
         */
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

//        if(listspo.get(0).get("Blood_oxygen") == "" || listhrv.get(0).get("date") == "" || listhrv.get(0).get("highBP") == "" || listhrv.get(0).get("lowBP") == ""|| listhrv.get(0).get("stress") == "" ||  listhrv.get(0).get("heartRate") == "" || listhrv.get(0).get("hrv") == ""|| TEMP == ""){
//            Toast.makeText(Dashboard.this, "Please Connect the watch!", Toast.LENGTH_LONG).show();
//        }

        if(listspo.size() == 0 || listhrv.size() == 0 || TEMP == null){
//            Toast.makeText(Dashboard.this, "Oops! There seems to be some problem with the connection. Please gp back and connect the device again!", Toast.LENGTH_LONG).show();
//            if (!BleManager.getInstance().isConnected()) startActivity(new Intent(Dashboard.this, DeviceScanActivity.class));
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");
            x.add("0");

            Log.d("jhfjyu22", String.valueOf(x));        }
        else {

            x.add(String.valueOf(listspo.get(0).get("Blood_oxygen")));
            x.add(String.valueOf(listhrv.get(0).get("date")));
            x.add(String.valueOf(listhrv.get(0).get("highBP")));
            x.add(String.valueOf(listhrv.get(0).get("lowBP")));
            x.add(String.valueOf(listhrv.get(0).get("stress")));
            x.add(String.valueOf(listhrv.get(0).get("heartRate")));
            x.add(String.valueOf(listhrv.get(0).get("hrv")));
            x.add(TEMP);

            Log.d("s5aca5as15", String.valueOf(x));

        }

        return x;
    }


    @Override
    protected void onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        listhrv.clear();
        listtemp.clear();
        listspo.clear();
        dataCount = 0;

        super.onDestroy();

        unsubscribe();
        if (BleManager.getInstance().isConnected()) BleManager.getInstance().disconnectDevice();
    }

//    @Override
//    protected void onResume() {
////        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
//        super.onResume();
//
//    }

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
        showConnectDialog(getString(R.string.connectting));
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

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        mainRecyclerview.setLayoutManager(gridLayoutManager);
//        mainAdapter = new MainAdapter(options, this);
//        mainRecyclerview.setAdapter(mainAdapter);
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_GATT_onDescriptorWrite)) {
//                    mainAdapter.setEnable(true);
//                    btConnect.setEnabled(false);
//                    dissMissDialog();
                    getalldata();
                } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
//                    mainAdapter.setEnable(false);
//                    btConnect.setEnabled(true);

                    isStartReal = false;
                    dissMissDialog();
                }
            }
        });
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
//                        if(null!=btConnect){
//                            btConnect.setEnabled(true);}
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

}