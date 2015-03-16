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
 * An activity that is called when the app starts with no user currently 'logged in'. Allows the user to login using there name
 * and the app generates a {@link com.cmput301.cs.project.model.User User} for use in the app.
 * 
 * @author rozsa
 *
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