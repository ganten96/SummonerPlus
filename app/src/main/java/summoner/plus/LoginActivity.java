package summoner.plus;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;


public class LoginActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onRegisterClick(View v)
    {
        Intent continueIntent = new Intent(this, Register.class);
        startActivity(continueIntent);
    }

    public void LogIn(View v)
    {
        String username = ((EditText) findViewById(R.id.summonerNameInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInput)).getText().toString();

        User user = new User(username, password);
        new LogInUser().execute(user);
    }

    private class LogInUser extends AsyncTask<User, Void, User>
    {

        @Override
        protected User doInBackground(User... users)
        {
            User u = users[0];
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://ganter.azurewebsites.net/Summoner/LogIn");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("Username", u.getUsername()));
                params.add(new BasicNameValuePair("Password", Base64.encodeToString(u.getEncryptedPassword(), Base64.DEFAULT)));
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
                long SummonerID = userObject.getLong("SummonerID");
                String summonerName = userObject.getString("Summonername");
                if(userId > 0)
                {
                    u.setUserID(userId);
                    u.setSummonername(summonerName);
                    u.SummonerID = SummonerID;
                    return u;
                }
                Toast.makeText(getApplicationContext(), "Error occurred logging in.", Toast.LENGTH_LONG).show();
                return null;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return u;
        }
        protected void onPostExecute(User result)
        {
            if(result == null || result.getUserID() == 0)
            {
                Toast.makeText(getApplicationContext(), "Invalid user. Error occurred logging in.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent champList = new Intent(getApplication(), ChampionList.class);
                champList.putExtra("User", result);
                startActivity(champList);
            }
        }
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
