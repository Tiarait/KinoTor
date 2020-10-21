package com.kinotor.tiar.kinotor.utils.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiar on 10.2018.
 */
public abstract class AdapterSearch extends ArrayAdapter<ItemSearch> {
    private List<ItemSearch> itemSearch;

    protected AdapterSearch(@NonNull Context context, @NonNull List<ItemSearch> item) {
        super(context, 0, item);
        itemSearch = new ArrayList<>(item);
    }



    @NonNull
    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search, parent, false);
        }
//        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());
        LinearLayout mview = convertView.findViewById(R.id.view);
        TextView title = convertView.findViewById(R.id.title);
        TextView subtitle = convertView.findViewById(R.id.subtitle);
        ImageView img = convertView.findViewById(R.id.img);
//        CardView cardView = convertView.findViewById(R.id.cardview);

//        if (!preference.getBoolean("tv_focus_select", true)) {
//            cardView.setForeground(null);
//        }
//        //holder.mView.setFocusableInTouchMode(true);
//        mview.setOnFocusChangeListener((view, b) -> {
//            if (!view.isSelected()) {
//                view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryLight));
//            }
//            else {
//                view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
//            }
//            view.setSelected(b);
//        });

        ItemSearch itemCur = getItem(position);
        if (itemCur != null) {

            title.setText(itemCur.getTitle());
            subtitle.setText(itemCur.getSubtitle());

            if (itemCur.getSubtitle().contains("error"))
                subtitle.setVisibility(View.GONE);
            if (itemCur.getImg().contains("error"))
                img.setVisibility(View.GONE);
            else {
                Picasso.get()
                        .load(itemCur.getImg())
                        .fit().centerInside()
                        .into(img);
            }

            mview.setFocusable(true);
            mview.setOnClickListener(view -> click(itemCur));
        }
        return convertView;
    }

    public abstract void click(ItemSearch itemCur);

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<ItemSearch> suggestion = new ArrayList<>(itemSearch);

            results.values = suggestion;
            results.count = suggestion.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ItemSearch) resultValue).getTitle();
        }
    };
}
