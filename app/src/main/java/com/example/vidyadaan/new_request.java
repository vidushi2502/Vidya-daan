package com.example.vidyadaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;

public class new_request extends AppCompatActivity {

    Spinner type_drop;
    Button postbtn;
    EditText desc;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference,reference_id,reference_ngo;
    String id,don_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        type_drop = findViewById(R.id.type_drop);
        postbtn = findViewById(R.id.postbtn);
        desc = findViewById(R.id.desid);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.dropitems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        type_drop.setAdapter(adapter);

        type_drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                don_type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        String description = desc.getText().toString();
                        if(don_type.equals("Select Type")){
                            Toast.makeText(new_request.this, "Select Type of Donation", Toast.LENGTH_SHORT).show();
                        }
                        else if (description.isEmpty()){
                            desc.setError("Enter A Description");
                        }
                        else{
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            reference = firebaseDatabase.getReference("Ngo_Requests");
                            reference_ngo = firebaseDatabase.getReference("NGOs");
                            reference_id = firebaseDatabase.getReference("UID");
                            try {
                                String filename = "Userdetail.txt";
                                FileInputStream fin = openFileInput(filename);
                                int a,n=0;
                                StringBuilder temp = new StringBuilder();
                                while ((a = fin.read()) != -1) {
                                    if(n<2){
                                        n++;
                                        continue;
                                    }
                                    temp.append((char) a);
                                }
                                String user = temp.toString();
                                Query check_id = reference_ngo.orderByChild("id").equalTo(user);
                                check_id.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if(snapshot1.exists()){
                                            String pin = snapshot1.child(user).child("pin").getValue().toString();
                                            reference_id.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    id = snapshot.child("Request_ID").getValue().toString();
                                                    Database_request db = new Database_request(user,id,don_type,description,"0",pin);
                                                    reference.child(id).setValue(db);
                                                    reference_id.child("Request_ID").setValue((Integer.parseInt(id)+1)+"");
                                                    Toast.makeText(new_request.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                                                    startNewActivity();
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            catch (Exception e){
                                System.out.println(e);
                                Toast.makeText(new_request.this, ""+e, Toast.LENGTH_SHORT).show();
                            }
                        }
            }
        });

    }

    private void startNewActivity() {
        Intent intent = new Intent(new_request.this,profile_ngo.class);
        startActivity(intent);
    }
}