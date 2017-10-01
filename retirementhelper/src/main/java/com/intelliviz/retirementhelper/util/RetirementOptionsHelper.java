package com.intelliviz.retirementhelper.util;

/**
 * Helper class for retirement options.
 * Created by Ed Muhlestein on 6/28/2017.
 */

public class RetirementOptionsHelper {
    /*
    public static String addAge(Context context, AgeData age) {
        ContentValues values = new ContentValues();
        values.put(RetirementContract.MilestoneEntry.COLUMN_AGE, age.getUnformattedString());
        Uri uri = context.getContentResolver().insert(RetirementContract.MilestoneEntry.CONTENT_URI, values);
        if(uri == null) {
            return null;
        } else {
            return uri.getLastPathSegment();
        }
    }

    public static int deleteAge(Context context, long id) {
        if(id == -1) {
            return 0;
        }
        String sid = String.valueOf(id);
        Uri uri = RetirementContract.MilestoneEntry.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, sid);
        return context.getContentResolver().delete(uri, null, null);
    }
    */
/*
    public static RetirementOptionsData getRetirementOptionsData(Context context) {
        Cursor cursor = getRetirementOptions(context);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int birthdateIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE);
        int endAgeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE);
        int withdrawModeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE);
        int withdrawAmountIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT);


        String birthdate = "01-01-1970";
        if(birthdateIndex != -1) {
            birthdate = cursor.getString(birthdateIndex);
        };
        String endAge = cursor.getString(endAgeIndex);
        int withdrawMode = cursor.getInt(withdrawModeIndex);
        String withdrawAmount = cursor.getString(withdrawAmountIndex);
        if(withdrawAmount.equals("")) {
            withdrawAmount = "4";
        }
        cursor.close();
        return new RetirementOptionsData(birthdate, endAge, withdrawMode, withdrawAmount);
    }
    */

/*
    public static int saveBirthdate(Context context, String birthdate) {
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE, birthdate);
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }

    //
    // Methods for retirement options table
    //
    public static int saveRetirementOptions(Context context, RetirementOptionsData rod) {
        saveBirthdate(context, rod.getBirthdate());
        ContentValues values  = new ContentValues();
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE, rod.getBirthdate());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE, rod.getEndAge());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE, rod.getWithdrawMode());
        values.put(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT, rod.getWithdrawAmount());
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().update(uri, values, null, null);
    }
    */
/*
    private static Cursor getRetirementOptions(Context context) {
        Uri uri = RetirementContract.RetirementParmsEntry.CONTENT_URI;
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static RetirementOptionsData extractData(Cursor cursor) {
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int birthdateIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_BIRTHDATE);
        int endAgeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_END_AGE);
        int withdrawModeIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_MODE);
        int withdrawAmountIndex = cursor.getColumnIndex(RetirementContract.RetirementParmsEntry.COLUMN_WITHDRAW_AMOUNT);

        String birthdate = cursor.getString(birthdateIndex);
        String endAge = cursor.getString(endAgeIndex);
        int withdrawMode = cursor.getInt(withdrawModeIndex);
        String withdrawAmount = cursor.getString(withdrawAmountIndex);

        return new RetirementOptionsData(birthdate, endAge, withdrawMode, withdrawAmount);
    }
    */
}
