package com.example.damxat.Views.Fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.damxat.Adapter.RecyclerXatAdapter;
import com.example.damxat.Model.User;
import com.example.damxat.Model.Xat;
import com.example.damxat.Model.XatGroup;
import com.example.damxat.R;
import com.example.damxat.Views.Activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class  XatFragment extends Fragment {

    DatabaseReference ref;
    View view;
    FirebaseUser firebaseUser;
    String userid;
    Bundle bundle;
    Boolean isXatUser;
    ArrayList<Xat> arrayXats;
    ArrayList<String> arrayUsers;
    XatGroup group;
    String groupName;
    // creating a variable for medi recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    public XatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_xat, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Agafem l'instancia amb els components
        bundle = getArguments();

        if(bundle.getString("type").equals("xatuser")){
            isXatUser = true;
            getUserXat();
        }else{
            isXatUser = false;
            groupName = bundle.getString("group");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(groupName);
            readGroupMessages(groupName);
        }


        ImageButton btnMessage = view.findViewById(R.id.btnMessage);
        EditText txtMessage = view.findViewById(R.id.txtMessage);

        //Enviem un missatge
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMessage.getText().toString();

                if(!msg.isEmpty()){
                    sendMessage(firebaseUser.getUid(), msg, isXatUser);
                }else{
                    Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("");
            }
        });

        return view;
    }

    //Agafem el xat
    public void getUserXat(){
        if(getArguments()!=null) {
            userid = bundle.getString("user");

            //Agafem el xat de Firebase
            ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);

            //Mostrem el missatges
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Agafem l'usuari
                    User user = dataSnapshot.getValue(User.class);

                    //Posem el nom de l'usuari sobre el misatge
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(user.getUsername());

                    //Mostra el missatge
                    readUserMessages();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



    public void sendMessage(String sender, String message, boolean isXatUser){
        //Si estem dins del xat procedim
        if(isXatUser==true){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            String receiver = userid;
            Xat xat = new Xat(sender, receiver, message);
            ref.child("Xats").push().setValue(xat);
        }else{
            ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

            Xat xat = new Xat(sender, message);

            if(arrayXats==null) {
                arrayXats = new ArrayList<Xat>();
                arrayXats.add(xat);
            }else{
                arrayXats.add(xat);
            }

            if(group.getUsers()==null){
                arrayUsers = new ArrayList<String>();
                arrayUsers.add(firebaseUser.getUid());
            }else{
                if(!group.getUsers().contains(firebaseUser.getUid())){
                    arrayUsers.add(firebaseUser.getUid());
                }
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("xats", arrayXats);
            hashMap.put("users", arrayUsers);
            ref.updateChildren(hashMap);
        }
    }

    public void readUserMessages(){
        arrayXats = new ArrayList<>();

        //agafem l'instancia de firebase
        ref = FirebaseDatabase.getInstance().getReference("Xats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayXats.clear();

                //Per cada missatge fem aixo
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Xat xat = postSnapshot.getValue(Xat.class);
                    //Si l'usuari que rep es correcte o el que envia es correcte
                    if(xat.getReceiver().equals(userid) && xat.getSender().equals(firebaseUser.getUid()) ||
                            xat.getReceiver().equals(firebaseUser.getUid()) && xat.getSender().equals(userid)){
                        arrayXats.add(xat);
                        Log.i("logTest",xat.getMessage());
                    }
                }

                //Actualitzem la llista de misatges
                updateRecycler();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }


    public void readGroupMessages(String groupName){

        //Agafem l'instancia de firebase
        ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(XatGroup.class);

                arrayXats = group.getXats();

                if(arrayXats!=null) {
                    updateRecycler();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }

    public void updateRecycler(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerXat);
        RecyclerXatAdapter adapter = new RecyclerXatAdapter(arrayXats, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}