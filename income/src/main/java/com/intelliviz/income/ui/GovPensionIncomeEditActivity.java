package com.intelliviz.income.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.intelliviz.data.GovPension;
import com.intelliviz.income.R;
import com.intelliviz.income.data.GovPensionViewData;
import com.intelliviz.income.viewmodel.GovPensionIncomeEditViewModel;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.ui.MessageDialog;
import com.intelliviz.lowlevel.ui.NewMessageDialog;
import com.intelliviz.lowlevel.util.RetirementConstants;
import com.intelliviz.lowlevel.util.SystemUtils;

import static com.intelliviz.income.ui.MessageMgr.EC_FOR_SELF_OR_SPOUSE;
import static com.intelliviz.income.ui.MessageMgr.EC_NO_ERROR;
import static com.intelliviz.income.ui.MessageMgr.EC_NO_SPOUSE_BIRTHDATE;
import static com.intelliviz.income.ui.MessageMgr.EC_ONLY_ONE_SOCIAL_SECURITY_ALLOWED;
import static com.intelliviz.income.ui.MessageMgr.EC_ONLY_TWO_SOCIAL_SECURITY_ALLOWED;
import static com.intelliviz.income.ui.MessageMgr.EC_PRINCIPLE_SPOUSE;
import static com.intelliviz.income.ui.MessageMgr.EC_SPOUSE_NOT_SUPPORTED;
import static com.intelliviz.income.util.uiUtils.getIncomeSourceTypeString;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_ID;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_MESSAGE_MGR;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_SPOUSE;
import static com.intelliviz.lowlevel.util.RetirementConstants.REQUEST_SPOUSE_BIRTHDATE;
import static com.intelliviz.lowlevel.util.SystemUtils.getFloatValue;


