package www.example.getsocial.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import www.example.getsocial.Models.Post;
import www.example.getsocial.Models.User;
import www.example.getsocial.R;

public class CreatePostActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private EditText postInput;
    private Button postButton;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();


        postButton=findViewById(R.id.postButton);
        postInput=findViewById(R.id.postInput);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=postInput.getText().toString().trim();
                if(input!=null) {
                    if (currentUser != null) {
                        getResult(new callBack() {
                            @Override
                            public void onCallBack(User user) {
                                Long currentTime=System.currentTimeMillis();
                                ArrayList<String> likedBy=new ArrayList<>();
                                likedBy.add("0");
                                Post post=new Post(input,user,currentTime,likedBy);
                                databaseReference.child("Posts").push().setValue(post);
                            }
                        });
                    }
                   // startActivity(new Intent(CreatePostActivity.this,post_fragment.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Empty fields not allowed!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getResult(callBack call)
    {
        FirebaseUser currentUser= firebaseAuth.getCurrentUser();
        String currentUserMail= currentUser.getEmail();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    if(snapshot1.child("mail").getValue().toString().equals(currentUserMail))
                    {
                        String userEmail=snapshot1.child("mail").getValue().toString();
                        String userPassword=snapshot1.child("password").getValue().toString();
                        String userName=snapshot1.child("userName").getValue().toString();
                        String profilePic=snapshot1.child("profilePicUrl").getValue().toString();
                        user=new User(userName,userEmail,userPassword,profilePic);
                    }
                }
                call.onCallBack(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private interface callBack
    {
        void onCallBack(User user);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}