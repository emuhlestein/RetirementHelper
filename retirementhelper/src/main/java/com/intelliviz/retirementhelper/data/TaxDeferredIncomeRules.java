package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by edm on 10/18/2017.
 */

public class TaxDeferredIncomeRules implements IncomeTypeRules {
    private AgeData mMinAge;
    private AgeData mCurrentAge;
    private AgeData mEndAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private double mPenalty;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    public TaxDeferredIncomeRules(String birthDate, AgeData endAge, double balance,
                                  double interest, double monthlyIncrease, int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mMinAge = new AgeData(59, 6);
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mPenalty = 0.10;
        mBalance = balance;
        mEndAge = endAge;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
    }

    public MilestoneData getMilestone(AgeData age) {
        if(age.isBefore(mCurrentAge)) {
            return null;
        }
        int numMonths =  age.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = BalanceUtils.getMonthlyAmount(futureBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData =
                BalanceUtils.getMilestoneData(age, mEndAge, mInterest, futureBalance, monthlyAmount, mWithdrawMode, mWithdrawAmount);

        if(age.isBefore(mMinAge)) {

        }

        return milestoneData;
    }

    @Override
    public List<AgeData> getAges() {
        return new ArrayList<>(Arrays.asList(mMinAge));
    }

    public TaxDeferredData getMonthlyBenefitForAge(AgeData startAge) {
        if(startAge.isBefore(mCurrentAge)) {
            return null;
        }

        int numMonths =  startAge.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        return new TaxDeferredData(startAge, futureBalance, mWithdrawAmount, mWithdrawMode);
    }
}
