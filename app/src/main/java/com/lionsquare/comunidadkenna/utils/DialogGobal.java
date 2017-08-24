package com.lionsquare.comunidadkenna.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.ProfileActivity;

/**
 * Created by EDGAR ARANA on 31/07/2017.
 */

public class DialogGobal {

    MaterialDialog dialog;
    Context context;

    private ProgressDialog pDialog;

    public DialogGobal(Context context) {
        this.context = context;

    }


    public void progressIndeterminateStyle() {

        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.custom_progressdialog, true)
                .cancelable(false)
                .show();
        View view = dialog.getView();

    }

    public void progressCustom() {
        pDialog = new ProgressDialog(context);
       // pDialog.setProgressStyle(R.style.MyAlertDialogTheme);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);


    }

    public void setDialog(String desciption) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.conectando)
                .content(desciption)
                .cancelable(false)
                .progressIndeterminateStyle(true)
                .show();
    }

    public void setDialogContent(String title ,String desciption) {
        dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(desciption)
                .cancelable(true)
                .progressIndeterminateStyle(true)
                .show();
    }


    public void correctSend(final Activity activity, String desc) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.send)
                .content(desc)
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

    public void errorProcces(final Activity activity) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(R.string.ocurrio_un_error_al_procesar_tu_solicitud)
                .cancelable(false)
                .positiveText(R.string.reintentar)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.finish();
                    }
                })
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

    public void errorConexion() {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(R.string.ocurrio_un_error_al_contectar)
                .cancelable(true)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .progressIndeterminateStyle(true)
                .show();
    }

    public void tokenDeprecated(final Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(R.string.token_deprecated)
                .content(R.string.inicar_sesion_nuevamente)
                .positiveText(R.string.salir)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent i = new Intent(activity, ProfileActivity.class);
                        i.putExtra("checkin_token", false);
                        activity.startActivity(i);
                        activity.finish();

                    }
                })
                .progressIndeterminateStyle(true)
                .show();
    }

    public void sinInternet(final Activity activity) {
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.sin_imternet)
                .content(R.string.sin_acceso_a_internet)
                .cancelable(false)
                .positiveText(R.string.confijuracion)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        activity.finish();
                    }
                })
                .negativeText(R.string.salir)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
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
        if (pDialog != null)
            pDialog.dismiss();
    }

}
