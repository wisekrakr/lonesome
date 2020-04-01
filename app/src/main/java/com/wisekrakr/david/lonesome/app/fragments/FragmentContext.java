package com.wisekrakr.david.lonesome.app.fragments;

import android.content.DialogInterface;

public interface FragmentContext {
    void alertDialogBuilder(String[] options, String title, DialogInterface.OnClickListener onClickListener);
}
