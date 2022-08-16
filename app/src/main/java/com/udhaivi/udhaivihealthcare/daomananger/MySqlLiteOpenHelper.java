package com.udhaivi.udhaivihealthcare.daomananger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.udhaivi.udhaivihealthcare.dao.DaoMaster;
import com.udhaivi.udhaivihealthcare.dao.EcgHistoryDataDao;
import com.udhaivi.udhaivihealthcare.dao.HeartDataDao;
import com.udhaivi.udhaivihealthcare.dao.SleepDataDao;
import com.udhaivi.udhaivihealthcare.dao.StepDataDao;
import com.udhaivi.udhaivihealthcare.dao.StepDetailDataDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Administrator on 2017/5/19.
 */

public class MySqlLiteOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = "MySqlLiteOpenHelper";
    public MySqlLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MySqlLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i(TAG,"oldVersion:"+oldVersion+",newVersion"+newVersion);
        MigrationHelper.getInstance().migrate(db, StepDataDao.class, StepDetailDataDao.class,
              HeartDataDao.class, SleepDataDao.class, EcgHistoryDataDao.class);

    }
}
