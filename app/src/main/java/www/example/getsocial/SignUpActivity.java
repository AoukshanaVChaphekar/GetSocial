package www.example.getsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import www.example.getsocial.Models.User;

public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 65;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    EditText email, password, username;
    Button signup, btn_google;
    TextView signin;
    String url;
    String uemail;
    Boolean flag=false;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //auth.getCurrentUser().getEmail()
//        Log.d("hahaactivity",auth.getCurrentUser().getEmail());
        signup = findViewById(R.id.btn_signUp);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        username = findViewById(R.id.et_username);
        signin = findViewById(R.id.alreadyAccount);
        btn_google = findViewById(R.id.btn_google);

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("profile_photos");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().trim().length() > 0 &&
                        password.getText().toString().trim().length() > 0 &&
                        username.getText().toString().trim().length() > 0
                ) {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword
                            (email.getText().toString(), password.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        getResult(new callback() {
                                            @Override
                                            public void onCallBack(String url) {
                                                User user = new User(username.getText().toString(), email.getText().toString(), password.getText().toString(), url);
                                                Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                String id = task.getResult().getUser().getUid();
                                                database.getReference().child("Users").child(id).setValue(user);
                                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                finish();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("user not created", "user not created");
                                    }

                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Log.d("button", "google button clicked");
                signIn();
                progressDialog.dismiss();
            }
        });


//        //Log.d("user",auth.getCurrentUser().getEmail());
//        if (auth.getCurrentUser() != null) {
//            Log.d("state","in auth.getcurrentUser not equal to null");
//            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
//            startActivity(intent);
//        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();
        Log.d("state","in onActivityResult");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getEmail().toString());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.d("state","in firebaseAuthWithGoogle");


        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            uemail=user.getEmail();

                            //check if user already exists
                            getEmail(new call() {
                                @Override
                                public void callBack(Boolean flag) {
                                    if(flag)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"User already exists!",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this, NavigationActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Log.d("Signup","in else");
                                        getResult(new callback() {
                                            @Override
                                            public void onCallBack(String url) {
                                                String username=user.getDisplayName();
                                                String password="null";
                                                User user = new User(username, uemail, password, url);
                                                Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                String id = task.getResult().getUser().getUid();
                                                database.getReference().child("Users").child(id).setValue(user);
                                                // startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

                                                Intent intent = new Intent(SignUpActivity.this, NavigationActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }


    public void getResult(callback callback) {
        StorageReference reference = storageReference.child("profile_photo.png");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
                callback.onCallBack(url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fail", e.getMessage());
            }
        });

    }

    public void getEmail(call callback)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        if(dataSnapshot.child("mail").getValue().toString().equals(uemail))
                        {
                            flag=true;
                        }

                    }

                }
                else
                    flag=false;

                callback.callBack(flag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    interface callback {
        public void onCallBack(String url);
    }
    interface  call
    {
        void callBack(Boolean flag);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null)
        {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}


