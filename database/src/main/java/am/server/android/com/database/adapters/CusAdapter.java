package am.server.android.com.database.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import am.server.android.com.database.R;
import am.server.android.com.database.bean.UserBean;


/**
 * Created by ${lixiang} on 2018/6/27.
 */

public class CusAdapter extends RecyclerView.Adapter<CusAdapter.WordViewHolder> {

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<UserBean> mWords; // Cached copy of words

    public CusAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (mWords != null) {
            UserBean current = mWords.get(position);
            holder.wordItemView.setText(current.getId()+","+current.getName()+","
            +current.getAge()+","+current.getSex()+","+current.getHeight());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No FirstName");
        }
    }

    public void setWords(List<UserBean> words){
        mWords = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWords != null) {
            return mWords.size();
        } else {
            return 0;
        }
    }
}