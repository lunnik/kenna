package com.lionsquare.kenna.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
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
                .cancelable(false)
                .content(R.string.please_wait)
                .progressIndeterminateStyle(true)
                .show();
    }

    public void setDialog(String desciption) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.conectando)
                .content(desciption)
                .cancelable(false)
                .progressIndeterminateStyle(true)
                .show();
    }

    public void errorConexionFinish(final Activity activity) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(R.string.ocurrio_un_error_al_contectar)
                .cancelable(false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.finish();
                    }
                })
                .progressIndeterminateStyle(true)
                .show();
    }

    public void dimmis() {
        if (dialog != null)
            dialog.dismiss();
    }

}
