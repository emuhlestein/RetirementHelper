package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.GovPensionEx;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.income.data.GovPensionViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.repo.GovEntityRepo;

import java.util.List;


/**
 * Created by Ed Muhlestein on 10/16/2017.
 */

public class GovPensionIncomeViewModel extends AndroidViewModel {
    private LiveData<GovPensionViewData> mViewData = new MutableLiveData<>();
    private GovEntityRepo mRepo;
    private LiveData<GovPensionEx> mSource;
    private RetirementOptions mRO;

    private static long mIncomeId;

    public GovPensionIncomeViewModel(Application application,
                                     GovEntityRepo govRepo,
                                     long incomeId) {
        super(application);
        mRepo = govRepo;
        mSource = mRepo.getGovPensionDataEx();
        subscribe(incomeId);
        mIncomeId = incomeId;
        //mRepo.load();
    }

    private void subscribe(final long id) {
        mViewData = Transformations.switchMap(mSource,
                new Function<GovPensionEx, LiveData<GovPensionViewData>>() {
                    @Override
                    public LiveData<GovPensionViewData> apply(GovPensionEx input) {
                        List<GovPensionEntity> gpeList = input.getGpeList();
                        mRO = RetirementOptionsMapper.map(input.getROE());
                        GovPensionHelper helper = new GovPensionHelper(getApplication(), gpeList, mRO);
                        MutableLiveData<GovPensionViewData> ldata = new MutableLiveData();
                        ldata.setValue(helper.get(id));
                        return ldata;
                    }
                });
    }

    public LiveData<GovPensionViewData> get() {
        return mViewData;
    }

    public void setData(GovPension gp) {
        mRepo.setData(GovPensionEntityMapper.map(gp));
    }

    public void update() {
        mRepo.load();
    }

    public void updateSpouseBirthdate(String birthdate) {
        mRepo.updateSpouseBirthdate(birthdate);
    }

    public AgeData getFRA(GovPension gp) {
        String birthdate;
        if(gp.getOwner() == RetirementConstants.OWNER_PRIMARY) {
            birthdate = mRO.getPrimaryBirthdate();
        } else {
            birthdate = mRO.getSpouseBirthdate();
        }

        int year = AgeUtils.getBirthYear(birthdate);
        return SocialSecurityRules.getFullRetirementAgeFromYear(year);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;
        private GovEntityRepo mRepo;

        public Factory(@NonNull Application application, GovEntityRepo repo, long incomeId) {
            mApplication = application;
            mRepo = repo;
            mIncomeId = incomeId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new GovPensionIncomeViewModel(mApplication, mRepo, mIncomeId);
        }
    }
}
