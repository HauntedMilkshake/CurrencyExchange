package currency.calculator;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ViewHolder>{
    private Cursor cursor;

    public ConversionAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ConversionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversion_table, parent, false);
        return new ConversionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionAdapter.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String conversionID = cursor.getString(cursor.getColumnIndex("_id"));
            String userID = cursor.getString(cursor.getColumnIndex("user_id"));
            String conversionFrom = cursor.getString(cursor.getColumnIndex("conversion_from"));
            String conversionTo = cursor.getString(cursor.getColumnIndex("conversion_to"));
            String conversionAmount = cursor.getString(cursor.getColumnIndex("conversion_amount"));

            holder.conversionIDView.setText(conversionID);
            holder.userIDView.setText(userID);
            holder.conversionFromView.setText(conversionFrom);
            holder.conversionToView.setText(conversionTo);
            holder.conversionAmountView.setText(conversionAmount);
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
        TextView conversionIDView, userIDView, conversionFromView, conversionToView, conversionAmountView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            conversionIDView = itemView.findViewById(R.id.id);
            userIDView = itemView.findViewById(R.id.user_id);
            conversionFromView = itemView.findViewById(R.id.conversion_from);
            conversionToView = itemView.findViewById(R.id.conversion_to);
            conversionAmountView = itemView.findViewById(R.id.conversion_amount);
        }
    }
}
