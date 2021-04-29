package www.example.getsocial.ui.UpdateUserName;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import www.example.getsocial.NavigationActivity;
import www.example.getsocial.R;
import www.example.getsocial.ui.home.HomeFragment;

public class UserNameFragment extends Fragment {

    TextView username;
    private DatabaseReference databaseReference;
    Button update;
    String email;
    private TextView success;
    private TextView userName= NavigationActivity.username;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update_username, container, false);
        username=root.findViewById(R.id.username_updated);
        update=root.findViewById(R.id.updateButton);

        success=root.findViewById(R.id.success);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().trim().length()>0) {

                    getResult(new call() {
                        @Override
                        public void callBack() {
                            userName.setText(username.getText().toString());
                            success.setVisibility(View.VISIBLE);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    success.setVisibility(View.INVISIBLE);
                                    username.setText("");
                                }
                            }, 5000);

                        }
                    });


                }
                else
                    Toast.makeText(getContext(),"Please all details",Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
    public void getResult(call callBack)
    {

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        email=user.getEmail();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("mail").getValue().toString().equals(email)) {
                            String id = dataSnapshot.getKey();
                            databaseReference.child(id).child("userName").setValue(username.getText().toString());


                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            if (dataSnapshot1.child("createdBy").child("mail").getValue().toString().equals(email)) {
                                                String id2 = dataSnapshot1.getKey();
                                                databaseReference.child(id2).child("createdBy")
                                                        .child("userName").setValue(username.getText().toString());
                                            }
                                        }
                                    }
                                    callBack.callBack();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public interface call
    {
        void callBack();
    }
}