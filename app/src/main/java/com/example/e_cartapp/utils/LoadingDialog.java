package com.example.e_cartapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.e_cartapp.R;

public class LoadingDialog extends Dialog {
    String message;

    public LoadingDialog(@NonNull Context context, String message) {
        super(context);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);
        setCancelable(false);
        setTitle(message);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
        setContentView(view);
    }
}
