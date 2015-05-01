package summoner.plus;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Register extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    private class RegisterUser extends AsyncTask<User, Void, User>
    {
        protected User doInBackground(User... user)
        {
            User u = user[0];
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://ganter.azurewebsites.net/Summoner/RegisterUser");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("Username", u.getUsername()));
                params.add(new BasicNameValuePair("Password", Base64.encodeToString(u.getEncryptedPassword(), Base64.DEFAULT)));
                params.add(new BasicNameValuePair("Summonername",u.getSummonername()));
                post.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse response = client.execute(post);

                InputStream content = response.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String data = "";
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    data += s;
                }
                JSONObject userObject = new JSONObject(data);
                int userId = userObject.getInt("UserID");
                if(userId > 0)
                {
                    u.setUserID(userId);
                    return u;
                }
                //Toast.makeText(getApplicationContext(), "Error occurred logging in.", Toast.LENGTH_LONG).show();
                return null;
            }
            catch(Exception e)
            {
                //Toast.makeText(getApplicationContext(), "Error occurred logging in.", Toast.LENGTH_LONG).show();
            }

            return u;
        }

        protected void onPostExecute(User result)
        {
            Intent champList = new Intent(getApplication(), ChampionList.class);
            champList.putExtra("User", result);
            startActivity(champList);
        }
    }

    public void RegisterUser(View v)
    {
        String password = ((EditText)findViewById(R.id.registerPassword)).getText().toString();
        String username = ((EditText)findViewById(R.id.registerUsername)).getText().toString();
        String summonername = ((EditText)findViewById(R.id.registerSummonername)).getText().toString();
        User user = new User(summonername, username, password);
        new RegisterUser().execute(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
