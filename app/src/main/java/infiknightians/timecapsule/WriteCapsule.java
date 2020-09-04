package infiknightians.timecapsule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class WriteCapsule extends AppCompatActivity {

    private EditText ET1;
    private Button B1;
    private  String personEmail, personName;

    DatabaseReference ref;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_capsule);

        ET1 = findViewById(R.id.ET1);
        B1 = findViewById(R.id.B1);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
        }

        ref = FirebaseDatabase.getInstance().getReference("Capsule");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDataEntered())
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(WriteCapsule.this);
                    builder.setTitle("Are you sure ?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String message = ET1.getText().toString().trim();
                            Capsule C = new Capsule();
                            Random random = new Random();
                            int num = random.nextInt(50);
                            num = num + 13;
                            String encryptedMessage = "";
                            for(int i = 0; i < message.length(); i++)
                            {
                                encryptedMessage +=  Character.toString((char) ((int) message.charAt(i) + num));
                            }
                            C.setCapsule(encryptedMessage);
                            C.setUserId(personEmail);
                            C.setName(personName);
                            Date date = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
                            String today = formatter.format(date);
                            C.setDate(today);
                            ref.child(Integer.toString(count + 1)).setValue(C);
                            C.sendMail(num);
                            Toast.makeText(getApplicationContext(), "Your capsule has been successfully submitted. Check email for your private key.", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(WriteCapsule.this, Home.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });



    }


    boolean isEmpty(EditText text)
    {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }



    boolean checkDataEntered() {

        if (isEmpty(ET1)) {
            Toast.makeText(getApplicationContext(), "Message field cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }


}
