package com.mcal.kotlin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mcal.kotlin.R;
import com.mcal.kotlin.view.MainView;

import java.util.ArrayList;

import static com.mcal.kotlin.data.Constants.getResPath;
import static com.mcal.kotlin.utils.LessonUtils.getLessonNumberByTitle;
import static com.mcal.kotlin.utils.LessonUtils.isRead;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Filterable {
    private SearchFilter filter;
    private ArrayList<String> items;
    private MainView mainView;

    public ListAdapter(ArrayList<String> items, MainView mainView) {
        this.items = items;
        this.mainView = mainView;
        filter = new SearchFilter();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position) {
        final String text = items.get(position);
        final int number = getLessonNumberByTitle(text);

        try {
            if (getClass().forName("cc.binmt.signature.PmsHookApplication") != null ||
                    getClass().forName("anymy.sign.BinSignatureFix") != null ||
                    getClass().forName("apkeditor.patch.signature.Fix") != null ||
                    getClass().forName("com.anymy.reflection") != null ||
                    getClass().forName("bin.mt.apksignaturekillerplus.HookApplication") != null ||
                    getClass().forName("cc.binmt.signature.Hook") != null) return;
        } catch (ClassNotFoundException e) {
            holder.itemText.setText(text);
            holder.item.setOnClickListener(p1 -> mainView.openLesson(getResPath() + "/lesson_" + number + ".html", holder.getAdapterPosition()));
        }

        // ставим галочку если урок прочитанный
        holder.checkMark.setVisibility(isRead(number) ? View.VISIBLE : View.GONE);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int p2) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView itemText;
        ImageView checkMark;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.lesson_item);
            itemText = view.findViewById(R.id.text);
            checkMark = view.findViewById(R.id.checkMark);
        }
    }

    private class SearchFilter extends Filter {
        private ArrayList<String> items_backup = items;
        private ArrayList<String> filteredItems = new ArrayList<>();

        @Override
        protected Filter.FilterResults performFiltering(CharSequence p1) {
            filteredItems.clear();

            for (int x = 0; x < items_backup.size(); x++) {
                String query = p1.toString().toLowerCase();
                if (items_backup.get(x).toLowerCase().contains((query))) {
                    filteredItems.add(items_backup.get(x));
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence p1, Filter.FilterResults p2) {
            items = filteredItems;
            notifyDataSetChanged();
        }
    }
}