package com.nowinski.kamil.flighttracker.Utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class DialogWindow {
    public static void showDialogOK(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Anuluj", okListener)
                .create()
                .show();
    }
}
