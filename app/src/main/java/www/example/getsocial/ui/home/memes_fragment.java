package www.example.getsocial.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import www.example.getsocial.MySingleton;
import www.example.getsocial.NavigationActivity;
import www.example.getsocial.R;

import org.json.JSONException;
import org.json.JSONObject;

public class memes_fragment extends Fragment {


    private GestureDetectorCompat gestureDetector;
    public ImageView likeMeme;
    private ImageView memeImageView;
    private ProgressBar progressBar;
    private Animation animation;
    private Button shareButton;
    private String currentImageUrl = null;

    public memes_fragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memes_fragment, container, false);

        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.notificationBarColor));

        Toast.makeText(getContext(), "Swipe up for more", Toast.LENGTH_SHORT).show();
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.shrink_grow);

        memeImageView = view.findViewById(R.id.memeImageView);
        progressBar = view.findViewById(R.id.progressBar);
        likeMeme = view.findViewById(R.id.likeMeme);
        shareButton = view.findViewById(R.id.shareButton);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMeme(view);
            }
        });

        gestureDetector = new GestureDetectorCompat(getContext(), new DairyGestureListener());

        loadMeme();
        memeImageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        return view;
    }



    public class DairyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            likeMeme.startAnimation(animation);
            likeMeme.setAlpha(1f);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    likeMeme.setAlpha(0f);
                    // likeMeme.animate().scaleX(0f).scaleY(0f).setDuration(1500);
                }
            }, 500);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {

            int Swipe_Threshold = 100;
            int Swipe_Velocity_Threshold = 100;

            float diffx = moveEvent.getX() - downEvent.getX();
            float diffy = moveEvent.getY() - downEvent.getY();

            if (Math.abs(diffx) < Math.abs(diffy)) {
                //up or down swipe
                if (Math.abs(diffy) > Swipe_Threshold && Math.abs(velocityY) > Swipe_Velocity_Threshold) {
                    if (diffy < 0) {
                        //down swipe
                        loadMeme();
                    } else {
                        //up swipe
                    }
                    return true;
                }
            }
            return super.onFling(downEvent, moveEvent, velocityX, velocityY);

        }
    }

    private void loadMeme() {
        progressBar.setVisibility(View.VISIBLE);
        final String[] url = {"https://meme-api.herokuapp.com/gimme"};
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentImageUrl = response.getString("url");
                            Log.d("log", currentImageUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Glide.with(getContext()).load(currentImageUrl).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).into(memeImageView);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void shareMeme(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey! Checkout this cool meme I got from reddit " + currentImageUrl);
        Intent chooser = Intent.createChooser(intent, "Share this meme using..");
        startActivity(chooser);
    }



}
