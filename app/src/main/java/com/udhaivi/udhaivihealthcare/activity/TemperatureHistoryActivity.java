package com.udhaivi.udhaivihealthcare.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.udhaivi.udhaivihealthcare.R;
import com.udhaivi.udhaivihealthcare.adapter.GpsDataAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手动温度数据 （Manual temperature data）
 */
public class TemperatureHistoryActivity extends BaseActivity {
    @BindView(R.id.RecyclerView_exerciseHistory)
    RecyclerView RecyclerViewExerciseHistory;
    byte ModeStart=0x00;       //开始获取数据 start getting data
    byte ModeContinue=0x02;    //继续读取数据 continue reading data
    byte ModeDelete=(byte) 0x99;//删除数据  delete data
    private List< String> list;
    private GpsDataAdapter activityModeDataAdapter;
    private int dataCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_data);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        list=new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewExerciseHistory.setLayoutManager(linearLayoutManager);
        activityModeDataAdapter = new GpsDataAdapter();
        RecyclerViewExerciseHistory.setAdapter(activityModeDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewExerciseHistory.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.bt_getData, R.id.bt_deleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_getData://读取手动温度数据 Read manual temperature data
                list.clear();
                dataCount=0;
                getData(ModeStart);
                break;
            case R.id.bt_deleteData://删除手动温度数据 Delete manual temperature data
                list.clear();
                dataCount=0;
                getData(ModeDelete);
                break;

        }
    }



    private void getData(byte mode){
        // showProgressDialog("同步数据(Synchronize data)");
        sendValue(BleSDK.GetTemperature_historyDataWithMode(mode,""));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.deleteGetTemperature_historyDataWithMode://删除手动温度数据 Delete manual temperature data
                list.add(maps.toString());
                activityModeDataAdapter.setData(list);
                break;
            case BleConst.Temperature_history://读取手动温度数据 Read manual temperature data
                list.add(maps.toString());
                dataCount++;
                if(finish){
                    dataCount=0;
                    disMissProgressDialog();
                    activityModeDataAdapter.setData(list);
                }
                if(dataCount==50){
                    Log.e("sdadaa","sssssssssssssssssssss");
                    dataCount=0;
                    if(finish){
                        disMissProgressDialog();
                        activityModeDataAdapter.setData(list);
                    }else{
                        getData(ModeContinue);
                    }
                }

                break;
        }
    }
}
