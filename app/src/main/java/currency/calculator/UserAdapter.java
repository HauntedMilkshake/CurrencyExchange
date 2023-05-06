package currency.calculator;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import currency.calculator.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Cursor cursor;

    public UserAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_table_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String username = cursor.getString(cursor.getColumnIndex("name"));
            String userID = cursor.getString(cursor.getColumnIndex("_id"));
            String userPassword = cursor.getString(cursor.getColumnIndex("password"));
            holder.userNameView.setText(username);
            holder.userIDView.setText(userID);
            holder.userPasswordView.setText(userPassword);
        }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIDView, userNameView, userPasswordView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIDView = itemView.findViewById(R.id.user_id);
            userNameView = itemView.findViewById(R.id.user_name);
            userPasswordView = itemView.findViewById(R.id.user_password);
        }
    }
}