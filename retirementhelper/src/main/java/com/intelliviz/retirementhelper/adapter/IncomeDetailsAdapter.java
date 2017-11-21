package com.intelliviz.retirementhelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.data.IncomeDetails;

import java.util.List;

/**
 * Created by edm on 11/16/2017.
 */

public class IncomeDetailsAdapter extends RecyclerView.Adapter<IncomeDetailsAdapter.GovPensionHolder>{
    private List<IncomeDetails> mIncomeDetails;
    private Context mContext;
    private int mNumLines;

    public IncomeDetailsAdapter(Context context, List<IncomeDetails> incomeDetails) {
        mContext = context;
        mIncomeDetails = incomeDetails;
    }

    @Override
    public IncomeDetailsAdapter.GovPensionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(mNumLines == 1) {
            view = inflater.inflate(R.layout.card_layout1, parent, false);
        } else {
            view = inflater.inflate(R.layout.card_layout, parent, false);
        }
        return new IncomeDetailsAdapter.GovPensionHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomeDetailsAdapter.GovPensionHolder holder, int position) {
        IncomeDetails incomeDetails = mIncomeDetails.get(position);
        holder.bindIncomeDetails(incomeDetails);
    }

    @Override
    public int getItemCount() {
        if(mIncomeDetails != null) {
            return mIncomeDetails.size();
        } else {
            return 0;
        }
    }

    public void update(List<IncomeDetails> incomeDetails) {
        if(incomeDetails != null || incomeDetails.isEmpty()) {
            mNumLines = incomeDetails.get(0).getNumLines();
            mIncomeDetails.clear();
            mIncomeDetails.addAll(incomeDetails);
            notifyDataSetChanged();
        }
    }

    class GovPensionHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mLine1TextView;
        private TextView mLine2TextView;
        private TextView mLine3TextView;
        private android.support.v7.widget.CardView mCardView;

        public GovPensionHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card_view);
            if(mNumLines == 1) {
                mLine1TextView = itemView.findViewById(R.id.line1);
            } else {
                mLine1TextView = itemView.findViewById(R.id.line1);
                mLine2TextView = itemView.findViewById(R.id.line2);
                mLine3TextView = itemView.findViewById(R.id.line3);
            }
            itemView.setOnClickListener(this);
        }

        private void bindIncomeDetails(IncomeDetails incomeDetails) {

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if(incomeDetails.getBenefitInfo() == 0) {
                    mCardView.setCardBackgroundColor( mContext.getResources().getColor(R.color.card_red) );
                } else {
                    if(incomeDetails.getBenefitInfo() == 1) {
                        mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_yellow));
                    } else {
                        mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_green));
                    }
                }
            } else {
                if(incomeDetails.getBenefitInfo() == 0) {
                    mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_red));
                } else {
                    if(incomeDetails.getBenefitInfo() == 1) {
                        mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_yellow));
                    } else {
                        mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.card_green));
                    }
                }
            }

            if(incomeDetails.getNumLines() == 1) {
                mLine1TextView.setText(incomeDetails.getLine1());
            } else {
                mLine1TextView.setText(incomeDetails.getLine1());
                mLine2TextView.setText(incomeDetails.getLine2());
                mLine3TextView.setText(incomeDetails.getLine3());
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
