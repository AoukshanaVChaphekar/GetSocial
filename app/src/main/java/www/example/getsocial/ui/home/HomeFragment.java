package www.example.getsocial.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import www.example.getsocial.Models.Post;
import www.example.getsocial.PostAdapter;
import www.example.getsocial.R;

public class HomeFragment extends Fragment implements NewsItemClicked , PostAdapter.IPostAdapter {
    public static NewsListAdapter newsListAdapter;
    public static PostAdapter postAdapter;
    private DatabaseReference databaseReference;
    public static ViewPager viewPager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(),getChildFragmentManager());
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = root.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        newsListAdapter = new NewsListAdapter(this);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Posts");
        Query query=databaseReference.orderByChild("createdAt");
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        postAdapter=new PostAdapter(options,this);
        return root;
    }
    @Override
    public void OnItemClicked(news_data item) {
        String url = item.url;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }

    @Override
    public void onLikedItemClicked(String postId) {
        post_fragment post=new post_fragment();
        post.updateLikes(postId);

    }
    public static void updateUi()
    {
        postAdapter.notifyDataSetChanged();
    }

}