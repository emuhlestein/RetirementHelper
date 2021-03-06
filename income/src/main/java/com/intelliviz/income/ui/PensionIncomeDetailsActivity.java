package com.intelliviz.income.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelliviz.data.PensionData;
import com.intelliviz.income.R;
import com.intelliviz.income.data.PensionViewData;
import com.intelliviz.income.viewmodel.PensionIncomeDetailsViewModel;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.INCOME_TYPE_PENSION;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;


public class PensionIncomeDetailsActivity extends AppCompatActivity {
    private PensionIncomeDetailsViewModel mViewModel;
    private PensionData mPD;
    private long mId;
    private boolean mSpouseIncluded;
    private TextView mNameTextView;
    private TextView mStartAgeTextView;
    private TextView mMonthlyBenefitTextView;
    private LinearLayout mExpandedTextLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mOwnerTextView;
    private TextView mIncomeSourceIncluded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pension_income_details);

        android.support.design.widget.AppBarLayout mAppBarLayout = findViewById(R.id.appbar);
        mNameTextView = findViewById(R.id.name_text_view);
        mStartAgeTextView = findViewById(R.id.min_age_text_view);
        mMonthlyBenefitTextView = findViewById(R.id.monthly_benefit_text_view);
        mExpandedTextLayout = findViewById(R.id.expanded_text_layout);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        FloatingActionButton editPensionFAB = findViewById(R.id.editPensionFAB);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mOwnerTextView = findViewById(R.id.owner_text);
        mIncomeSourceIncluded = findViewById(R.id.included_text_view);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
        }

        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    mExpandedTextLayout.setVisibility(View.GONE);
                    mCollapsingToolbarLayout.setTitle(getApplicationName(PensionIncomeDetailsActivity.this));
                } else {
                    isShow = false;
                    mExpandedTextLayout.setVisibility(View.VISIBLE);
                    mCollapsingToolbarLayout.setTitle("");
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // The FAB will pop up an activity to allow a new income source to be edited
        editPensionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PensionIncomeDetailsActivity.this, PensionIncomeEditActivity.class);
                intent.putExtra(EXTRA_INCOME_SOURCE_ID, mId);
                intent.putExtra(RetirementConstants.EXTRA_ACTIVITY_RESULT, RetirementConstants.ACTIVITY_RESULT);
                startActivityForResult(intent, RetirementConstants.ACTIVITY_RESULT);
            }
        });

        PensionIncomeDetailsViewModel.Factory factory = new
                PensionIncomeDetailsViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(PensionIncomeDetailsViewModel.class);

        mViewModel.get().observe(this, new Observer<PensionViewData>() {
            @Override
            public void onChanged(@Nullable PensionViewData viewData) {
                if(viewData == null) {
                    return;
                }
                mSpouseIncluded = viewData.isSpouseIncluded();
                mPD = viewData.getPensionData();
                updateUI();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.update();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == RetirementConstants.ACTIVITY_RESULT) {
            Bundle bundle = intent.getExtras();
            if(bundle == null) {
                return;
            }
            String name = bundle.getString(RetirementConstants.EXTRA_INCOME_SOURCE_NAME);
            AgeData minAge = bundle.getParcelable(RetirementConstants.EXTRA_INCOME_SOURCE_START_AGE);
            String monthlyBenefit = bundle.getString(RetirementConstants.EXTRA_INCOME_SOURCE_BENEFIT);

            PensionData pd = new PensionData(mId, INCOME_TYPE_PENSION, name, mPD.getOwner(), mPD.getIncluded(),
                    minAge, monthlyBenefit);
            //mViewModel.setData(pd);

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void updateUI() {
        if(mPD == null) {
            return;
        }

        SystemUtils.setToolbarSubtitle(this, "Pension - " + mPD.getName());

        String name = mPD.getName();
        if(!mSpouseIncluded) {
            mOwnerTextView.setVisibility(View.GONE);
        } else if(mPD.getOwner() == OWNER_PRIMARY) {
            mOwnerTextView.setText(getResources().getString(R.string.self));
        } else if(mPD.getOwner() == OWNER_SPOUSE) {
            mOwnerTextView.setText(getResources().getString(R.string.spouse));
        }

        if(mPD.getIncluded() == 1) {
            mIncomeSourceIncluded.setText("Yes");
        } else {
            mIncomeSourceIncluded.setText("No");
        }

        mNameTextView.setText(name);

        AgeData age = mPD.getStartAge();
        mStartAgeTextView.setText(age.toString());

        String formattedValue = SystemUtils.getFormattedCurrency(mPD.getMonthlyBenefit());
        mMonthlyBenefitTextView.setText(formattedValue);
    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
