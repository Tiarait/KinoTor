package com.kinotor.tiar.kinotor.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemCatalogUrls;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.MainCatalogActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tiar on 02.01.2018.
 */
public class DialogSort extends DialogFragment implements View.OnClickListener {
    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> kps = new ArrayList<>();
    String [] year, kp;
    String category = "", categoryText = "Все",
            country = "", yearStart = "1902", yearEnd = "2019", kpStart = "0", kpEnd = "10", genre = "", sort = "";
    String url = "", test = "", name = "";
    TextView categoryT, countryT, yearStartT, yearEndT, kpStartT, kpEndT, genreT, sortT, yearMultT;
    LinearLayout categoryL, countryL, genreL, sortL, yearL, yearMultL, kpL;
    SharedPreferences preference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        setStyle(STYLE_NO_FRAME, R.style.full_screen_dialog);
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.dialog_sort, null);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getDialog().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        categoryT = v.findViewById(R.id.dialog_sort_category_text);
        countryT = v.findViewById(R.id.dialog_sort_country_text);
        yearMultT = v.findViewById(R.id.dialog_sort_year_mult_text);
        yearStartT = v.findViewById(R.id.dialog_sort_year_start);
        yearEndT = v.findViewById(R.id.dialog_sort_year_end);
        kpStartT = v.findViewById(R.id.dialog_sort_kp_start);
        kpEndT = v.findViewById(R.id.dialog_sort_kp_end);
        genreT = v.findViewById(R.id.dialog_sort_genre_text);
        sortT = v.findViewById(R.id.dialog_sort_sort_text);

        categoryL = v.findViewById(R.id.dialog_sort_category);
        countryL = v.findViewById(R.id.dialog_sort_country);
        genreL = v.findViewById(R.id.dialog_sort_genre);
        sortL = v.findViewById(R.id.dialog_sort_sort);

        yearL = v.findViewById(R.id.dialog_sort_year);
        yearMultL = v.findViewById(R.id.dialog_sort_year_mult);
        kpL = v.findViewById(R.id.dialog_sort_kp);


        preference = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!preference.getString("filter_category", "").trim().isEmpty()) {
            category = preference.getString("filter_category", "");
        }
        if (!preference.getString("filter_country", "").trim().isEmpty()) {
            country = preference.getString("filter_country", "");
        }
        if (!preference.getString("filter_genre", "").trim().isEmpty()) {
            genre = preference.getString("filter_genre", "");
        }
        if (!preference.getString("filter_year_st", "").trim().isEmpty()) {
            yearStart = preference.getString("filter_year_st", "");
        }
        if (!preference.getString("filter_year_en", "").trim().isEmpty()) {
            yearEnd  = preference.getString("filter_year_en", "");
        }
        if (!preference.getString("filter_kp_st", "").trim().isEmpty()) {
            kpStart  = preference.getString("filter_kp_st", "");
        }
        if (!preference.getString("filter_kp_en", "").trim().isEmpty()) {
            kpEnd = preference.getString("filter_kp_en", "");
        }
        if (!preference.getString("filter_sort", "").trim().isEmpty()) {
            sort = preference.getString("filter_sort", "");
        }

        for (int i = 2019; i > 1901; i--) {
            years.add(String.valueOf(i));
        }
        year = years.toArray(new String[years.size()]);
        for (int i = 10; i > 0; i--) {
            kps.add(String.valueOf(i));
        }
        kp = kps.toArray(new String[kps.size()]);

        switch (preference.getString("catalog", "filmix")){
            case "filmix":
                if (category.isEmpty())
                    category = "filters%2Fs7-s14-s93-s999";
                else {
                    String c = "";
                    for (String ct : ItemCatalogUrls.cFilmixUrl) {
                        if (category.contains(ct+"-") || category.endsWith(ct)) {
                            c += ItemCatalogUrls.cFilmix[Arrays.asList(ItemCatalogUrls.cFilmixUrl).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        category = "filters%2Fs7-s14-s93-s999";
                    }
                    categoryT.setText(c.trim().replace("  ", ", "));
                }
                if (!country.isEmpty()) {
                    String c = "";
                    for (String ct : ItemCatalogUrls.countryFilmixID) {
                        if (country.contains(ct+"-") || country.endsWith(ct)) {
                            c += ItemCatalogUrls.countryFilmix[Arrays.asList(ItemCatalogUrls.countryFilmixID).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        country = "";
                    }
                    countryT.setText(c.trim().replace("  ", ", "));
                }
                if (!genre.isEmpty()) {
                    String c = "";
                    for (String ct : ItemCatalogUrls.cFilmixGenreUrl) {
                        if (genre.contains(ct+"-") || genre.endsWith(ct)) {
                            c += ItemCatalogUrls.cFilmixGenre[Arrays.asList(ItemCatalogUrls.cFilmixGenreUrl).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        genre = "";
                    }
                    genreT.setText(c.trim().replace("  ", ", "));
                }

                if (yearStart.isEmpty()) {
                    yearStart = "1901";
                }
                yearStartT.setText(yearStart);
                if (yearEnd.isEmpty()) {
                    yearEnd = "2019";
                }
                yearEndT.setText(yearEnd);

                if (sort.isEmpty()) {
                    sort = ItemCatalogUrls.sortFilmixID[0];
                    sortT.setText(ItemCatalogUrls.sortFilmix[0]);
                } else {
                    if (Arrays.asList(ItemCatalogUrls.sortFilmixID).indexOf(sort) != -1)
                        sortT.setText(ItemCatalogUrls.sortFilmix[Arrays.asList(ItemCatalogUrls.sortFilmixID).indexOf(sort)]);
                }
                sortL.setVisibility(View.VISIBLE);
                yearL.setVisibility(View.VISIBLE);
                yearMultL.setVisibility(View.GONE);
                countryL.setVisibility(View.VISIBLE);
                kpL.setVisibility(View.GONE);
                break;
            case "topkino":
                if (category.isEmpty())
                    category = "cat=19%2C3%2C15%2C16/";
                else {
                    String c = "";
                    for (String ct : ItemCatalogUrls.cTopkinoUrl) {
                        if (category.contains("="+ct) || category.contains("2C"+ct)) {
                            Log.e("ewq", ct);
                            if (Arrays.asList(ItemCatalogUrls.cTopkinoUrl).indexOf(ct) != -1)
                                c += ItemCatalogUrls.cTopkino[Arrays.asList(ItemCatalogUrls.cTopkinoUrl).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        category = "cat=19%2C3%2C15%2C16/";
                    }
                    categoryT.setText(c.trim().replace("  ", ", "));
                }
                if (!genre.isEmpty()) {
                    String c = "";
                    for (String ct : ItemCatalogUrls.cTopkinoGenre) {
                        if (genre.contains(ct)) {
                            c += ct + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        genre = "";
                    }
                    genreT.setText(c.trim().replace("  ", ", "));
                }
                if (!yearStart.isEmpty()) {
                    String c = "";
                    for (String ct : year) {
                        if (yearStart.contains(ct)) {
                            c += ct + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        yearStart = "";
                    }
                    yearMultT.setText(c.trim().replace("  ", ", "));
                }
                if (sort.isEmpty()) {
                    sort = ItemCatalogUrls.sortFilmixID[0];
                    sortT.setText(ItemCatalogUrls.sortTopkino[0]);
                } else {
                    if (Arrays.asList(ItemCatalogUrls.sortTopkinoID).indexOf(sort) != -1)
                        sortT.setText(ItemCatalogUrls.sortTopkino[Arrays.asList(ItemCatalogUrls.sortTopkinoID).indexOf(sort)]);
                }
                sortL.setVisibility(View.VISIBLE);
                yearL.setVisibility(View.GONE);
                yearMultL.setVisibility(View.VISIBLE);
                countryL.setVisibility(View.GONE);
                kpL.setVisibility(View.VISIBLE);
                break;
            case "kinopub":
                if (category.isEmpty())
                    category = ItemCatalogUrls.cKinopubUrl[0];
                else {
                    categoryT.setText(ItemCatalogUrls.cKinopub[Arrays.asList(ItemCatalogUrls.cKinopubUrl).indexOf(category)]);
                }
                if (sort.isEmpty())
                    sort = ItemCatalogUrls.sortKinopubID[0];
                else {
                    sortT.setText(ItemCatalogUrls.sortKinopub[Arrays.asList(ItemCatalogUrls.sortKinopubID).indexOf(sort)]);
                }
                if (genre.isEmpty())
                    genre = ItemCatalogUrls.genreKinopubID[0];
                else {
                    genreT.setText(ItemCatalogUrls.genreKinopub[Arrays.asList(ItemCatalogUrls.genreKinopubID).indexOf(genre)]);
                }
                if (country.isEmpty())
                    country = ItemCatalogUrls.countryKinopubID[0];
                else {
                    countryT.setText(ItemCatalogUrls.countryKinopub[Arrays.asList(ItemCatalogUrls.countryKinopubID).indexOf(country)]);
                }
                if (yearStart.isEmpty()) {
                    yearStart = "1901";
                }
                yearStartT.setText(yearStart);
                if (yearEnd.isEmpty()) {
                    yearEnd = "2019";
                }
                yearEndT.setText(yearEnd);

                categoryT.setText(ItemCatalogUrls.cKinopub[0]);
                genreT.setText(ItemCatalogUrls.genreKinopub[0]);
                countryT.setText(ItemCatalogUrls.countryKinopub[0]);
                sortT.setText(ItemCatalogUrls.sortKinopub[0]);

                sortL.setVisibility(View.VISIBLE);
                yearL.setVisibility(View.VISIBLE);
                yearMultL.setVisibility(View.GONE);
                countryL.setVisibility(View.VISIBLE);
                kpL.setVisibility(View.VISIBLE);
                break;
            case "my-hit":
                if (category.isEmpty()) {
                    category = "/film/";
                    categoryT.setText("Фильмы");
                }  else {
                    categoryT.setText(ItemCatalogUrls.cMyhitC[Arrays.asList(ItemCatalogUrls.cMyhitCUrl).indexOf(category)]);
                }
                if (!country.isEmpty()) {
                    String c = "";
                    for (String ct : ItemCatalogUrls.countryMyhitID) {
                        if (country.contains(ct)) {
                            c += ItemCatalogUrls.countryMyhit[Arrays.asList(ItemCatalogUrls.countryMyhitID).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        country = "";
                    }
                    countryT.setText(c.trim().replace("  ", ", "));
                }
                if (!genre.isEmpty()) {
                    String c = "";
                    for (String ct : ItemCatalogUrls.cMyhitGenreUrl) {
                        if (genre.contains(ct)) {
                            c += ItemCatalogUrls.cMyhitGenre[Arrays.asList(ItemCatalogUrls.cMyhitGenreUrl).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        genre = "";
                    }
                    genreT.setText(c.trim().replace("  ", ", "));
                }
                if (!yearStart.isEmpty()) {
                    String c = "";
                    for (String ct : ItemCatalogUrls.sortMyhitYearID) {
                        if (yearStart.contains(ct)) {
                            c += ItemCatalogUrls.sortMyhitYear[Arrays.asList(ItemCatalogUrls.sortMyhitYearID).indexOf(ct)] + "  ";
                        }
                    }
                    if (c.isEmpty()) {
                        c = "Все";
                        yearStart = "";
                    }
                    genreT.setText(c.trim().replace("  ", ", "));
                }
                if (sort.isEmpty()) {
                    sort = ItemCatalogUrls.sortMyhitID[0];
                    sortT.setText(ItemCatalogUrls.sortMyhit[0]);
                } else {
                    sortT.setText(ItemCatalogUrls.sortMyhit[Arrays.asList(ItemCatalogUrls.sortMyhitID).indexOf(sort)]);
                }
                sortL.setVisibility(View.VISIBLE);
                yearL.setVisibility(View.GONE);
                yearMultL.setVisibility(View.VISIBLE);
                countryL.setVisibility(View.VISIBLE);
                kpL.setVisibility(View.GONE);
                break;
        }

        v.findViewById(R.id.b_ok).setFocusable(true);
        v.findViewById(R.id.b_cancel).setFocusable(true);
        categoryL.setFocusable(true);
        countryL.setFocusable(true);
        genreL.setFocusable(true);
        sortL.setFocusable(true);
        yearMultL.setFocusable(true);
        yearStartT.setFocusable(true);
        yearEndT.setFocusable(true);
        kpStartT.setFocusable(true);
        kpEndT.setFocusable(true);

        v.findViewById(R.id.b_ok).setOnFocusChangeListener(this::changeListener);
        v.findViewById(R.id.b_cancel).setOnFocusChangeListener(this::changeListener);
        categoryL.setOnFocusChangeListener(this::changeListener);
        countryL.setOnFocusChangeListener(this::changeListener);
        genreL.setOnFocusChangeListener(this::changeListener);
        sortL.setOnFocusChangeListener(this::changeListener);
        yearMultL.setOnFocusChangeListener(this::changeListener);
        yearStartT.setOnFocusChangeListener(this::changeListener);
        yearEndT.setOnFocusChangeListener(this::changeListener);
        kpStartT.setOnFocusChangeListener(this::changeListener);
        kpEndT.setOnFocusChangeListener(this::changeListener);

        v.findViewById(R.id.b_ok).setOnClickListener(this);
        v.findViewById(R.id.b_cancel).setOnClickListener(this);
        categoryL.setOnClickListener(this);
        countryL.setOnClickListener(this);
        genreL.setOnClickListener(this);
        sortL.setOnClickListener(this);
        yearMultL.setOnClickListener(this);
        yearStartT.setOnClickListener(this);
        yearEndT.setOnClickListener(this);
        kpStartT.setOnClickListener(this);
        kpEndT.setOnClickListener(this);

        return v;
    }

    @Override
    public int getTheme() {
        return R.style.full_screen_dialog;
    }

    private void changeListener(View view, boolean b) {
        if (!view.isSelected()) {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryLight));
        }
        else view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
        view.setSelected(b);
    }

    public void onClick(View v) {
        SharedPreferences.Editor editor = preference.edit();
        switch (v.getId()) {
            case R.id.b_ok:
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        url = Statics.FILMIX_URL + "/loader.php?do=cat&category=" +
                                category + country + genre + "-r" + yearStart + yearEnd +
                                "&requested_url=" + category + country + genre + "-r" + yearStart + yearEnd;
                        break;
                    case "topkino":
                        url = Statics.TOPKINO_URL + "/f/" +
                                category + genre + yearStart +
                                "from-kinopoisk=" + kpStart + "/to-kinopoisk=" + kpEnd + "/" + sort;
                        break;
                    case "my-hit":
                        url = Statics.MYHIT_URL + category + "-" + genre + "-" + country + "-" + yearStart +
                                "/?s=" + sort;
                        break;
                    case "kinopub":
                        url = Statics.KINOPUB_URL + category + "?genre=" + genre + "&country=" +
                                country + "&years=" + yearStart + "%3B" + yearEnd +
                                "&kinopoisk=" + kpStart + "%3B" + kpEnd +
                                "&order=" + sort + "&period=all&subtitle=&imdb=0%3B10";
                        break;
                }
                editor.putString("last_catalog", preference.getString("catalog", "filmix"));
                editor.putString("filter_category", category);
                editor.putString("filter_country", country);
                editor.putString("filter_genre", genre);
                editor.putString("filter_year_st", yearStart);
                editor.putString("filter_year_en", yearEnd);
                editor.putString("filter_kp_st", kpStart);
                editor.putString("filter_kp_en", kpEnd);
                editor.putString("filter_sort", sort);
                editor.apply();

                Statics.refreshMain = true;

                Intent intent = new Intent(getActivity(), MainCatalogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Type", "sort");
                intent.putExtra("Query", url.trim());
                getActivity().startActivity(intent);
                dismiss();
                break;
            case R.id.b_cancel:
                editor.putString("last_catalog", preference.getString("catalog", "filmix"));
                editor.putString("filter_category", "");
                editor.putString("filter_country", "");
                editor.putString("filter_genre", "");
                editor.putString("filter_year_st", "");
                editor.putString("filter_year_en", "");
                editor.putString("filter_kp_st", "");
                editor.putString("filter_kp_en", "");
                editor.putString("filter_sort", "");
                editor.apply();

                dismiss();
                break;
            case R.id.dialog_sort_category:
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        AlertDialog.Builder builderCT = multiBuilder(ItemCatalogUrls.cFilmix, ItemCatalogUrls.cFilmixUrl);
                        builderCT.setNegativeButton("Отмена", (dialog, id) -> {
                            category = "filters%2Fs7-s14-s93-s999";
                            categoryT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (test.startsWith("-"))
                                test = test.substring(1);
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            categoryT.setText(name.replace(",", ", "));
                            category = "filters%2F" + test.trim();
                        }).create().show();
                        break;
                    case "topkino":
                        AlertDialog.Builder topkinoCategory = multiBuilder(ItemCatalogUrls.cTopkino, ItemCatalogUrls.cTopkinoUrl);
                        topkinoCategory.setNegativeButton("Отмена", (dialog, id) -> {
                            category = "cat=19%2C3%2C15%2C16/";
                            categoryT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (test.startsWith("-"))
                                test = test.substring(1);
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            categoryT.setText(name.replace(",", ", "));
                            category = "cat=" + test.trim().replace("-", "%2C") + "/";
                        }).create().show();
                        break;
                    case "my-hit":
                        AlertDialog.Builder myhitCategory = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        myhitCategory.setItems(ItemCatalogUrls.cMyhitC,
                                (dialog, i) -> {
                                    test = ItemCatalogUrls.cMyhitCUrl[i];
                                    name = ItemCatalogUrls.cMyhitC[i];
                                    category = ItemCatalogUrls.cMyhitCUrl[i];
                                    categoryT.setText(ItemCatalogUrls.cMyhitC[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "kinopub":
                        AlertDialog.Builder kinopubCategory = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        kinopubCategory.setItems(ItemCatalogUrls.cKinopub,
                                (dialog, i) -> {
                                    category = ItemCatalogUrls.cKinopubUrl[i].replace(Statics.KINOPUB_URL, "");
                                    categoryT.setText(ItemCatalogUrls.cKinopub[i]);
                                    genre = ItemCatalogUrls.genreKinopubID[0];
                                    genreT.setText(ItemCatalogUrls.genreKinopub[0]);
                                    country = ItemCatalogUrls.countryKinopubID[0];
                                    countryT.setText(ItemCatalogUrls.countryKinopub[0]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_country:
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        AlertDialog.Builder builderC = multiBuilder(ItemCatalogUrls.countryFilmix, ItemCatalogUrls.countryFilmixID);
                        builderC.setNegativeButton("Отмена", (dialog, id) -> {
                            country = "";
                            countryT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            countryT.setText(name.replace(",", ", "));
                            country = test.trim();
                        }).create().show();
                        break;
                    case "topkino":
                        break;
                    case "my-hit":
                        AlertDialog.Builder myhitCategory = multiBuilder(ItemCatalogUrls.countryMyhit, ItemCatalogUrls.countryMyhitID);
                        myhitCategory.setNegativeButton("Отмена", (dialog, id) -> {
                            country = "";
                            countryT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            if (test.trim().endsWith(","))
                                test = test.trim().substring(0, test.length() - 1);
                            if (test.trim().startsWith("-"))
                                test = test.trim().substring(1);
                            countryT.setText(name.replace(",", ", "));
                            country = test.replace(",","-").trim();
                        }).create().show();
                        break;
                    case "kinopub":
                        AlertDialog.Builder kinopubCategory = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        kinopubCategory.setItems(ItemCatalogUrls.countryKinopub,
                                (dialog, i) -> {
                                    country = ItemCatalogUrls.countryKinopubID[i].replace(Statics.KINOPUB_URL, "");
                                    countryT.setText(ItemCatalogUrls.countryKinopub[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_genre:
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        AlertDialog.Builder builder = multiBuilder(ItemCatalogUrls.cFilmixGenre, ItemCatalogUrls.cFilmixGenreUrl);
                        builder.setNegativeButton("Отмена", (dialog, id) -> {
                            genre = "";
                            genreT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            genre = test.trim();
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            genreT.setText(name.replace(",", ", "));
                        }).create().show();
                        break;
                    case "topkino":
                        AlertDialog.Builder topkinoGenre = multiBuilder(ItemCatalogUrls.cTopkinoGenre,
                                ItemCatalogUrls.cTopkinoGenre);
                        topkinoGenre.setNegativeButton("Отмена", (dialog, id) -> {
                            genre = "";
                            genreT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (test.trim().startsWith("-"))
                                test = test.trim().substring(1);
                            genre = "zhanr=" + test.trim().replace("-", "%2C") + "/";
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            genreT.setText(name.replace(",", ", "));
                        }).create().show();
                        break;
                    case "my-hit":
                        AlertDialog.Builder myhitGenre = multiBuilder(ItemCatalogUrls.cMyhitGenre,
                                ItemCatalogUrls.cMyhitGenreUrl);
                        myhitGenre.setNegativeButton("Отмена", (dialog, id) -> {
                            genre = "";
                            genreT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (test.trim().startsWith("-"))
                                test = test.trim().substring(1);
                            genre = test.trim();
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            genreT.setText(name.replace(",", ", "));
                        }).create().show();
                        break;
                    case "kinopub":
                        String[] genres = ItemCatalogUrls.genreKinopub;
                        String[] genresID = ItemCatalogUrls.genreKinopubID;
                        if (category.contains("concert")) {
                            genres = ItemCatalogUrls.genreKinopubConc;
                            genresID = ItemCatalogUrls.genreKinopubConcID;
                        }
                        if (category.contains("docu")) {
                            genres = ItemCatalogUrls.genreKinopubDoc;
                            genresID = ItemCatalogUrls.genreKinopubDocID;
                        }
                        if (category.contains("tvshow")) {
                            genres = ItemCatalogUrls.genreKinopubTv;
                            genresID = ItemCatalogUrls.genreKinopubTvID;
                        }
                        String[] g = genres;
                        String[] gID = genresID;
                        AlertDialog.Builder kinopubCategory = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        kinopubCategory.setItems(g,
                                (dialog, i) -> {
                                    genre = g[i];
                                    countryT.setText(gID[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_year_start:
                AlertDialog.Builder builderS = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        builderS.setItems(year,
                                (dialog, i) -> {
                                    yearStart = year[i];
                                    yearStartT.setText(year[i]);
                                    dialog.dismiss();
                                });
                        break;
                    case "kinopub":
                        builderS.setItems(year,
                                (dialog, i) -> {
                                    yearStart = year[i];
                                    yearStartT.setText(year[i]);
                                    dialog.dismiss();
                                });
                        break;
                }
                builderS.create().show();
                break;
            case R.id.dialog_sort_year_end:
                AlertDialog.Builder builderE = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        builderE.setItems(year, (dialog, i) -> {
                                    yearEnd = year[i];
                                    yearEndT.setText(year[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "kinopub":
                        builderE.setItems(year, (dialog, i) -> {
                                    yearEnd = year[i];
                                    yearEndT.setText(year[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_kp_start:
                AlertDialog.Builder builderKS = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                switch (preference.getString("catalog", "filmix")) {
                    case "topkino":
                        builderKS.setItems(kp, (dialog, i) -> {
                                    kpStart = kp[i];
                                    kpStartT.setText(kp[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "kinopub":
                        builderKS.setItems(kp, (dialog, i) -> {
                                    kpStart = kp[i];
                                    kpStartT.setText(kp[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_kp_end:
                AlertDialog.Builder builderKE = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                switch (preference.getString("catalog", "filmix")) {
                    case "topkino":
                        builderKE.setItems(kp, (dialog, i) -> {
                                    kpEnd = kp[i];
                                    kpEndT.setText(kp[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "kinopub":
                        builderKE.setItems(kp, (dialog, i) -> {
                            kpEnd = kp[i];
                            kpEndT.setText(kp[i]);
                            dialog.dismiss();
                        }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_year_mult:
                switch (preference.getString("catalog", "filmix")) {
                    case "topkino":
                        AlertDialog.Builder topkinoYear = multiBuilder(year, year);
                        topkinoYear.setNegativeButton("Отмена", (dialog, id) -> {
                            yearStart = "";
                            yearMultT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (test.trim().startsWith("-"))
                                test = test.trim().substring(1);
                            yearStart = "year=" + test.trim().replace("-", "%2C") + "/";
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            yearMultT.setText(name.replace(",", ", "));
                        }).create().show();
                        break;
                    case "my-hit":
                        AlertDialog.Builder myhitYear = multiBuilder(ItemCatalogUrls.sortMyhitYear,
                                ItemCatalogUrls.sortMyhitYearID);
                        myhitYear.setNegativeButton("Отмена", (dialog, id) -> {
                            genre = "";
                            genreT.setText("Все");
                            dialog.cancel();
                        }).setPositiveButton("Применить", (dialog, id) -> {
                            if (test.trim().startsWith("-"))
                                test = test.trim().substring(1);
                            yearStart = test.trim();
                            if (name.trim().endsWith(","))
                                name = name.trim().substring(0, name.length() - 1);
                            yearMultT.setText(name.replace(",", ", "));
                        }).create().show();
                        break;
                }
                break;
            case R.id.dialog_sort_sort:
                switch (preference.getString("catalog", "filmix")) {
                    case "filmix":
                        AlertDialog.Builder filmixSort = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        filmixSort.setItems(ItemCatalogUrls.sortFilmix,
                                (dialog, i) -> {
                                    test = ItemCatalogUrls.sortFilmixID[i];
                                    name = ItemCatalogUrls.sortFilmix[i];
                                    sort = test.replace("-", "").trim();
                                    ItemMain.xs_field = sort;
                                    if (name.trim().endsWith(","))
                                        name = name.trim().substring(0, name.length() - 1);
                                    sortT.setText(name.replace(",", ", "));
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "topkino":
                        AlertDialog.Builder topkinoSort = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        topkinoSort.setItems(ItemCatalogUrls.sortTopkino,
                                (dialog, i) -> {
                                    test = ItemCatalogUrls.sortTopkinoID[i];
                                    name = ItemCatalogUrls.sortTopkino[i];
                                    sort = test.replace("-", "").trim();
                                    if (name.trim().endsWith(","))
                                        name = name.trim().substring(0, name.length() - 1);
                                    sortT.setText(name.replace(",", ", "));
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "my-hit":
                        AlertDialog.Builder myhitYear = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        myhitYear.setItems(ItemCatalogUrls.sortMyhit,
                                (dialog, i) -> {
                                    sort = ItemCatalogUrls.sortMyhitID[i];
                                    sortT.setText(ItemCatalogUrls.sortMyhit[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                    case "kinopub":
                        AlertDialog.Builder kinopub = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                        kinopub.setItems(ItemCatalogUrls.sortKinopub,
                                (dialog, i) -> {
                                    sort = ItemCatalogUrls.sortKinopubID[i];
                                    sortT.setText(ItemCatalogUrls.sortKinopub[i]);
                                    dialog.dismiss();
                                }).create().show();
                        break;
                }
                break;
        }
    }

    private AlertDialog.Builder multiBuilder(String[] mSelectedItems, String[] mSelectedVal){
        test = "";
        name = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setMultiChoiceItems(mSelectedItems, null,
                (dialog, i, isChecked) -> {
                    if (name.contains(mSelectedItems[i]))
                        name = name.replace(mSelectedItems[i] + ",", "");
                    else name += mSelectedItems[i] + ",";
                    if (test.contains(mSelectedVal[i]))
                        test = test.replace("-" + mSelectedVal[i], "");
                    else test += "-" + mSelectedVal[i];
                });
        return builder;
    }

    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
