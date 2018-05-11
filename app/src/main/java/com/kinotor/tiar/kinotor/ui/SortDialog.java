package com.kinotor.tiar.kinotor.ui;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemCatalogUrls;

/**
 * Created by Tiar on 02.01.2018.
 */
public abstract class SortDialog extends DialogFragment implements View.OnClickListener {
    private ItemCatalogUrls catalogUrls;
    private TextView category, country, list, year;
    private String cur_category = "", cur_country = "", cur_list = "", cur_year = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setStyle(STYLE_NO_TITLE, 0);
        catalogUrls = new ItemCatalogUrls();
        View v = inflater.inflate(R.layout.dialog_sort, null);
        v.findViewById(R.id.b_ok).setOnClickListener(this);
        v.findViewById(R.id.b_cancel).setOnClickListener(this);
        v.findViewById(R.id.lbs_category).setOnClickListener(this);
        v.findViewById(R.id.lbs_country).setOnClickListener(this);
        v.findViewById(R.id.lbs_list).setOnClickListener(this);
        v.findViewById(R.id.lbs_year).setOnClickListener(this);

        category = (TextView) v.findViewById(R.id.t_category);
        country = (TextView) v.findViewById(R.id.t_country);
        list = (TextView) v.findViewById(R.id.t_list);
        year = (TextView) v.findViewById(R.id.t_year);
        return v;
    }

    public abstract void ok(String[] x);

    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.b_ok:
//                //"defaultsort", "getGenre", "year", "country"
//                String[] x = {cur_list, cur_category, cur_year, cur_country};
//                ok(x);
//                dismiss();
//                break;
//            case R.id.b_cancel:
//                dismiss();
//                break;
//            case R.id.lbs_list:
//                AlertDialog.Builder builder_list = new AlertDialog.Builder(getActivity(), 2);
//                builder_list.setTitle("Выберите категорию").setItems(catalogUrls.getSortName(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        list.setText(catalogUrls.getSortName()[i]);
//                        cur_list = catalogUrls.getSortUrl()[i];
//                    }
//                });
//                builder_list.create().show();
//                break;
//            case R.id.lbs_category:
//                AlertDialog.Builder builder_category = new AlertDialog.Builder(getActivity(), 2);
//                builder_category.setTitle("Выберите категорию").setItems(catalogUrls.getGenre(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        category.setText(catalogUrls.getGenre()[i]);
//                        cur_category = catalogUrls.getcAmcetID()[i];
//                    }
//                });
//                builder_category.create().show();
//                break;
//            case R.id.lbs_country:
//                AlertDialog.Builder builder_country = new AlertDialog.Builder(getActivity(), 2);
//                builder_country.setTitle("Выберите категорию").setItems(catalogUrls.getSortAmcetCountry(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        country.setText(catalogUrls.getSortAmcetCountry()[i]);
//                        cur_category = catalogUrls.getSortAmcetCountry()[i];
//                    }
//                });
//                builder_country.create().show();
//                break;
//            case R.id.lbs_year:
//                AlertDialog.Builder builder_year = new AlertDialog.Builder(getActivity(), 2);
//                builder_year.setTitle("Выберите категорию").setItems(catalogUrls.getYear(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        year.setText(catalogUrls.getYear()[i]);
//                        cur_year = catalogUrls.getYear()[i];
//                    }
//                });
//                builder_year.create().show();
//                break;
//        }
    }

    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
