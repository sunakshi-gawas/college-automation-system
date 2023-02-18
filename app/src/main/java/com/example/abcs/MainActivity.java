package com.example.abcs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    //delclaration
   private  Button bt_login, reg;
   private EditText et_email;
   private EditText et_pass;
   private TextView tv_jump;
   private TextView tv_jump2;
   private TextView resetpass;

   TextView error;

   private FirebaseAuth auth;
    FirebaseFirestore fstore;
    // [END auth_fui_create_launcher]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //createSignInIntent();

        error=findViewById(R.id.textView154);
        bt_login=findViewById(R.id.btn_login);
        et_email=findViewById(R.id.txt_email);
        et_pass=findViewById(R.id.txt_password);
        tv_jump=findViewById(R.id.txt_view_jump);
        tv_jump2=findViewById(R.id.txt_view_jump2);
        fstore=FirebaseFirestore.getInstance();
        resetpass=findViewById(R.id.reset);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                error.setVisibility(View.INVISIBLE);
            }
        }, 100);






//firbase intialize
        auth = FirebaseAuth.getInstance();
       //temp jump page for testing purpose
        //all button code
        checkCurrentUser();


            resetpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText resetMail = new EditText(v.getContext());
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle("Reset Password");
                    passwordResetDialog.setMessage("Enter your email to received password reset link");
                    passwordResetDialog.setView(resetMail);
                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //email sent on reset link
                            String mail = resetMail.getText().toString();
                            auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Reset link sent to email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error! Reset link not send"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close the dialog
                        }
                    });

                    passwordResetDialog.create().show();

                }
            });
                        bt_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tex_email=et_email.getText().toString();
                                String tex_pass=et_pass.getText().toString();

//                                Intent intent=new Intent(MainActivity.this,HomeActivity.class);
//
                               // startActivity(intent);
//Admin login
                                DocumentReference documentReference=fstore.collection("Ademo").document(tex_email);
                                documentReference.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        String s,s1;
                                        String m=et_email.getText().toString();
                                        String p=et_pass.getText().toString();


                                        s=value.getString("Email");
                                        s1=value.getString("Password");


                                        if(m.equals(s)&&p.equals(s1)){
                                            Toast.makeText(MainActivity.this, "Account user loging done", Toast.LENGTH_SHORT).show();
                                            Intent intent1=new Intent(MainActivity.this,Account_Home.class);
                                            intent1.putExtra("Teacher_email",m);

                                            startActivity(intent1);


                                        }




                                    }
                                });

//techer login commited
                                DocumentReference document =fstore.collection("tdemo").document(tex_email);
                                document.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        String s,s1;
                                        String m=et_email.getText().toString();
                                        String p=et_pass.getText().toString();


                                        s=value.getString("Email");
                                        s1=value.getString("Password");


                                        if(m.equals(s)&&p.equals(s1)){
                                            Toast.makeText(MainActivity.this, "teacher loging done", Toast.LENGTH_SHORT).show();
                                            Intent intent1=new Intent(MainActivity.this,TeacherHome.class);
                                            intent1.putExtra("Teacher",m);

                                            startActivity(intent1);


                                        }

                                        if(tex_email.equals("Admin")&&  tex_pass.equals("admin")){
                                            Intent intent1=new Intent(MainActivity.this,Adminhome.class);
                                            startActivity(intent1);
                                        }

                                        else if(tex_email.equals("admin@gmail.com")&&  tex_pass.equals("admin1")){
                                            Intent intent1=new Intent(MainActivity.this,Adminhome.class);
                                            startActivity(intent1);
                                        }
                                        else{
                                            loginuser(tex_email,tex_pass);
                                        }





                                    }
                                });



                            }
                        });

                                tv_jump2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(MainActivity.this,social_media_login_Activity.class);
                                        startActivity(intent);
                                    }
                                });

    }
    private void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();

        } else {
            // No user is signed in

            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
        }
    }
    private void loginuser(String _email, String _pass) {
        auth.signInWithEmailAndPassword(_email,_pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(MainActivity.this, " login", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                intent.putExtra("home_email",_email);
               intent.putExtra("home_pass",_pass);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        error.setVisibility(View.INVISIBLE);
                    }
                }, 100);

//                Toast.makeText(MainActivity.this, "login Fail", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, "Enter Correct Username And Password", Toast.LENGTH_SHORT).show();
            }
        });

    }



}