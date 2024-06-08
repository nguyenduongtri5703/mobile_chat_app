package com.example.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.model.ChatroomModel;
import com.example.mychat.model.UserModel;
import com.example.mychat.utils.AndroidUtil;
import com.example.mychat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    String chatroomId;
    ChatroomModel chatroomModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.chat_recycler_view);
        otherUsername = findViewById(R.id.other_username);


        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchUserActivity.class);
            startActivity(intent);
//            onBackPressed();
        });


        otherUsername.setText(otherUser.getUsername());
        getOnCreateChatroomModel();
    }

    void getOnCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               chatroomModel = task.getResult().toObject(ChatroomModel.class);
               if(chatroomModel == null){
                   chatroomModel = new ChatroomModel(
                           chatroomId,
                           Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                           Timestamp.now(),
                           ""
                   );
                   FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
               }
           }
        });
    }

}