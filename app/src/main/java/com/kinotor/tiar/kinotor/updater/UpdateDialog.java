package com.kinotor.tiar.kinotor.updater;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.kinotor.tiar.kinotor.R;

/**
 * Created by Tiar on 02.01.2018.
 */
public class UpdateDialog extends DialogFragment implements View.OnClickListener {
    Intent intent;
    String url;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        url = getTag();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setStyle(STYLE_NO_TITLE, 0);
        View v = inflater.inflate(R.layout.dialog_update, null);
        v.findViewById(R.id.yes).setOnClickListener(this);
        v.findViewById(R.id.no).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                dismiss();
                break;
            case R.id.no:
                dismiss();
                break;
        }
    }

    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
