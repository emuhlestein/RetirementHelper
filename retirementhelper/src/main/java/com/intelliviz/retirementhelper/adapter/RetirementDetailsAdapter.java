package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.data.IncomeData;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.SystemUtils;
import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.ui.SelectAmountListener;

import java.util.List;

/**
 * Created by edm on 11/1/2017.
 */

public class RetirementDetailsAdapter extends
        RecyclerView.Adapter<RetirementDetailsAdapter.AmountDataHolder>{
    private Context mContext;
    private List<IncomeData> mBenefitData;
    private SelectAmountListener mListener;

    public RetirementDetailsAdapter(Context context, List<IncomeData> benefitData) {
        mContext = context;
        mBenefitData = benefitData;
    }

    @Override
    public AmountDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.milestone_item_layout, parent, false);
        return new AmountDataHolder(view);
    }

    @Override
    public void onBindViewHolder(AmountDataHolder holder, int position) {
        holder.bindAmount(mBenefitData.get(position));
    }

    @Override
    public int getItemCount() {
        if(mBenefitData != null) {
            return mBenefitData.size();
        } else {
            return 0;
        }
    }

    public void setOnSelectAmountListener(SelectAmountListener listener) {
        mListener = listener;
    }

    public void update(List<IncomeData> benefitData) {
        mBenefitData.clear();
        mBenefitData.addAll(benefitData);
        notifyDataSetChanged();
    }

    class AmountDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mMilestoneTextView;
        private TextView mMonthlyAmountTextView;
        private LinearLayout mLinearLayout;
        private IncomeData mAmount;

        private AmountDataHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.milestone_item_layout);
            mMilestoneTextView = itemView.findViewById(R.id.milestone_text_view);
            mMonthlyAmountTextView = itemView.findViewById(R.id.monthly_amount_text_view);
            itemView.setOnClickListener(this);
        }

        private void bindAmount(IncomeData benefitData) {
            mAmount = benefitData;
            double amount = mAmount.getMonthlyAmount();
            AgeData age = mAmount.getAge();

            String formattedCurrency = SystemUtils.getFormattedCurrency(amount);
            mMonthlyAmountTextView.setText(formattedCurrency);
            mMilestoneTextView.setText(AgeUtils.getFormattedAge(age));
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.onSelectAmount(mAmount);
            }
        }
    }
}
