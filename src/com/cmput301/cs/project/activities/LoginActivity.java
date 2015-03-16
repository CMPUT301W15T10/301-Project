package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.User;

/**
 * This activity is launched by App if a user is not logged in
 * It simply takes a string and has App save the user and return to ClaimListActivity
 */

public class LoginActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }

    public void login(View view) {
        EditText nameEditText = (EditText) findViewById(R.id.name);
        String name = nameEditText.getText().toString();

        if(name.isEmpty()) {
            nameEditText.setError("Name must not be empty");
        } else {
            User user = new User(name);
            App.get(this).createUser(user);


            //TODO: this line is suspect, maybe duplicate
            startActivity(new Intent(this, ClaimListActivity.class));

            finish();
        }

    }

}