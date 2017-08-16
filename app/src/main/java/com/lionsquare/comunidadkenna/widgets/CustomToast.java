package com.lionsquare.comunidadkenna.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lionsquare.comunidadkenna.R;

/**
 * Created by EDGAR ARANA on 16/08/2017.
 */

public class CustomToast {
    public  static Toast toast;
    public static void show(Context context, String message, boolean showImage) {


        if (context != null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.custom_toast, null);

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(message);

            ImageView imageView = (ImageView) layout.findViewById(R.id.image);

            if (!showImage)
                imageView.setVisibility(View.GONE);

            layout.requestLayout();

            if (toast == null
                    || toast.getView().getWindowVisibility() != View.VISIBLE) {
                toast = new Toast(context.getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }



        }


    }
}
