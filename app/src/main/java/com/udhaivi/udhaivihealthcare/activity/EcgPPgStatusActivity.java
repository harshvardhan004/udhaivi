package com.udhaivi.udhaivihealthcare.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.udhaivi.udhaivihealthcare.R;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EcgPPgStatusActivity extends BaseActivity {


    @BindView(R.id.data_info)
    TextView data_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecgppgstatus);
        ButterKnife.bind(this);


    }


    @OnClick({R.id.getstatus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getstatus:
                sendValue(BleSDK.GetEcgPpgStatus());
                break;

        }
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.GetEcgPpgStatus:
                data_info.setText(maps.toString());
                break;
        }}

}