public class GovPensionIncomeEditActivity extends AppCompatActivity implements
        AgeDialog.OnAgeEditListener, MessageDialog.DialogResponse, NewMessageDialog.DialogResponse, BirthdateDialog.BirthdateDialogListener {

    private GovPension mGP;
    private long mId;
    private GovPensionIncomeEditViewModel mViewModel;
    private boolean mIsPrincipleSpouse;
    private boolean mSpouseIncluded;

    private CoordinatorLayout mCoordinatorLayout;
    private EditText mName;
    private TextView mPrincipleSpouseLabel;
    private TextView mFullRetirementAge;
    private EditText mFullMonthlyBenefit;
    private Toolbar mToolbar;
    private Button mAddIncomeSource;
    private CheckBox mIncomeSourceIncluded;
    private TextView mOwnerTextView;
    private MessageMgr mMessageMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gov_pension_income);

        mOwnerTextView = findViewById(R.id.owner_text);

        mIncomeSourceIncluded = findViewById(R.id.include_income_source);

        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mName = findViewById(R.id.name_edit_text);
        mPrincipleSpouseLabel = findViewById(R.id.principle_spouse_label);
        mFullRetirementAge = findViewById(R.id.full_retirement_age_text_view);
        mFullMonthlyBenefit = findViewById(R.id.full_monthly_benefit_edit_text);
        mToolbar = findViewById(R.id.income_source_toolbar);
        mFullRetirementAge = findViewById(R.id.full_retirement_age_text_view);
        mAddIncomeSource = findViewById(R.id.add_income_source_button);
        mAddIncomeSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIncomeSourceData();
            }
        });

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mId = 0;
        if(intent != null) {
            mId = intent.getLongExtra(EXTRA_INCOME_SOURCE_ID, 0);
            mMessageMgr = intent.getParcelableExtra(EXTRA_MESSAGE_MGR);
        }

        mFullMonthlyBenefit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    TextView textView = (TextView)v;
                    String str = textView.getText().toString();
                    String value = getFloatValue(str);
                    String formattedString = SystemUtils.getFormattedCurrency(value);
                    if(formattedString != null) {
                        mFullMonthlyBenefit.setText(formattedString);
                    }
                }
            }
        });

        GovPensionIncomeEditViewModel.Factory factory = new
                GovPensionIncomeEditViewModel.Factory(getApplication(), mId);
        mViewModel = ViewModelProviders.of(this, factory).
                get(GovPensionIncomeEditViewModel.class);

        mViewModel.get().observe(this, new Observer<GovPensionViewData>() {
            @Override
            public void onChanged(@Nullable GovPensionViewData viewData) {
                if(viewData == null) {
                    return;
                }

                mSpouseIncluded = viewData.isSpouseIncluded();
                mGP = viewData.getGovPension();
                mIsPrincipleSpouse = false;
                String message;
                switch(viewData.getStatus()) {
                    case EC_ONLY_TWO_SOCIAL_SECURITY_ALLOWED:
                        final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, viewData.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                finish();
                            }
                        });
                        snackbar.show();
                        break;
                    case EC_NO_SPOUSE_BIRTHDATE:
                        FragmentManager fm = getSupportFragmentManager();
                        MessageDialog dialog = MessageDialog.newInstance("Social Security", "Adding a second social security income source is for a spouse." +
                                " In order to do this, the spouse's birth date must be added.\n\nClick Ok to add birth date.", EC_NO_SPOUSE_BIRTHDATE, false, null, null);
                        dialog.show(fm, "message");
                        break;
                    case EC_PRINCIPLE_SPOUSE:
                        mIsPrincipleSpouse = true;
                        break;
                    case EC_SPOUSE_NOT_SUPPORTED:
                        fm = getSupportFragmentManager();
                        dialog = MessageDialog.newInstance("Warning", viewData.getMessage(), viewData.getStatus(), true, null, null);
                        dialog.show(fm, "message");
                        break;
                    case EC_FOR_SELF_OR_SPOUSE:
                        fm = getSupportFragmentManager();
                        message = mMessageMgr.getMessage(viewData.getStatus());
                        NewMessageDialog newdialog = NewMessageDialog.newInstance(viewData.getStatus(), "Income Source", message, "Self", "Spouse");
                        newdialog.show(fm, "message");
                        break;
                    case EC_ONLY_ONE_SOCIAL_SECURITY_ALLOWED:
                        fm = getSupportFragmentManager();
                        message = mMessageMgr.getMessage(viewData.getStatus());
                        newdialog = NewMessageDialog.newInstance(viewData.getStatus(), "Income Source", message, "Ok");
                        newdialog.show(fm, "message");
                        break;
                    case EC_NO_ERROR:
                        break;
                }
                updateUI();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SPOUSE_BIRTHDATE:
                    String birthdate = intent.getStringExtra(RetirementConstants.EXTRA_BIRTHDATE);
                    //mViewModel.updateSpouseBirthdate(birthdate);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }

    private void updateUI() {
        if(mGP == null) {
            return;
        }

        if(!mSpouseIncluded) {
            mOwnerTextView.setVisibility(View.GONE);
        } else if(mGP.getOwner() == OWNER_PRIMARY) {
            mOwnerTextView.setText(getResources().getString(R.string.self));
        } else if(mGP.getOwner() == OWNER_SPOUSE) {
            mOwnerTextView.setText(getResources().getString(R.string.spouse));
        }

        if(mGP.getIncluded() == 1) {
            mIncomeSourceIncluded.setChecked(true);
        } else {
            mIncomeSourceIncluded.setChecked(false);
        }

        if(mIsPrincipleSpouse) {
            mPrincipleSpouseLabel.setVisibility(View.VISIBLE);
        } else {
            mPrincipleSpouseLabel.setVisibility(View.GONE);
        }

        mName.setText(mGP.getName());

        AgeData age = mGP.getFullRetirementAge();
        mFullRetirementAge.setText(age.toString());

        String monthlyBenefit = SystemUtils.getFormattedCurrency(mGP.getFullMonthlyBenefit());
        mFullMonthlyBenefit.setText(monthlyBenefit);

        age = mGP.getStartAge();
        setStartRetirementAge(age.toString());

        int type = mGP.getType();
        String incomeSourceTypeString = getIncomeSourceTypeString(this, type);
        SystemUtils.setToolbarSubtitle(this, incomeSourceTypeString);
    }

    private void updateIncomeSourceData() {
        String name = mName.getText().toString();
        String value = mFullMonthlyBenefit.getText().toString();
        int included = mIncomeSourceIncluded.isChecked() ? 1 : 0;
        String fullBenefit = getFloatValue(value);
        if (fullBenefit == null) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.monthly_benefit_not_valid) + value, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        AgeData startAge = getStartRetirementAge();

        GovPension gp = new GovPension(mGP.getId(), mGP.getType(), name, mGP.getOwner(), included,
                fullBenefit, startAge);
        mViewModel.setData(gp);

        finish();
    }

    @Override
    public void onEditAge(String year, String month) {
        AgeData age = new AgeData(year, month);
        setStartRetirementAge(age.toString());
    }

    private void showBirthdateDialog(String birthdate) {
        FragmentManager fm = getSupportFragmentManager();
        BirthdateDialog birthdateDialog = BirthdateDialog.getInstance(birthdate);
        birthdateDialog.show(fm, "birthdate");
    }

    private void setStartRetirementAge(String age) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.gov_pension_advanced_fragment);
        if(fragment != null && fragment instanceof GovPensionAdvancedFragment) {
            ((GovPensionAdvancedFragment)fragment).setStartRetirementAge(age.toString());
        }
    }

    private AgeData getStartRetirementAge() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.gov_pension_advanced_fragment);
        if(fragment != null && fragment instanceof GovPensionAdvancedFragment) {
            String age = ((GovPensionAdvancedFragment)fragment).getStartRetirementAge();
            AgeData startAge = new AgeData(age);
            if(startAge.isValid()) {
                return startAge;
            }
        }
        return new AgeData();
    }

    @Override
    public void onGetResponse(int response, int id, boolean isOk) {
        if (response == Activity.RESULT_OK) {
            switch (id) {
                case EC_NO_SPOUSE_BIRTHDATE:
                    // Launch add birthdate diadlog
                    showBirthdateDialog("01-01-1900");
                    break;
                case EC_SPOUSE_NOT_SUPPORTED:
                    finish();
                    break;
            }
        } else {
            // terminate activity
            finish();
        }
    }

    @Override
    public void onGetBirthdate(String birthdate) {
        if(birthdate == null) {
            finish();
        } else {
            //mViewModel.updateSpouseBirthdate(birthdate);
        }
    }

    @Override
    public void onGetResponse(int id, int button) {
        AgeData fra;
        switch (id) {
            case EC_ONLY_ONE_SOCIAL_SECURITY_ALLOWED:
                finish();
                break;
            case EC_FOR_SELF_OR_SPOUSE:
                if (button == NewMessageDialog.POS_BUTTON) {
                    mGP.setOwner(RetirementConstants.OWNER_PRIMARY);
                    fra = mViewModel.getFRA(mGP);
                    mFullRetirementAge.setText(fra.toString());
                } else if(button == NewMessageDialog.NEG_BUTTON) {
                    mGP.setOwner(RetirementConstants.OWNER_SPOUSE);
                    fra = mViewModel.getFRA(mGP);
                    mFullRetirementAge.setText(fra.toString());
                }
                break;
        }
    }
}
