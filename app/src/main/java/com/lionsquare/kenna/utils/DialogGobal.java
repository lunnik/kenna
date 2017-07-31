package com.lionsquare.kenna.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lionsquare.kenna.R;

/**
 * Created by EDGAR ARANA on 31/07/2017.
 */

public class DialogGobal {

    MaterialDialog dialog;
    Context context;

    public DialogGobal(Context context) {
        this.context = context;
    }


    public void progressIndeterminateStyle() {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.conectando)
                .content(R.string.please_wait)
                .progressIndeterminateStyle(true)
                .show();
    }

    public void dimmis() {
        dialog.dismiss();
    }

}
