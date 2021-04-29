package www.example.getsocial;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import www.example.getsocial.Models.Post;

public class PostAdapter extends FirebaseRecyclerAdapter<Post, PostAdapter.PostViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    IPostAdapter listener;
    public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options,IPostAdapter listener) {
        super(options);
        this.listener=listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post post) {
        holder.userName.setText(post.getCreatedBy().getUserName());
        holder.postTitle.setText(post.getText());
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
//        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            final String pictureUrl = dataSnapshot.child("profilePicUrl").getValue(String.class);
//                            Glide.with(holder.userImage.getContext()).load(pictureUrl).into(holder.userImage);
//                        }
//                        notifyDataSetChanged();
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });
        Glide.with(holder.userImage.getContext()).load(post.getCreatedBy().getProfilePicUrl()).circleCrop().into(holder.userImage);
        holder.likeCount.setText(String.valueOf(post.getLikedBy().size()-1));
        holder.createdAt.setText(Utils.getlongtoago(post.getCreatedAt()));
      //  holder.cardView.setBackgroundResource(R.drawable.edit_text_border);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(post.getLikedBy().contains(user.getUid()))
        {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.getContext(),R.drawable.ic_liked));
        }
        else
        {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.getContext(),R.drawable.ic_unliked));
        }


    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        PostViewHolder postViewHolder=new PostViewHolder(view);


        postViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               listener.onLikedItemClicked(getSnapshots().getSnapshot(postViewHolder.getAdapterPosition()).getKey());
               }
        });
        return postViewHolder;
       }


    class PostViewHolder extends RecyclerView.ViewHolder
    {

        ImageView userImage;
        TextView userName;
        TextView createdAt;
        TextView postTitle;
        ImageView likeButton;
        TextView likeCount;
        CardView cardView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.userImage);
            userName=itemView.findViewById(R.id.user);
            createdAt=itemView.findViewById(R.id.createdAt);
            postTitle=itemView.findViewById(R.id.postTitle);
            likeButton=itemView.findViewById(R.id.likeButton);
            likeCount=itemView.findViewById(R.id.likeCount);
            cardView=itemView.findViewById(R.id.cardView);

        }
    }
    public interface IPostAdapter
    {
        void onLikedItemClicked(String postId);
    }
}
