package com.intelliviz.retirementhelper.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by edm on 3/27/2017.
 */

public class RetirementProvider extends ContentProvider {
    private SqliteHelper mSqliteHelper;
    private static final String DBASE_NAME = "retirement";
    private static final int DBASE_VERSION = 9;
    private static final int PERSONALINFO_ID = 101;
    private static final int CATEGORY_LIST = 201;
    private static final int CATEGORY_ID = 202;
    private static final int EXPENSE_LIST = 301;
    private static final int EXPENSE_ID = 302;
    private static final int INSTITUTION_LIST = 401;
    private static final int INSTITUTION_ID = 402;
    private static final int PENSION_DATA_LIST = 501;
    private static final int PENSION_DATA_ID = 502;
    private static final int SAVINGS_DATA_LIST = 601;
    private static final int SAVINGS_DATA_ID = 602;
    private static final int BALANCE_LIST = 701;
    private static final int BALANCE_ID = 702;


    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_PERSONALINFO, PERSONALINFO_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_CATEGORY, CATEGORY_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_CATEGORY + "/#", CATEGORY_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_EXPENSE, EXPENSE_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_EXPENSE + "/#", EXPENSE_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_INSTITUTION, INSTITUTION_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_INSTITUTION + "/#", INSTITUTION_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_PENSION_DATA, PENSION_DATA_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_PENSION_DATA + "/#", PENSION_DATA_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_SAVINGS_DATA, SAVINGS_DATA_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_SAVINGS_DATA + "/#", SAVINGS_DATA_ID);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_BALANCE, BALANCE_LIST);

        sUriMatcher.addURI(RetirementContract.CONTENT_AUTHORITY, RetirementContract.PATH_BALANCE + "/#", BALANCE_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mSqliteHelper = new SqliteHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                return RetirementContract.PeronsalInfoEntry.CONTENT_ITEM_TYPE;
            case EXPENSE_LIST:
                return RetirementContract.ExpenseEntery.CONTENT_TYPE;
            case EXPENSE_ID:
                return RetirementContract.ExpenseEntery.CONTENT_ITEM_TYPE;
            case CATEGORY_LIST:
                return RetirementContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return RetirementContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case INSTITUTION_LIST:
                return RetirementContract.InstitutionEntry.CONTENT_TYPE;
            case INSTITUTION_ID:
                return RetirementContract.InstitutionEntry.CONTENT_ITEM_TYPE;
            case PENSION_DATA_LIST:
                return RetirementContract.PensionDataEntry.CONTENT_TYPE;
            case PENSION_DATA_ID:
                return RetirementContract.PensionDataEntry.CONTENT_ITEM_TYPE;
            case SAVINGS_DATA_LIST:
                return RetirementContract.SavingsDataEntry.CONTENT_TYPE;
            case SAVINGS_DATA_ID:
                return RetirementContract.SavingsDataEntry.CONTENT_ITEM_TYPE;
            case BALANCE_LIST:
                return RetirementContract.BalanceEntry.CONTENT_TYPE;
            case BALANCE_ID:
                return RetirementContract.BalanceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                // get the personal info table, there should be only one.
                sqLiteQueryBuilder.setTables(RetirementContract.PeronsalInfoEntry.TABLE_NAME);
                break;
            case CATEGORY_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.CategoryEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.CategoryEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case CATEGORY_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.CategoryEntry.TABLE_NAME);
                break;
            case EXPENSE_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.ExpenseEntery.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.ExpenseEntery._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case EXPENSE_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.ExpenseEntery.TABLE_NAME);
                break;
            case INSTITUTION_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.InstitutionEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.InstitutionEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case INSTITUTION_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.InstitutionEntry.TABLE_NAME);
                break;
            case PENSION_DATA_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.PensionDataEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.PensionDataEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case PENSION_DATA_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.PensionDataEntry.TABLE_NAME);
                break;
            case SAVINGS_DATA_ID:
                sqLiteQueryBuilder.setTables(RetirementContract.SavingsDataEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(RetirementContract.SavingsDataEntry._ID +
                        "=" + uri.getLastPathSegment());
                break;
            case SAVINGS_DATA_LIST:
                sqLiteQueryBuilder.setTables(RetirementContract.SavingsDataEntry.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        Cursor cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId;
        SQLiteDatabase db;
        Uri returnUri;

        db = mSqliteHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case CATEGORY_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.CategoryEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case EXPENSE_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.ExpenseEntery.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case INSTITUTION_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.InstitutionEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case PENSION_DATA_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.PensionDataEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SAVINGS_DATA_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.SavingsDataEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case BALANCE_LIST:
                // The second parameter will allow an empty row to be inserted. If it was null, then no row
                // can be inserted if values is empty.
                rowId = db.insert(RetirementContract.BalanceEntry.TABLE_NAME, null, values);
                if (rowId > -1) {
                    returnUri = ContentUris.withAppendedId(uri, rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri.toString());
        }
        notifyChanges(returnUri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        int rowsDeleted = 0;
        String id;

        switch(sUriMatcher.match(uri)) {
            case CATEGORY_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.CategoryEntry.TABLE_NAME,
                        RetirementContract.CategoryEntry._ID + "=" + id, null);
                break;
            case EXPENSE_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.ExpenseEntery.TABLE_NAME,
                        RetirementContract.ExpenseEntery._ID + "=" + id, null);
                break;
            case INSTITUTION_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.InstitutionEntry.TABLE_NAME,
                        RetirementContract.InstitutionEntry._ID + "=" + id, null);
                break;
            case PENSION_DATA_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.PensionDataEntry.TABLE_NAME,
                        RetirementContract.InstitutionEntry._ID + "=" + id, null);
                break;
            case SAVINGS_DATA_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.SavingsDataEntry.TABLE_NAME,
                        RetirementContract.InstitutionEntry._ID + "=" + id, null);
                break;
            case BALANCE_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = db.delete(RetirementContract.BalanceEntry.TABLE_NAME,
                        RetirementContract.InstitutionEntry._ID + "=" + id, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSqliteHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        String id;

        switch(sUriMatcher.match(uri)) {
            case PERSONALINFO_ID:
                rowsUpdated = db.update(RetirementContract.PeronsalInfoEntry.TABLE_NAME,
                        values, null, null);
                break;
            case CATEGORY_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.CategoryEntry.TABLE_NAME,
                            values,
                            RetirementContract.ExpenseEntery._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.CategoryEntry.TABLE_NAME,
                            values,
                            RetirementContract.CategoryEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case EXPENSE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.ExpenseEntery.TABLE_NAME,
                            values,
                            RetirementContract.ExpenseEntery._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.ExpenseEntery.TABLE_NAME,
                            values,
                            RetirementContract.ExpenseEntery._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case INSTITUTION_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.InstitutionEntry.TABLE_NAME,
                            values,
                            RetirementContract.InstitutionEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.InstitutionEntry.TABLE_NAME,
                            values,
                            RetirementContract.InstitutionEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case PENSION_DATA_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.PensionDataEntry.TABLE_NAME,
                            values,
                            RetirementContract.PensionDataEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.PensionDataEntry.TABLE_NAME,
                            values,
                            RetirementContract.PensionDataEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case SAVINGS_DATA_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.SavingsDataEntry.TABLE_NAME,
                            values,
                            RetirementContract.SavingsDataEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.SavingsDataEntry.TABLE_NAME,
                            values,
                            RetirementContract.SavingsDataEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case BALANCE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(RetirementContract.BalanceEntry.TABLE_NAME,
                            values,
                            RetirementContract.BalanceEntry._ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = db.update(RetirementContract.BalanceEntry.TABLE_NAME,
                            values,
                            RetirementContract.BalanceEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
        if (rowsUpdated != 0) {
            notifyChanges(uri);
        }
        return rowsUpdated;
    }

    private void notifyChanges(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        if(contentResolver != null) {
            contentResolver.notifyChange(uri, null);
        }
    }

    private static class SqliteHelper extends SQLiteOpenHelper {

        SqliteHelper(Context context) {
            super(context, DBASE_NAME, null, DBASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create the personal info table
            String sql = "CREATE TABLE " + RetirementContract.PeronsalInfoEntry.TABLE_NAME +
                    " ( " + RetirementContract.PeronsalInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_PASSWORD + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_NAME + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_PIN + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_DEATH_AGE + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_START_AGE + " TEXT, " +
                    RetirementContract.PeronsalInfoEntry.COLUMN_BIRTHDATE + " TEXT);";

            db.execSQL(sql);

            // create the category table
            sql = "CREATE TABLE " + RetirementContract.CategoryEntry.TABLE_NAME +
                    " ( " + RetirementContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.CategoryEntry.COLUMN_NAME + " INTEGER NOT NULL, " +
                    RetirementContract.CategoryEntry.COLUMN_PARENT_NAME + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the expense table
            sql = "CREATE TABLE " + RetirementContract.ExpenseEntery.TABLE_NAME +
                    " ( " + RetirementContract.ExpenseEntery._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.ExpenseEntery.COLUMN_CAT_ID + " INTEGER NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_YEAR + " TEXT NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_MONTH + " TEXT NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_ACTUAL_AMOUNT + " TEXT NOT NULL, " +
                    RetirementContract.ExpenseEntery.COLUMN_RETIRE_AMOUNT + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the institution table
            sql = "CREATE TABLE " + RetirementContract.InstitutionEntry.TABLE_NAME +
                    " ( " + RetirementContract.InstitutionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.InstitutionEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                    RetirementContract.InstitutionEntry.COLUMN_NAME + " TEXT NOT NULL);";


            db.execSQL(sql);

            // create the pension data table
            sql = "CREATE TABLE " + RetirementContract.PensionDataEntry.TABLE_NAME +
                    " ( " + RetirementContract.PensionDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.PensionDataEntry.COLUMN_INSTITUTION_ID + " INTEGER NOT NULL, " +
                    RetirementContract.PensionDataEntry.COLUMN_START_AGE + " TEXT NOT NULL, " +
                    RetirementContract.PensionDataEntry.COLUMN_MONTHLY_BENEFIT + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the savings data table
            sql = "CREATE TABLE " + RetirementContract.SavingsDataEntry.TABLE_NAME +
                    " ( " + RetirementContract.SavingsDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.SavingsDataEntry.COLUMN_INSTITUTION_ID + " INTEGER NOT NULL, " +
                    RetirementContract.SavingsDataEntry.COLUMN_INTEREST + " TEXT NOT NULL, " +
                    RetirementContract.SavingsDataEntry.COLUMN_MONTHLY_ADDITION + " TEXT NOT NULL);";

            db.execSQL(sql);

            // create the balance table
            sql = "CREATE TABLE " + RetirementContract.BalanceEntry.TABLE_NAME +
                    " ( " + RetirementContract.BalanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RetirementContract.BalanceEntry.COLUMN_INSTITUTION_ID + " INTEGER NOT NULL, " +
                    RetirementContract.BalanceEntry.COLUMN_AMOUNT + " TEXT NOT NULL, " +
                    RetirementContract.BalanceEntry.COLUMN_DATE + " TEXT NOT NULL);";

            db.execSQL(sql);

            String ROW = "INSERT INTO " + RetirementContract.PeronsalInfoEntry.TABLE_NAME + " Values ('0', '-1', '-1', '-1', '-1', '90', 'NOW', '-1');";
            db.execSQL(ROW);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.PeronsalInfoEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.CategoryEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.ExpenseEntery.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + "income_source"); // TODO remove
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.InstitutionEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.PensionDataEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.SavingsDataEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RetirementContract.BalanceEntry.TABLE_NAME);

            onCreate(db);
        }
    }
}
