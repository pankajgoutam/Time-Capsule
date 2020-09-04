package infiknightians.timecapsule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    private EditText ET1;
    private ImageButton B1;
    private  String personEmail, personName;
    private ListView LV;

    private List<Message> list = new ArrayList<>();
    private List<Message> newlist = new ArrayList<>();
    ArrayAdapter<Message> adapter;

    private DatabaseReference ref;
    int count;
    private ProgressDialog progress;
    boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        ET1 = findViewById(R.id.ET1);
        B1 = findViewById(R.id.B1);
        LV = findViewById(R.id.LV);


        adapter = new ArrayAdapter<Message>(Chat.this, android.R.layout.simple_list_item_1, list);
        LV.setAdapter(adapter);
        final ImageButton ib = new ImageButton(Chat.this);

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final Message M = list.get(position);
                if(M.getUserId().equals(personEmail))
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                    builder.setTitle("Do you want to delete this message");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String child = Integer.toString(M.getId()) + personEmail.split("@")[0];
                            ref.child(child).removeValue();
                            Toast.makeText(Chat.this, "Message deleted.", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }

        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
        }

        ref = FirebaseDatabase.getInstance().getReference("Message");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        Message M = dataSnapshot.child(keys.getKey()).getValue(Message.class);
                        newlist.add(M);
                    }
                    Priority P = new Priority();
                    newlist = P.sortMessage(newlist);
                    list.clear();
                    for (Message M : newlist) {
                        list.add(M);
                        adapter.notifyDataSetChanged();
                    }
                    newlist.clear();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(ET1))
                    ET1.setError("Empty Message");
                else {
                    ET1.setError(null);
                    Message M = new Message();
                    M.setMessage(ET1.getText().toString().trim());
                    M.setName(personName);
                    M.setUserId(personEmail);
                    M.setId(count + 1);
                    String child = Integer.toString(count + 1) + personEmail.split("@")[0];
                    ref.child(child).setValue(M);
                    progress = new ProgressDialog(Chat.this);
                    progress.setTitle("Sending Message");
                    progress.setMessage("Please Wait");
                    progress.setCancelable(false);
                    progress.show();
                    progress.dismiss();
                    ET1.setText("");
                }
            }
        });

    }

    boolean isEmpty(EditText text)
    {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
