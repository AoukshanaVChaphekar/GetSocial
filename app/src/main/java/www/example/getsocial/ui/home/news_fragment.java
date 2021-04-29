package www.example.getsocial.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import www.example.getsocial.MySingleton;
import www.example.getsocial.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class news_fragment extends Fragment {
    private RecyclerView recyclerView;
    ArrayList<String> strings;
    NewsListAdapter newsListAdapter= HomeFragment.newsListAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_news_fragment, container, false);


        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.black));

        fetch_data();
        try {
            getHeaders();
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }


        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsListAdapter);

        return view;
    }
    private void fetch_data() {
        String url = "https://saurav.tech/NewsAPI/top-headlines/category/health/in.json";
        final JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<news_data> newsDatalist = new ArrayList<>();
                        try {
                            JSONArray jsonArrayNews = response.getJSONArray("articles");

                            for (int i = 0; i < jsonArrayNews.length(); i++) {
                                JSONObject json = jsonArrayNews.getJSONObject(i);
                                news_data newsData = new news_data(json.getString("title"), json.getString("author"),
                                        json.getString("url"), json.getString("urlToImage"));
                                newsDatalist.add(newsData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        newsListAdapter.updateNews(newsDatalist);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObject);

    }
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();

        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }


}