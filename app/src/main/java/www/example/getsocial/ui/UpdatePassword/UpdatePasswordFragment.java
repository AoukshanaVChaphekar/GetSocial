package www.example.getsocial.ui.UpdatePassword;

import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import www.example.getsocial.MainActivity;
import www.example.getsocial.R;

public class UpdatePasswordFragment extends Fragment {

    TextView oldPassword;
    TextView confPassword;
    TextView newPassword;
    Button update;
   private String password;
    DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update_password, container, false);
        oldPassword = root.findViewById(R.id.old_password);
        confPassword = root.findViewById(R.id.conf_password);
        update = root.findViewById(R.id.update_pass_button);
        newPassword = root.findViewById(R.id.newpassword);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.child("mail").getValue().toString().equalsIgnoreCase(email))
                    {
                        password=dataSnapshot.child("password").getValue().toString();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        getCallback(new call() {
            @Override
            public void onCallback(String password) {
                if (password.equals("null")) {
                    confPassword.setEnabled(false);
                    newPassword.setEnabled(false);
                    oldPassword.setEnabled(false);
                    Toast.makeText(getContext(), "Cannot Change Password as logged in using Gmail", Toast.LENGTH_SHORT).show();
                }
            }
        });




        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!password.equalsIgnoreCase("null")) {
                    if (oldPassword.getText().toString().trim().length() > 0 && newPassword.getText().toString().trim().length() > 0 && newPassword.getText().toString().equals(confPassword.getText().toString())) {
                        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword.getText().toString());
                        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Something went wrong.Please try again", Toast.LENGTH_SHORT).show();
                                            } else {
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            String id = dataSnapshot.getKey();
                                                            if (dataSnapshot.child("mail").getValue().toString().equalsIgnoreCase(email)) {
                                                                databaseReference.child(id).child("password").setValue(newPassword.getText().toString());
                                                            }

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.d("Database error", error.getMessage());
                                                    }
                                                });

                                                Toast.makeText(getContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        confPassword.setText("");
                                                        newPassword.setText("");
                                                        oldPassword.setText("");
                                                    }
                                                }, 5000);


                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Enter Correct Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    Toast.makeText(getContext(), "Cannot Change Password as logged in using Gmail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    public void getCallback(call call)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
        String email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.child("mail").getValue().toString().equalsIgnoreCase(email))
                    {
                        Log.d("Password",dataSnapshot.child("password").getValue().toString());

                        if(dataSnapshot.child("password").getValue().toString().equalsIgnoreCase("null"))
                        {
                            password="null";
                        }
                    }
                }
                call.onCallback(password);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    interface call{
        public void onCallback(String password);
    }
}


