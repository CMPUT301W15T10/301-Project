import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;


public class ClaimsActivity extends Activity {

    public final int LOGIN_REQUEST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claims_activity);

        App app = App.get(this);

        if(app.getUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}