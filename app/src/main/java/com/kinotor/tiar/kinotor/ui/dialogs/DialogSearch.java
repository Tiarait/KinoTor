package com.kinotor.tiar.kinotor.ui.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.MainCatalogActivity;

/**
 * Created by Tiar on 02.01.2018.
 */
public class DialogSearch extends DialogFragment implements View.OnClickListener {
  private EditText text;
  private AutoCompleteTextView actv;

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    setStyle(STYLE_NO_TITLE, 0);
    View v = inflater.inflate(R.layout.dialog_search, null);
    v.findViewById(R.id.b_search).setOnClickListener(this);
    v.findViewById(R.id.b_cancel).setOnClickListener(this);

    text = v.findViewById(R.id.text);
    actv = v.findViewById(R.id.actv);

//        List<ItemSearch> searchList = new ArrayList<>();
//        searchList.add(new ItemSearch("test", "firstTest", "http://lordfilm.tv/uploads/posts/2018-08/1535483407-1114476413.jpg"));
//        searchList.add(new ItemSearch("тест", "lastTest", "http://lordfilm.tv/uploads/posts/2018-09/1536949981-1011773705.jpg"));
//        AdapterSearch adapterSearch = new AdapterSearch(getActivity(), searchList);
//        actv.setAdapter(adapterSearch);

    text.setOnKeyListener((v1, keyCode, event) -> {
      // If the event is a key-key event on the "enter" button
      if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
              (keyCode == KeyEvent.KEYCODE_ENTER)) {
        onDone();
        return true;
      }
      return false;
    });
    return v;
  }

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
    Statics.refreshMain = true;
    String s = text.getText().toString();
    Intent intent = new Intent(getActivity(), MainCatalogActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("Type", "actor");
    intent.putExtra("Query", s.trim());
    getActivity().startActivity(intent);
    dismiss();
  }

  public void onCancel(DialogInterface dialog) {
    dismiss();
  }
}
