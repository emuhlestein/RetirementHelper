package com.intelliviz.income.viewmodel;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDetails;
import com.intelliviz.data.IncomeTypeRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.income.data.SavingsViewData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.income.ui.MessageMgr.EC_FOR_SELF_OR_SPOUSE;
import static com.intelliviz.income.ui.MessageMgr.EC_NO_ERROR;

public abstract class AbstractSavingsIncomeHelper {
    private SavingsData mSD;
    private RetirementOptions mRO;

    public AbstractSavingsIncomeHelper(SavingsData sd, RetirementOptions ro) {
        mSD = sd;
        mRO = ro;
    }

    public abstract boolean canCreateNewIncomeSource();
    public abstract int getOnlyOneSupportedErrorCode();
    public abstract String getOnlyOneSupportedErrorMessage();
    public abstract boolean isSpouseIncluded();

    public SavingsViewData get(final long id, final int incomeType) {
        if (id == 0) {
            if (canCreateNewIncomeSource()) {
                // create default pension income source
                return createDefault(incomeType);
            } else {
                return new SavingsViewData(null, Collections.<IncomeDetails>emptyList(), isSpouseIncluded(), getOnlyOneSupportedErrorCode(), getOnlyOneSupportedErrorMessage());
            }
        } else {
            IncomeTypeRules sr;
            if(mSD.getType() == RetirementConstants.INCOME_TYPE_401K) {
                sr = new Savings401kIncomeRules(mRO, true);
            } else {
                sr = new SavingsIncomeRules(mRO, true);
            }
            mSD.setRules(sr);
            AgeData age = AgeUtils.getAge(mRO.getPrimaryBirthdate());
            AgeData endAge = mRO.getEndAge();

            List<IncomeData> incomeDataList = new ArrayList<>();
            for(int year = age.getYear(); year <= endAge.getYear(); year++) {
                IncomeData benefitData = sr.getIncomeData(new AgeData(year, 0));
                if(benefitData != null) {
                    incomeDataList.add(benefitData);
                }
            }
            List<IncomeDetails> incomeDetails = getIncomeDetailsList(incomeDataList, mRO);

            return new SavingsViewData(mSD, incomeDetails, isSpouseIncluded(), EC_NO_ERROR, "");
        }
    }

    private SavingsViewData createDefault(final int incomeType) {
        SavingsData sd = new SavingsData(0, incomeType, "", RetirementConstants.OWNER_PRIMARY, 1,
                new AgeData(65, 0), "0", "0", "0", new AgeData(65, 0), "0", "0");
        IncomeTypeRules sr;

        int status = EC_NO_ERROR;
        if(isSpouseIncluded()) {
            status = EC_FOR_SELF_OR_SPOUSE;
        }

        if(incomeType == RetirementConstants.INCOME_TYPE_401K) {
            sr = new Savings401kIncomeRules(mRO, true);
        } else {
            sr = new SavingsIncomeRules(mRO, true);
        }
        sd.setRules(sr);
        return new SavingsViewData(sd, Collections.<IncomeDetails>emptyList(), isSpouseIncluded(), status, "");
    }

    private List<IncomeDetails> getIncomeDetailsList(List<IncomeData> incomeDataList, RetirementOptions ro) {
//        List<IncomeData> incomeDataList = IncomeSummaryHelper.getIncomeSummary(incomeSourceList, ro);
//        if(incomeDataList == null) {
//            return Collections.emptyList();
//        }

        List<IncomeDetails> incomeDetails = new ArrayList<>();

        for (IncomeData benefitData : incomeDataList) {
            AgeData age = benefitData.getAge();
            String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
            String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
            String line1 = age.toString() + "   " + amount + "  " + balance;
            IncomeDetails incomeDetail = new IncomeDetails(line1, RetirementConstants.BALANCE_STATE_GOOD, "");
            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }

    // TODO make utils method
    private List<IncomeDetails> getIncomeDetailsList(List<IncomeData> incomeDataList) {
        List<IncomeDetails> incomeDetails = new ArrayList<>();
/*
        for (IncomeData benefitData : incomeDataList) {
            AgeData age = benefitData.getAge();
            String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
            String balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
            String line1 = age.toString() + "   " + amount + "  " + balance;
            int benefitInfo = BI_GOOD;
            String message = "";
            if(benefitData.isPenalty()) {
                benefitInfo |= BI_PENALTY;
                message = "There is a 10% penalty for early withdrawal.";
            }
            if(benefitData.getState() == BALANCE_STATE_LOW) {
                benefitInfo |= BI_LOW_BALANCE;
                message = "Balance will be exhausted in less than a year";
            } else if(benefitData.getState() == BALANCE_STATE_EXHAUSTED) {
                benefitInfo |= BI_EXHAUSTED_BALANCE;
                message = "Balance has been exhausted. Need to increase savings, reduce initial monthly withdraw or delay retirement.";
            }

            IncomeDetails incomeDetail = new IncomeDetails(line1, benefitInfo, message);
            incomeDetails.add(incomeDetail);
        }
*/
        return incomeDetails;
    }
}
