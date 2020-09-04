package infiknightians.timecapsule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class ReadCapsule extends AppCompatActivity {

    private ListView LV;

    private List<Capsule> list = new ArrayList<>();
    ArrayAdapter<Capsule> adapter;

    private DatabaseReference ref;

    private  String personEmail, personName;
    int key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_capsule);

        LV = findViewById(R.id.LV);

        adapter = new ArrayAdapter<Capsule>(ReadCapsule.this, android.R.layout.simple_list_item_1, list);
        LV.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("Capsule");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot keys : dataSnapshot.getChildren())
                {
                    Capsule C = dataSnapshot.child(keys.getKey()).getValue(Capsule.class);
                    list.add(C);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
        }


        LV.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final Capsule C = list.get(position);
                if(C.getUserId().equals(personEmail))
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ReadCapsule.this);
                    builder.setTitle("Do you want to decrypt this message");
                    builder.setPositiveButton("Decrypt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final AlertDialog.Builder builder1 = new AlertDialog.Builder(ReadCapsule.this);
                            builder1.setTitle("Enter your personal Key: ");
                            final EditText input = new EditText(ReadCapsule.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
                            builder1.setView(input);
                            builder1.setPositiveButton("Show", new DialogInterface.OnClickListener() {


                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    key = Integer.parseInt(input.getText().toString());
                                    if(isEmpty(input))
                                        Toast.makeText(ReadCapsule.this, "Empty Field", Toast.LENGTH_LONG).show();
                                    else {

                                        String message = C.getCapsule();
                                        String decryptedMessage = "";
                                        for(int i = 0; i < message.length(); i++)
                                        {
                                            decryptedMessage +=  Character.toString((char) ((int) message.charAt(i) - key));
                                        }
                                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(ReadCapsule.this);
                                        builder2.setTitle("Your Secret Capsule is : ");
                                        builder2.setMessage(decryptedMessage);
                                        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                               dialog.cancel();
                                            }
                                        });
                                        builder2.show();
                                    }

                                }

                            });
                            builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder1.show();
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



    }

    boolean isEmpty(EditText text)
    {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
