package com.example.googlemappractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.text.TextUtils.isEmpty;

public class SignUpActivity extends AppCompatActivity {
  private static final String TAG = "SignUpActivity";

  @BindView(R.id.input_email)
    EditText mEmail;
  @BindView(R.id.input_password)
  EditText mPassword;
  @BindView(R.id.input_confirm_password)
  EditText mConfirmpassword;
  @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
      mAuth=FirebaseAuth.getInstance();
      database = FirebaseDatabase.getInstance();
      //get firebase database instance and reference
      mDatabaseReference= database.getReference().child("Users");
      user=new User();
    }

  private void showDialog(){
    mProgressBar.setVisibility(View.VISIBLE);

  }

  private void hideDialog(){
    if(mProgressBar.getVisibility() == View.VISIBLE){
      mProgressBar.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * Register a new email and password to Firebase Authentication
   * @param email
   * @param password
   */
  public void registerNewEmail(final String email, String password){

    showDialog();

    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                if (task.isSuccessful()){
                  Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                  //insert some default data
                  User user = new User();
                  user.setEmail(email);
                  user.setUsername(email.substring(0, email.indexOf("@")));
                  user.setUser_id(FirebaseAuth.getInstance().getUid());
                  mDatabaseReference.child(user.getUser_id()).setValue(user);
                  Toast.makeText(SignUpActivity.this, "Save User", Toast.LENGTH_SHORT).show();
                  redirectLoginScreen();
                }
                else {
                  View parentLayout = findViewById(android.R.id.content);
                  Toast.makeText(SignUpActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                  hideDialog();
                }

                // ...
              }
            });
  }

  /**
   * Redirects the user to the login screen
   */
  private void redirectLoginScreen(){
    Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
  }

  @OnClick(R.id.btn_register)
  void register(){
    Log.d(TAG, "onClick: attempting to register.");

    //check for null valued EditText fields
    if(!isEmpty(mEmail.getText().toString())
            && !isEmpty(mPassword.getText().toString())
            && !isEmpty(mConfirmpassword.getText().toString())){

      //check if passwords match
      if(doStringsMatch(mPassword.getText().toString(), mConfirmpassword.getText().toString())){

        //Initiate registration task
        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());
      }else{
        Toast.makeText(SignUpActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
      }

    }else{
      Toast.makeText(SignUpActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Return true if the @param is null
   * @param string
   * @return
   */
  public static boolean isEmpty(String string){
    return string.equals("");
  }

  /**
   * Return true if @param 's1' matches @param 's2'
   * @param s1
   * @param s2
   * @return
   */
  public static boolean doStringsMatch(String s1, String s2){
    return s1.equals(s2);
  }

}
