package www.example.getsocial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import www.example.getsocial.ui.home.HomeFragment;

import static www.example.getsocial.ui.home.HomeFragment.viewPager;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView imageView;
    private static final int RC_PHOTO_PICKER = 2;
    public static  TextView username;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private FirebaseStorage firebaseStorage;
    private PostAdapter postAdapter= HomeFragment.postAdapter;
    private View header;

    public Uri downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_password, R.id.nav_username,R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        header=navigationView.getHeaderView(0);
        imageView=header.findViewById(R.id.imageView);
        username=header.findViewById(R.id.username);


        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("profile_photos");



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_PHOTO_PICKER && resultCode==RESULT_OK)
        {
            Toast.makeText(getApplicationContext(),"Updating image",Toast.LENGTH_LONG).show();
            Uri selectedImage=data.getData();
            StorageReference reference= storageReference.child(selectedImage.getLastPathSegment());

            UploadTask task= reference.putFile(selectedImage);
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_LONG).show();
                }
            });
            Task<Uri> urlTask=task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful())
                        throw  task.getException();
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        downloadUri=task.getResult();
                        //save to database

                        getResult(new onCallback() {
                            @Override
                            public void call() {
                                //Glide.with(imageView.getContext()).load(dataSnapshot.child("profilePicUrl").getValue())
                                  //      .into(imageView);

                                databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                String email=user.getEmail();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot snapshot1:snapshot.getChildren())
                                        {
                                            if(snapshot1.child("mail").getValue().toString().equals(email))
                                            {
                                                Glide.with(imageView.getContext()).load(snapshot1.child("profilePicUrl").
                                                        getValue().toString()).circleCrop().into(imageView);
                                               // Picasso.get().load(snapshot1.child("profilePicUrl").getValue().toString()).into(imageView);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



//                                      Glide.with(getApplicationContext()).load(dataSnapshot.child("profilePicUrl").getValue())
//                                        .into(imageView);
//
                               Toast.makeText(getApplicationContext(), "Image changed successfully", Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Download failed!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {

            super.onBackPressed();
        }else {

            //If any other tab is open, then switch to first tab
            viewPager.setCurrentItem(0);
        }

    }

    public void getResult(onCallback callback)
    {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String email=user.getEmail();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.child("mail").getValue().toString().equals(email))
                    {
                        String id=dataSnapshot.getKey();
                        databaseReference.child(id).child("profilePicUrl").setValue(downloadUri.toString());

                        //update post's image

                        databaseReference=FirebaseDatabase.getInstance().getReference().child("Posts");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1:snapshot.getChildren())
                                {
                                    if(dataSnapshot1.child("createdBy").child("mail").getValue().toString().equals(email))
                                    {
                                        String id=dataSnapshot1.getKey();
                                        assert id != null;
                                        databaseReference.child(id).child("createdBy")
                                                .child("profilePicUrl").setValue(downloadUri.toString());

                                    }
                                }

                                callback.call();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public interface onCallback
    {
        void call();
    }

    @Override
    protected void onStart() {
        super.onStart();


        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("profile_photos");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String email=user.getEmail();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.child("mail").getValue().toString().equals(email))
                    {
                        Log.d("mail",email);
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("profilePicUrl").getValue())
                                .circleCrop().into(imageView);
                        username.setText(dataSnapshot.child("userName").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}