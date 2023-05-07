package currency.calculator;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private Cursor cursor;
    private ImageButton delete, itemView;
    private Context context;

    public FavouritesAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }
    @Override
    @NonNull
    public FavouritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_table_text, parent, false);
        return new FavouritesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {


            String favID = cursor.getString(cursor.getColumnIndex("_id"));
            String fav_con_from = cursor.getString(cursor.getColumnIndex("from_currency_fav"));
            String fav_con_to = cursor.getString(cursor.getColumnIndex("to_currency_fav"));

            holder.favIDView.setText(favID);
            holder.favConFromView.setText(fav_con_from);
            holder.favConToView.setText(fav_con_to);

            delete = holder.itemView.findViewById(R.id.delete_favorite);
            itemView = holder.itemView.findViewById(R.id.editButton);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int favoriteId = cursor.getInt(cursor.getColumnIndex("_id"));
                    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
                    boolean success = myDatabaseHelper.deleteFavourites(favoriteId);

                    if (success) {
                        Toast.makeText(context, "Favorite conversion deleted", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                        String loggedInUsername = sharedPreferences.getString("username", "");

                        int userID = myDatabaseHelper.getUserId(loggedInUsername);
                        Cursor newCursor = myDatabaseHelper.readFavourites(userID);
                        swapCursor(newCursor);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Failed to delete favorite conversion", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int favoriteId = cursor.getInt(cursor.getColumnIndex("_id"));

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View editDialogView = inflater.inflate(R.layout.edit_favourites, null);

                    EditText editConversionFrom = editDialogView.findViewById(R.id.edit_conversion_from);
                    EditText editConversionTo = editDialogView.findViewById(R.id.edit_conversion_to);

                    String conversion_From = cursor.getString(cursor.getColumnIndex("from_currency_fav"));
                    String conversion_To = cursor.getString(cursor.getColumnIndex("to_currency_fav"));

                    editConversionFrom.setText(conversion_From);
                    editConversionTo.setText(conversion_To);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(editDialogView);
                    builder.setTitle("Edit Favorite Conversion");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String updatedConversionFrom = editConversionFrom.getText().toString();
                            String updatedConversionTo = editConversionTo.getText().toString();

                            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
                            SharedPreferences sharedPreferences = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                            String loggedInUsername = sharedPreferences.getString("username", "");
                            int userId = myDatabaseHelper.getUserId(loggedInUsername);

                            boolean success = myDatabaseHelper.updateFavourites(favoriteId, userId, updatedConversionFrom, updatedConversionTo);

                            if (success) {
                                Toast.makeText(context, "Favorite conversion updated", Toast.LENGTH_SHORT).show();
                                // Refresh the list
                                Cursor newCursor = myDatabaseHelper.readFavourites(userId);
                                swapCursor(newCursor);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "Failed to update favorite conversion", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }
            });

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
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView favIDView, favConFromView, favConToView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favIDView = itemView.findViewById(R.id.fav_id);
            favConFromView = itemView.findViewById(R.id.fav_con_from);
            favConToView = itemView.findViewById(R.id.fav_con_to);
        }
    }
}
