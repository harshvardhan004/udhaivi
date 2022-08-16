package com.udhaivi.udhaivihealthcare.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.udhaivi.udhaivihealthcare.model.EcgHistoryData;
import com.udhaivi.udhaivihealthcare.model.HeartData;
import com.udhaivi.udhaivihealthcare.model.SleepData;
import com.udhaivi.udhaivihealthcare.model.StepData;
import com.udhaivi.udhaivihealthcare.model.StepDetailData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig ecgHistoryDataDaoConfig;
    private final DaoConfig heartDataDaoConfig;
    private final DaoConfig sleepDataDaoConfig;
    private final DaoConfig stepDataDaoConfig;
    private final DaoConfig stepDetailDataDaoConfig;

    private final EcgHistoryDataDao ecgHistoryDataDao;
    private final HeartDataDao heartDataDao;
    private final SleepDataDao sleepDataDao;
    private final StepDataDao stepDataDao;
    private final StepDetailDataDao stepDetailDataDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        ecgHistoryDataDaoConfig = daoConfigMap.get(EcgHistoryDataDao.class).clone();
        ecgHistoryDataDaoConfig.initIdentityScope(type);

        heartDataDaoConfig = daoConfigMap.get(HeartDataDao.class).clone();
        heartDataDaoConfig.initIdentityScope(type);

        sleepDataDaoConfig = daoConfigMap.get(SleepDataDao.class).clone();
        sleepDataDaoConfig.initIdentityScope(type);

        stepDataDaoConfig = daoConfigMap.get(StepDataDao.class).clone();
        stepDataDaoConfig.initIdentityScope(type);

        stepDetailDataDaoConfig = daoConfigMap.get(StepDetailDataDao.class).clone();
        stepDetailDataDaoConfig.initIdentityScope(type);

        ecgHistoryDataDao = new EcgHistoryDataDao(ecgHistoryDataDaoConfig, this);
        heartDataDao = new HeartDataDao(heartDataDaoConfig, this);
        sleepDataDao = new SleepDataDao(sleepDataDaoConfig, this);
        stepDataDao = new StepDataDao(stepDataDaoConfig, this);
        stepDetailDataDao = new StepDetailDataDao(stepDetailDataDaoConfig, this);

        registerDao(EcgHistoryData.class, ecgHistoryDataDao);
        registerDao(HeartData.class, heartDataDao);
        registerDao(SleepData.class, sleepDataDao);
        registerDao(StepData.class, stepDataDao);
        registerDao(StepDetailData.class, stepDetailDataDao);
    }
    
    public void clear() {
        ecgHistoryDataDaoConfig.clearIdentityScope();
        heartDataDaoConfig.clearIdentityScope();
        sleepDataDaoConfig.clearIdentityScope();
        stepDataDaoConfig.clearIdentityScope();
        stepDetailDataDaoConfig.clearIdentityScope();
    }

    public EcgHistoryDataDao getEcgHistoryDataDao() {
        return ecgHistoryDataDao;
    }

    public HeartDataDao getHeartDataDao() {
        return heartDataDao;
    }

    public SleepDataDao getSleepDataDao() {
        return sleepDataDao;
    }

    public StepDataDao getStepDataDao() {
        return stepDataDao;
    }

    public StepDetailDataDao getStepDetailDataDao() {
        return stepDetailDataDao;
    }

}