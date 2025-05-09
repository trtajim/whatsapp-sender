package com.example.whatsappsender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AndroidUtils {
    public static void makeAlert(Context context, String title, String body , Runnable positive){
        new AlertDialog.Builder(context).setTitle(title).setMessage(body).setPositiveButton("এগিয়ে যান", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positive.run();
            }
        }).setNegativeButton("বাতিল করুন ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}
