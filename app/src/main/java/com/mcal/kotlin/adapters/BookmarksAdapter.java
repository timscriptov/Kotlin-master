package com.mcal.kotlin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.mcal.kotlin.R;
import com.mcal.kotlin.data.Bookmarks;
import com.mcal.kotlin.data.Preferences;
import com.mcal.kotlin.view.BookmarksFragment;
import com.mcal.kotlin.view.MainView;

import java.util.ArrayList;

import static com.mcal.kotlin.data.Constants.getResPath;
import static com.mcal.kotlin.utils.LessonUtils.getLessonNumberByTitle;
import static com.mcal.kotlin.utils.LessonUtils.isRead;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {
    private ArrayList<String> items;
    private BookmarksFragment bookmarksFragment;
    private MainView mainView;

    public BookmarksAdapter(ArrayList<String> items, BookmarksFragment bookmarksFragment, MainView mainView) {
        this.items = items;
        this.bookmarksFragment = bookmarksFragment;
        this.mainView = mainView;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(final BookmarksAdapter.ViewHolder holder, final int position) {
        final String text = items.get(position);
        final int number = getLessonNumberByTitle(text);

        holder.itemText.setText(text);
        holder.item.setOnClickListener(p1 -> {
            bookmarksFragment.dismiss();
            mainView.openLesson(getResPath() + "/lesson_" + number + ".html#googtrans(ru|" + Preferences.getLang() + ")", holder.getAdapterPosition());
        });

        // ставим галочку если урок прочитанный
        holder.checkMark.setVisibility(isRead(number) ? View.VISIBLE : View.GONE);

        holder.removeBookmarkButton.setOnClickListener(v -> {
            Bookmarks.remove(number);
            items.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            if (getItemCount() == 0) bookmarksFragment.dismiss();
        });
    }

    @Override
    public BookmarksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int p2) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item, parent, false);
        return new BookmarksAdapter.ViewHolder(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView itemText;
        ImageView checkMark;
        SwipeLayout swipeLayout;
        AppCompatImageButton removeBookmarkButton;

        private ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.lesson_item);
            itemText = view.findViewById(R.id.text);
            checkMark = view.findViewById(R.id.checkMark);
            swipeLayout = view.findViewById(R.id.bookmark_swipe);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            removeBookmarkButton = view.findViewById(R.id.remove_bookmark);
        }
    }
}