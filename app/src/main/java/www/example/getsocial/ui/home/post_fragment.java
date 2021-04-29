package www.example.getsocial.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import www.example.getsocial.Models.Post;
import www.example.getsocial.NavigationActivity;
import www.example.getsocial.PostAdapter;
import www.example.getsocial.R;


public class post_fragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter= HomeFragment.postAdapter;
    private DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    FloatingActionButton fab;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.post_fragment, container, false);
        fab=root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreatePostActivity.class));
            }
        });

        recyclerView=root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true);
     //   layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(postAdapter);
        return root;

    }
    @Override
    public void onStart() {
        super.onStart();
        postAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }
    public void updateLikes(String postId)
    {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                databaseReference=FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> likedBy= (ArrayList<String>) snapshot.child("likedBy").getValue();

                        if (likedBy.contains(user.getUid()))
                        {
                            Log.d("Func","if");
                            likedBy.remove(user.getUid());

                        }
                        else {
                            Log.d("Func","else");
                            likedBy.add(user.getUid());

                        }
                        databaseReference.child("likedBy").setValue(likedBy);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
}