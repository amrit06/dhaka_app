package com.example.dhaka.widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.dhaka.R;

public class LoadingAlertDialog {
    private Activity activity;
    private AlertDialog dialog;

    public LoadingAlertDialog(Activity myactivity){
        activity = myactivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflator = activity.getLayoutInflater();
        builder.setView(inflator.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void setCancelable(boolean bool){
        dialog.setCancelable(bool);
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

    public boolean isCancelled(){
        if (dialog.isShowing()){
            return false;
        }else {
            return true;
        }
    }

}
