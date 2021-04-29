package www.example.getsocial.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import www.example.getsocial.R;
import www.example.getsocial.SignInActivity;


public class fragment_logout extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_logout, container, false);

        Intent loginscreen=new Intent(getContext(), SignInActivity.class);
        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginscreen);
        FirebaseAuth.getInstance().signOut();
        Log.d("state","in logout");
        getActivity().finish();
        return root;
    }
}