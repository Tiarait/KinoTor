package com.kinotor.tiar.kinotor.ui.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.kinotor.tiar.kinotor.R;

/**
 * Created by Tiar on 02.01.2018.
 */
public abstract class DialogSearch extends DialogFragment implements View.OnClickListener {
    private EditText text;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setStyle(STYLE_NO_TITLE, 0);
        View v = inflater.inflate(R.layout.dialog_serch, null);
        v.findViewById(R.id.b_search).setOnClickListener(this);
        v.findViewById(R.id.b_cancel).setOnClickListener(this);

        text = (EditText) v.findViewById(R.id.text);
        text.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onDone();
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    public abstract void ok(String s);

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_search:
                onDone();
                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }
    }

    private void onDone () {
        String s = text.getText().toString();
        ok(s.trim());
        dismiss();
    }

    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
