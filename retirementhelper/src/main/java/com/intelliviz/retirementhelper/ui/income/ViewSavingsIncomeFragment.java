package com.intelliviz.retirementhelper.ui.income;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.adapter.MilestoneDataAdapter;
import com.intelliviz.retirementhelper.data.MilestoneAgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.RetirementOptionsData;
import com.intelliviz.retirementhelper.data.SavingsIncomeData;
import com.intelliviz.retirementhelper.db.RetirementContract;
import com.intelliviz.retirementhelper.ui.MilestoneDetailsDialog;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.RetirementOptionsHelper;
import com.intelliviz.retirementhelper.util.SavingsIncomeHelper;
import com.intelliviz.retirementhelper.util.SelectionMilestoneDataListener;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.intelliviz.retirementhelper.ui.income.ViewTaxDeferredIncomeFragment.ID_ARGS;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;

/**
 * Fragment used for viewing savings income sources.
 *
 * @author Ed Muhlestein
 */
public class ViewSavingsIncomeFragment extends Fragment implements
        SelectionMilestoneDataListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    public static final String VIEW_SAVINGS_INCOME_FRAG_TAG = "view savings income frag tag";
    private static final String EXTRA_INTENT = "extra intent";
    public static final int ROD_LOADER = 0;
    public static final int SID_LOADER = 1;
    private SavingsIncomeData mSID;
    private RetirementOptionsData mROD;
    private long mId;
    private MilestoneDataAdapter mMilestoneDataAdapter;

    @Bind(R.id.name_text_view) TextView mIncomeSourceName;
    @Bind(R.id.annual_interest_text_view) TextView mAnnualInterest;
    @Bind(R.id.monthly_increase_text_view) TextView mMonthlyIncrease;
    @Bind(R.id.current_balance_text_view) TextView mCurrentBalance;
    @Bind(R.id.monthly_amount_text_view) TextView mMonthlyAmount;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;

    public static ViewSavingsIncomeFragment newInstance(Intent intent) {
        ViewSavingsIncomeFragment fragment = new ViewSavingsIncomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_INTENT, intent);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewSavingsIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Intent intent = getArguments().getParcelable(EXTRA_INTENT);
            mId = -1;
            if(intent != null) {
                mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, -1);
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_savings_income, container, false);
        ButterKnife.bind(this, view);
        List<MilestoneData> milestones = new ArrayList<>();
        mMilestoneDataAdapter = new MilestoneDataAdapter(getContext(), milestones);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMilestoneDataAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()));
        mMilestoneDataAdapter.setOnSelectionMilestoneDataListener(this);

        setHasOptionsMenu(true);

        mROD = null;
        mSID = null;
        Bundle bundle = new Bundle();
        bundle.putString(ID_ARGS, Long.toString(mId));
        getLoaderManager().initLoader(ROD_LOADER, null, this);
        getLoaderManager().initLoader(SID_LOADER, bundle, this);

        return view;
    }

    private void updateUI() {
        if(mSID == null) {
            return;
        }

        mIncomeSourceName.setText(mSID.getName());
        String subTitle = SystemUtils.getIncomeSourceTypeString(getContext(), mSID.getType());
        SystemUtils.setToolbarSubtitle(getActivity(), subTitle);

        String interest = mSID.getInterest() + "%";
        mAnnualInterest.setText(interest);
        mMonthlyIncrease.setText(SystemUtils.getFormattedCurrency(mSID.getMonthlyIncrease()));

        double balance = mSID.getBalance();
        String formattedAmount = SystemUtils.getFormattedCurrency(balance);

        mCurrentBalance.setText(String.valueOf(formattedAmount));
    }

    @Override
    public void onSelectMilestone(MilestoneData msd) {
        Intent intent = new Intent(getContext(), MilestoneDetailsDialog.class);
        intent.putExtra(RetirementConstants.EXTRA_MILESTONEDATA, msd);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader;
        Uri uri;
        switch (loaderId) {
            case ROD_LOADER:
                uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
                loader = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                        null);
                break;
            case SID_LOADER:
                String id = args.getString(ID_ARGS);
                uri = RetirementContract.SavingsIncomeEntry.CONTENT_URI;
                if(uri != null) {
                    uri = Uri.withAppendedPath(uri, id);
                }

                loader = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                        null);
                break;
            default:
                loader = null;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case ROD_LOADER:
                mROD = RetirementOptionsHelper.extractData(cursor);
                break;
            case SID_LOADER:
                mSID = SavingsIncomeHelper.extractData(cursor);
                updateUI();
                break;
        }

        if(mROD != null && mSID != null) {
            List<MilestoneAgeData> ages = DataBaseUtils.getMilestoneAges(getContext(), mROD);
            List<MilestoneData> milestones = mSID.getMilestones(getContext(), ages, mROD);
            mMilestoneDataAdapter.update(milestones);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
