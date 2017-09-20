package com.intelliviz.retirementhelper.ui.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.intelliviz.retirementhelper.R;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GovPensionIncomeActivity extends AppCompatActivity {

    @Bind(R.id.income_source_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gov_pension_income);


        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        int mIncomeSourceType = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_TYPE, RetirementConstants.INCOME_TYPE_SAVINGS);
        int mIncomeSourceAction = intent.getIntExtra(RetirementConstants.EXTRA_INCOME_SOURCE_ACTION, RetirementConstants.INCOME_ACTION_VIEW);

        if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_ADD) {
            addIncomeSourceFragment(false, intent);
        } else {
            // View or edit an income source
            if(mIncomeSourceAction == RetirementConstants.INCOME_ACTION_EDIT) {
                // View or edit an income source
                addIncomeSourceFragment(false, intent);
            } else {
                addIncomeSourceFragment(true, intent);
            }
        }
    }

    private void addIncomeSourceFragment(boolean viewMode, Intent intent) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft;
        Fragment fragment;

        if (viewMode) {
            fragment = fm.findFragmentByTag(ViewGovPensionIncomeFragment.VIEW_GOV_PENSION_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = ViewGovPensionIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, ViewGovPensionIncomeFragment.VIEW_GOV_PENSION_INCOME_FRAG_TAG);
                ft.commit();
            }
        } else {
            fragment = fm.findFragmentByTag(EditGovPensionIncomeFragment.EDIT_GOVPENSION_INCOME_FRAG_TAG);
            if (fragment == null) {
                fragment = EditGovPensionIncomeFragment.newInstance(intent);
                ft = fm.beginTransaction();
                ft.add(R.id.content_frame, fragment, EditGovPensionIncomeFragment.EDIT_GOVPENSION_INCOME_FRAG_TAG);
                ft.commit();
            }
        }
    }
}
