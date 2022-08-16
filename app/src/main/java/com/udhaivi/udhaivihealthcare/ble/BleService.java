package com.udhaivi.udhaivihealthcare.ble;import android.annotation.SuppressLint;import android.app.Service;import android.bluetooth.BluetoothAdapter;import android.bluetooth.BluetoothDevice;import android.bluetooth.BluetoothGatt;import android.bluetooth.BluetoothGattCallback;import android.bluetooth.BluetoothGattCharacteristic;import android.bluetooth.BluetoothGattDescriptor;import android.bluetooth.BluetoothGattService;import android.bluetooth.BluetoothManager;import android.bluetooth.BluetoothProfile;import android.content.Context;import android.content.Intent;import android.os.Binder;import android.os.Build;import android.os.Handler;import android.os.IBinder;import android.text.TextUtils;import android.util.Log;import com.jstyle.blesdk2025.Util.BleSDK;import com.udhaivi.udhaivihealthcare.Util.BleData;import com.udhaivi.udhaivihealthcare.Util.ResolveData;import com.udhaivi.udhaivihealthcare.Util.RxBus;import com.udhaivi.udhaivihealthcare.Util.SDUtil;import java.lang.reflect.Method;import java.util.ArrayList;import java.util.HashMap;import java.util.LinkedList;import java.util.List;import java.util.Queue;import java.util.UUID;public final class BleService extends Service {    private static final String TAG = "BleService";    private static final UUID NOTIY = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");    private static final UUID SERVICE_DATA = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");    private static final UUID DATA_Characteristic = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");    private static final UUID NOTIY_Characteristic = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb");    private boolean NeedReconnect=false;    public boolean fastconnect = false;//是否连接成功过设备    public final static String ACTION_GATT_onDescriptorWrite = "com.jstylelife.ble.service.onDescriptorWrite";    public final static String ACTION_GATT_CONNECTED = "com.jstylelife.ble.service.ACTION_GATT_CONNECTED";    public final static String ACTION_GATT_DISCONNECTED = "com.jstylelife.ble.service.ACTION_GATT_DISCONNECTED";    public final static String ACTION_DATA_AVAILABLE = "com.jstylelife.ble.service.ACTION_DATA_AVAILABLE";    public HashMap<BluetoothDevice, BluetoothGatt> hasp = new HashMap<BluetoothDevice, BluetoothGatt>();    private final IBinder kBinder = new LocalBinder();    private static ArrayList<BluetoothGatt> arrayGatts = new ArrayList<BluetoothGatt>(); // 存放BluetoothGatt的集�?    public static BluetoothGattCharacteristic colorCharacteristic;    private HashMap<String, BluetoothGatt> gattHash = new HashMap<String, BluetoothGatt>();    private BluetoothManager bluetoothManager;    private BluetoothAdapter mBluetoothAdapter;    private BluetoothGatt mGatt;    private boolean isConnected;    private Handler handler = new Handler();    @Override    public IBinder onBind(Intent intent) {        initAdapter();        return kBinder;    }    @Override    public boolean onUnbind(Intent intent) {        return super.onUnbind(intent);    }    /**     * 初始化BLE 如果已经连接就不用再次连     *     * @param bleDevice     * @return     */    private String address;    private Context mContext;    public void initBluetoothDevice(final String address, final Context context) {        //MyLog.i("开始连接");        fastconnect = false;        this.address = address;        this.mContext = context;        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);        if(isConnected())return;        if(null!=mGatt){            refreshDeviceCache(mGatt);            mGatt=null;        }        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {            mGatt = device.connectGatt(context, false, bleGattCallback,BluetoothDevice.TRANSPORT_LE);        } else {            mGatt = device.connectGatt(context, false, bleGattCallback);        }        if (mGatt == null) {            System.out.println(device.getAddress() + "gatt is null");        }        // this.mContext=context;        // reconnect(true);    }    private boolean scanToConnect=true;    private boolean isScaning;    @SuppressLint("MissingPermission")    public void startScan(boolean enable) {        Log.i(TAG, "startScan: "+enable);        if (!mBluetoothAdapter.isEnabled() ) {            return;        }        if(scanToConnect){            startScanDevice(true);        }else{            initBluetoothDevice(address,mContext);        }        scanToConnect=!scanToConnect;    }    @SuppressLint("MissingPermission")    private void startScanDevice(boolean enable){        if(enable){            if(isScaning)return;            handler.postDelayed(new Runnable() {                @Override                public void run() {                    mBluetoothAdapter.stopLeScan(mLeScanCallback);                    isScaning=false;                    startScan(true);                }            },20000);            fastconnect=false;            mBluetoothAdapter.startLeScan(mLeScanCallback);        }else{            if(isScaning){                mBluetoothAdapter.stopLeScan(mLeScanCallback);                handler.removeCallbacksAndMessages(null);            }        }        isScaning=enable;    }    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {        @Override        public void onLeScan(final BluetoothDevice device, final int rssi,                             final byte[] scanRecord) {            if (device.getAddress().equals(address)) {                String name = ResolveData.decodeDeviceName(device,scanRecord);                if (!TextUtils.isEmpty(name) && name.equals("DfuTarg"))                    return;                if (mGatt != null)                    return;                handler.post(new Runnable() {                    @Override                    public void run() {                        startScanDevice(false);                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {                            mGatt = device.connectGatt(mContext, false, bleGattCallback,BluetoothDevice.TRANSPORT_LE);                        } else {                            mGatt = device.connectGatt(mContext, false, bleGattCallback);                        }                        if (mGatt == null) {                            System.out.println("gatt is null ");                        }                    }                });            }        }    };    private void initAdapter() {        if (bluetoothManager == null) {            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);            if (bluetoothManager == null) {                return;            }        }        mBluetoothAdapter = bluetoothManager.getAdapter();    }    /**     * 断开连接     */    public void disconnect() {        NeedReconnect = false;        broadcastUpdate(ACTION_GATT_DISCONNECTED);        if (mGatt != null) {            if(isConnected){                mGatt.disconnect();                mGatt.close();                mGatt=null;            }else{                Log.i(TAG, "close: ");                mGatt.close();                mGatt=null;            }        }        isConnected=false;    }    /**     * 根据设备的Mac地址断开连接     *     * @param address     */    public void disconnect(String address) {        ArrayList<BluetoothGatt> gatts = new ArrayList<BluetoothGatt>();        for (BluetoothGatt gatt : arrayGatts) {            if (gatt != null && gatt.getDevice().getAddress().equals(address)) {                gatts.add(gatt);                // gatt.disconnect();                gatt.close();                // gatt = null;            }        }        arrayGatts.removeAll(gatts);    }    public class LocalBinder extends Binder {        public BleService getService() {            return BleService.this;        }    }    private int discoverCount;    private Object ob = new Object();    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {        @Override        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {            if (status == 133||newState==0) {                if(mGatt!=null){                    mGatt.disconnect();                    mGatt.close();                    refreshDeviceCache(mGatt);                    mGatt=null;                } if(gatt!=null){                    gatt.disconnect();                    gatt.close();                    refreshDeviceCache(gatt);                    gatt=null;                }                if(NeedReconnect)startScan(true);                return;            }            String action = null;            Log.i(TAG, "onConnectionStateChange:  status"+ status+" newstate "+newState);            if (newState == BluetoothProfile.STATE_CONNECTED) {                try {                    gatt.discoverServices();                } catch (Exception e) { e.printStackTrace(); }            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {                isConnected=false;                Log.i(TAG, "onConnectionStateChange: "+ACTION_GATT_DISCONNECTED);                if (mGatt != null) {                    mGatt.close();                    mGatt = null;                }                queues.clear();                if(!NeedReconnect) {                    action = ACTION_GATT_DISCONNECTED;                    broadcastUpdate(action);                }if(!NeedReconnect) {                }else{                    if (fastconnect) {                        fastconnect=false;                        Log.e(TAG, "发送异常断开");                    }                    if (status == 133) {                        refreshDeviceCache(gatt);                    }                    startScan(true);                }            }        }        /*         * 搜索device中的services (non-Javadoc)         *         * @see         * android.bluetooth.BluetoothGattCallback#onServicesDiscovered(android         * .bluetooth.BluetoothGatt, int)         */        @Override        public void onServicesDiscovered(BluetoothGatt gatt, int status) {            // if (mGatt == null)            // return;            if (status == BluetoothGatt.GATT_SUCCESS) {                String address = gatt.getDevice().getAddress();                String name = mBluetoothAdapter.getRemoteDevice(address)                        .getName();                setCharacteristicNotification(true);              /*  if (gatt != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {                    gatt.requestMtu(512);                }else{                    setCharacteristicNotification(true);                }*/                discoverCount = 0;            } else {                // mGatt = null;                Log.w("servieDiscovered", "onServicesDiscovered received: "                        + status);            }        }        @Override        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {            super.onMtuChanged(gatt, mtu, status);            if (BluetoothGatt.GATT_SUCCESS == status) {                setCharacteristicNotification(true);            }else {                gatt.requestMtu(153);            }        }        /*         * 读取特征�?(non-Javadoc)         *         * @see         * android.bluetooth.BluetoothGattCallback#onCharacteristicRead(android         * .bluetooth.BluetoothGatt,         * android.bluetooth.BluetoothGattCharacteristic, int)         */        public void onCharacteristicRead(BluetoothGatt gatt, android.bluetooth.BluetoothGattCharacteristic characteristic, int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt                        .getDevice().getAddress());            } else {            }        }        public void onDescriptorWrite(BluetoothGatt gatt,                                      BluetoothGattDescriptor descriptor, int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                offerValue(BleSDK.disableAncs());                nextQueue();                isConnected=true;                broadcastUpdate(ACTION_GATT_onDescriptorWrite);            }else{                Log.i(TAG, "onDescriptorWrite: failed");            }        }        ;        /*         * 特征值的变化 (non-Javadoc)         *         * @see         * android.bluetooth.BluetoothGattCallback#onCharacteristicChanged(android         * .bluetooth.BluetoothGatt,         * android.bluetooth.BluetoothGattCharacteristic)         */        public void onCharacteristicChanged(BluetoothGatt gatt,                                            android.bluetooth.BluetoothGattCharacteristic characteristic) {            if (mGatt == null)                return;           Log.i(TAG, "onCharacteristicChanged: " + ResolveData.byte2Hex(characteristic.getValue()));            SDUtil.saveBTLog("log","Receiving: "+ ResolveData.byte2Hex(characteristic.getValue()));            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt                    .getDevice().getAddress());            //	SendData.sendBus(ACTION_DATA_AVAILABLE, characteristic.getValue());        }        public void onCharacteristicWrite(BluetoothGatt gatt,                                          BluetoothGattCharacteristic characteristic, int status) {            if (status == BluetoothGatt.GATT_SUCCESS) {                nextQueue();            } else {                //	MyLog.i("status" + status);            }        }        ;    };    public boolean refreshDeviceCache(BluetoothGatt gatt) {        try {            BluetoothGatt localBluetoothGatt = gatt;            Method localMethod = localBluetoothGatt.getClass().getMethod(                    "refresh", new Class[0]);            if (localMethod != null) {                boolean bool = ((Boolean) localMethod.invoke(                        localBluetoothGatt, new Object[0])).booleanValue();                return bool;            }        } catch (Exception localException) {            Log.e("s", "An exception occured while refreshing device");        }        return false;    }    /**     * 广播     *     * @param action     */    private void broadcastUpdate(String action) {        BleData bleData = new BleData();        bleData.setAction(action);        RxBus.getInstance().post(bleData);        //Intent intent = new Intent(action);        //sendBroadcast(intent);    }    /**     * 发�?带蓝牙信息的到广�? *     *     * @param action     * @param characteristic     */    private void broadcastUpdate(String action,                                 BluetoothGattCharacteristic characteristic, String mac) {     //   Intent intent = new Intent(action);        byte[] data = characteristic.getValue();        BleData bleData = new BleData();        bleData.setAction(action);        bleData.setValue(data);        RxBus.getInstance().post(bleData);    }    /**     * 读取设备数据     *     * @param     * @param characteristic     */    public void readValue(BluetoothGattCharacteristic characteristic) {        if (mGatt == null) return;        mGatt.readCharacteristic(characteristic);    }    /**     * 写入设备数据     */    public void writeValue(byte[] value) {        Log.d("sfdf", String.valueOf(value));        Log.d("asfdghj", String.valueOf(mGatt));        if (mGatt == null||value==null) return;        BluetoothGattService service = mGatt.getService(SERVICE_DATA);        if (service == null) return;        BluetoothGattCharacteristic characteristic = service.getCharacteristic(DATA_Characteristic);        if (characteristic == null) return;        if (value[0] ==(byte) 0x47) {            NeedReconnect = false;        }        characteristic.setValue(value);        Log.i(TAG, "writeValue: "+ ResolveData.byte2Hex(value));        mGatt.writeCharacteristic(characteristic);        SDUtil.saveBTLog("log","writeValue: "+ ResolveData.byte2Hex(value));    }    public void setCharacteristicNotification(boolean enable) {        if (mGatt == null) return;        BluetoothGattService service = mGatt.getService(SERVICE_DATA);        if (service == null) return;        BluetoothGattCharacteristic characteristic = service.getCharacteristic(NOTIY_Characteristic);        if (characteristic == null) return;        mGatt.setCharacteristicNotification(characteristic, enable);        try {            Thread.sleep(20);        } catch (InterruptedException e) { e.printStackTrace(); }        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(NOTIY);        if (descriptor == null) {            //MyLog.e("setCharacteristicNotification  descriptor=null，所以不能发送使能数据");            return;        }        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);        if (mGatt == null)            return;        mGatt.writeDescriptor(descriptor);    }    /**     * 获取services     *     * @return     */    public List<BluetoothGattService> getSupportedGattServices() {        if (mGatt == null) {            //MyLog.e("getServices, gatt is null ");            return null;        }        return mGatt.getServices();    }    /**     * 根据设备的Mac地址从已经连接的设备中匹配对应的BluetoothGatt对象     *     * @param device     * @return     */    private BluetoothGatt getBluetoothGatt(BluetoothDevice device) {        return mGatt;    }    /**     * //读取信号     *     * @param device     */    public void readRssi(BluetoothDevice device) {        mGatt.readRemoteRssi();    }    @Override    public void onDestroy() {        super.onDestroy();    }    Queue<byte[]> queues=new LinkedList<>();    public void offerValue(byte[]value) {        queues.offer(value);    }    public void nextQueue(){        final Queue<byte[]> requests=queues;        byte[]data=requests!=null?requests.poll():null;        writeValue(data);    }    public boolean isConnected(){        return this.isConnected;    }}