package currency.calculator;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import currency.calculator.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Cursor cursor;
    private ImageButton delete, itemView;
    private Context context;

    public UserAdapter(Context context, Cursor cursor) {
        this.context = context;
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

            itemView = holder.itemView.findViewById(R.id.editButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    // Inflate the edit dialog layout and set up its UI elements
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View editDialogView = inflater.inflate(R.layout.edit_user_info, null);

                    EditText editUserName = editDialogView.findViewById(R.id.edit_username);
                    EditText editUserPassword = editDialogView.findViewById(R.id.edit_password);

                    String userName = cursor.getString(cursor.getColumnIndex("name"));
                    String userPassword = cursor.getString(cursor.getColumnIndex("password"));

                    editUserName.setText(userName);
                    editUserPassword.setText(userPassword);

                    // Show the edit dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(editDialogView);
                    builder.setTitle("Edit profile");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String updatedUserName = editUserName.getText().toString();
                            String updatedUserPassword = editUserPassword.getText().toString();

                            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
                            SharedPreferences sharedPreferences = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                            String loggedInUsername = sharedPreferences.getString("username", "");
                            int userId = myDatabaseHelper.getUserId(loggedInUsername);

                            boolean success = myDatabaseHelper.updateUser(userId,updatedUserName, updatedUserPassword);

                            if (success) {
                                Toast.makeText(context, "profile updated", Toast.LENGTH_SHORT).show();
                                // Refresh the list
                                Cursor newCursor = myDatabaseHelper.readUser(userId);
                                swapCursor(newCursor);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "failed to update profile", Toast.LENGTH_SHORT).show();
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