package com.wisekrakr.david.lonesome.app.fragments;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public abstract class AbstractFragment extends Fragment implements FragmentContext {

    /**
     * Shows a modal where we can choose an option
     * @param options
     * @param title
     * @param onClickListener
     */
    @Override
    public void alertDialogBuilder(String[] options, String title, DialogInterface.OnClickListener onClickListener) {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle(title);
        //set items in dialog
        builder.setItems(options, onClickListener);
        //create and show dialog
        builder.create().show();
    }
}
