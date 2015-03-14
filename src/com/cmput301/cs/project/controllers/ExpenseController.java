package com.cmput301.cs.project.controllers;


import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.cmput301.cs.project.model.Expense;

import java.io.File;

public class ExpenseController {
    Expense mExpense;

    public ExpenseController(Expense expense) {
        mExpense = expense;
    }

    public Expense getExpense() {
        return mExpense;
    }

    public Drawable createDrawableReceipt(Resources res) {
        final File receiptFile = mExpense.getReceipt().getFile();
        return new BitmapDrawable(res, receiptFile.getPath());
    }
}
