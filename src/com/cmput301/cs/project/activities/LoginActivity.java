package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.controllers.LoginController;

/**
 * An activity that is called when the app starts with no user currently 'logged in'. Allows the user to login using there name
 * and the app generates a {@link com.cmput301.cs.project.models.User User} for use in the app.
 * 
 * @author rozsa
 *
 */

public class LoginActivity extends Activity {
    private LoginController loginController;
    private EditText mName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        loginController = new LoginController(this);

        mName = (EditText) findViewById(R.id.name);
    }

    public void login(View view) {
        String name = mName.getText().toString();

        if(name.isEmpty()) {
            mName.setError("Name must not be empty");
        } else {
            loginController.attemptLogin(name);

            if(App.get(this).getUser() == null) {
                return;
            }

            startActivity(new Intent(this, ClaimListActivity.class));

            finish();
        }

    }

}