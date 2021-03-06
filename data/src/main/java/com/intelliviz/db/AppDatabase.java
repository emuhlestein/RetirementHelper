package com.intelliviz.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.intelliviz.db.dao.GovPensionDao;
import com.intelliviz.db.dao.MilestoneAgeDao;
import com.intelliviz.db.dao.MilestoneSummaryDao;
import com.intelliviz.db.dao.PensionIncomeDao;
import com.intelliviz.db.dao.RetirementOptionsDao;
import com.intelliviz.db.dao.SavingsIncomeDao;
import com.intelliviz.db.dao.SummaryDao;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.IncomeTypeEntity;
import com.intelliviz.db.entity.MilestoneAgeEntity;
import com.intelliviz.db.entity.MilestoneSummaryEntity;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.db.entity.SummaryEntity;

/**
 * Created by edm on 10/2/2017.
 */

@Database(entities = {MilestoneAgeEntity.class, GovPensionEntity.class, IncomeTypeEntity.class,
        MilestoneSummaryEntity.class, PensionIncomeEntity.class, RetirementOptionsEntity.class,
        SavingsIncomeEntity.class, SummaryEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private volatile static AppDatabase mINSTANCE;

    public static AppDatabase getInstance(Context context) {
        if(mINSTANCE == null) {
            synchronized (AppDatabase.class) {
                if(mINSTANCE == null) {
                    RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            // set default values
                            ContentValues values = new ContentValues();
                            values.put(RetirementOptionsEntity.END_AGE_FIELD, 90*12);
                            values.put(RetirementOptionsEntity.BIRTHDATE_FIELD, 0);
                            values.put(RetirementOptionsEntity.INCLUDE_SPOUSE_FIELD, 0);
                            values.put(RetirementOptionsEntity.SPOUSE_BIRTHDATE_FIELD, 0);
                            values.put(RetirementOptionsEntity.COUNTRY_CODE_FIELD, "US");
                            db.insert(RetirementOptionsEntity.TABLE_NAME, OnConflictStrategy.IGNORE, values);
                        }
                    };

                    mINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "income_db")
                            .addCallback(rdc)
                            .build();
                }
            }
        }
        return mINSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tax_deferred_income "
                    + " ADD COLUMN start_age TEXT");
        }
    };

    public abstract MilestoneAgeDao milestoneAgeDao();
    public abstract GovPensionDao govPensionDao();
    public abstract PensionIncomeDao pensionIncomeDao();
    public abstract SavingsIncomeDao savingsIncomeDao();
    public abstract RetirementOptionsDao retirementOptionsDao();
    public abstract MilestoneSummaryDao milestoneSummaryDao();
    public abstract SummaryDao summaryDao();
}
