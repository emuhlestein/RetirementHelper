package com.intelliviz.data;

import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.RetirementOptionsEntity;

import java.util.List;

public class IncomeSummaryData {
    private RetirementOptionsEntity mROE;
    private List<IncomeSourceEntityBase> mIncomeSourceList;

    public IncomeSummaryData(RetirementOptionsEntity ROE, List<IncomeSourceEntityBase> incomeSourceList) {
        mROE = ROE;
        mIncomeSourceList = incomeSourceList;
    }

    public RetirementOptionsEntity getROE() {
        return mROE;
    }

    public List<IncomeSourceEntityBase> getIncomeSourceList() {
        return mIncomeSourceList;
    }
}
