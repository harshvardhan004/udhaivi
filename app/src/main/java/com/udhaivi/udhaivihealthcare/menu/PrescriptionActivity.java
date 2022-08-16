package com.udhaivi.udhaivihealthcare.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.udhaivi.udhaivihealthcare.R;

public class PrescriptionActivity extends AppCompatActivity {

    BottomSheetDialog bottomSheetDialog;
    TextView textFile;
    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomsheetdialog();
            }
        });

    }

    public void bottomsheetdialog() {

        bottomSheetDialog = new BottomSheetDialog(PrescriptionActivity.this, R.style.TransparentDialog);
        View parentView = getLayoutInflater().inflate(R.layout.add_new_prescrip, null);
        bottomSheetDialog.setContentView(parentView);

        Button buttonPick = parentView.findViewById(R.id.buttonpick);
        textFile = parentView.findViewById(R.id.textfile);

        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent,PICKFILE_RESULT_CODE);

            }
        });



        bottomSheetDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    textFile.setText(FilePath);
                }
                break;

        }
    }
}