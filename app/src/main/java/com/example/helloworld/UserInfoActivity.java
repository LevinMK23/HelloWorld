package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseAuth authService;
    private List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        RecyclerView recyclerView = findViewById(R.id.user_info_rv);
        LinearLayoutManager manager = new LinearLayoutManager(UserInfoActivity.this);
        recyclerView.setLayoutManager(manager);
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    Log.d("users_info", user.toString());
                    usersList.add(user);
                }
                for (int i = 0; i < 150; i++) {
                    usersList.add(new User("UserName" + i, "UserSurname" + i));
                }
                UserAdapter adapter = new UserAdapter(usersList);
                //adapter.setHasStableIds(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String uId = getIntent().getStringExtra("userId");
        Log.d("users", usersList.toString());

    }

    private class UserAdapter extends RecyclerView.Adapter<UserHolder> {

        private List<User> userList;

        public UserAdapter(List<User> userList) {
            this.userList = userList;
        }



        @NonNull
        @Override
        public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = getLayoutInflater().inflate(R.layout.user_item_layout, parent, false);
            return new UserHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull UserHolder holder, int position) {
            User user = userList.get(position);
            holder.itemView.setOnClickListener(view -> {
                notifyItemRemoved(position);
                userList.remove(user);
            });
            holder.setName(user.getName());
            holder.setSurname(user.getSurname());
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }

    private class UserHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView surname;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_user_name);
            surname = itemView.findViewById(R.id.item_user_surname);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setSurname(String surname) {
            this.surname.setText(surname);
        }

    }
}
