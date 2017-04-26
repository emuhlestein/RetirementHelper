package com.intelliviz.retirementhelper.ui.income;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelliviz.retirementhelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditIncomeSourceFragment extends Fragment {
    public static final String EDIT_INCOME_FRAG_TAG = "edit income frag tag";

    public static EditIncomeSourceFragment newInstance() {
        EditIncomeSourceFragment fragment = new EditIncomeSourceFragment();
        return fragment;
    }

    public EditIncomeSourceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_income_source, container, false);
    }

}
