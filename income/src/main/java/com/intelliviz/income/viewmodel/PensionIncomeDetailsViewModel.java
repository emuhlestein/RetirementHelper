package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionDataEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.PensionViewData;

import java.util.List;

public class PensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<PensionViewData> mViewData = new MutableLiveData<>();
    private AppDatabase mDB;
    private long mId;

    public PensionIncomeDetailsViewModel(@NonNull Application application, long id) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        mId = id;
        new GetExAsyncTask().execute(id);
    }

    public LiveData<PensionViewData> get() {
        return mViewData;
    }

    public void update() {
        new GetExAsyncTask().execute(mId);
    }

    private class GetExAsyncTask extends AsyncTask<Long, Void, PensionDataEx> {

        @Override
        protected PensionDataEx doInBackground(Long... params) {
            PensionIncomeEntity pie = mDB.pensionIncomeDao().get(params[0]);
            List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            return new PensionDataEx(pie, pieList.size(), roe);
        }

        @Override
        protected void onPostExecute(PensionDataEx pdEx) {
            RetirementOptions ro = RetirementOptionsMapper.map(pdEx.getROE());
            PensionData pd = null;
            if(pdEx.getPie() != null) {
                pd = PensionDataEntityMapper.map(pdEx.getPie());
            }
            PensionIncomeHelper helper = new PensionIncomeHelper(pd, ro, pdEx.getNumRecords());
            long id = 0;
            if(pdEx.getPie() != null) {
                id = pdEx.getPie().getId();
            }
            mViewData.setValue(helper.get(id));
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;

        public Factory(@NonNull Application application, long incomeId) {
            mApplication = application;
            mIncomeId = incomeId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new PensionIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }
}
