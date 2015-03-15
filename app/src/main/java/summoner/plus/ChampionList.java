package summoner.plus;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


public class ChampionList extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_list);
        new DownloadAllChampData().execute();
    }

    private class DownloadAllChampData extends AsyncTask<String, Void, ArrayList<Champion>>
    {
        @Override
        protected ArrayList<Champion> doInBackground(String... urls)
        {
            String data =  getAllChampions();
            ArrayList<Champion> champions = processChampionData(data);
            return champions;
        }

        private ArrayList<Champion> processChampionData(String obj)
        {
            ArrayList<Champion> champs = new ArrayList<Champion>();
            if(obj != null)
            {
                try
                {
                    JSONObject champData = new JSONObject(obj);
                    JSONObject dataObj = champData.getJSONObject("data");
                    Iterator<String> keys = dataObj.keys();
                    while(keys.hasNext())
                    {
                        String item = keys.next();
                        JSONObject currChampJson = (JSONObject)dataObj.get(item);
                        String name = currChampJson.getString("name");
                        String title = currChampJson.getString("title");
                        Integer id = currChampJson.getInt("id");
                        String key = currChampJson.getString("key");
                        Champion currentChamp = new Champion(name,id,title,key);
                        champs.add(currentChamp);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Unable to map champion data.");
                }
            }
            return champs;
        }

        private String getAllChampions()
        {
            String data = "";
            String url = "https://global.api.pvp.net/api/lol/static-data/na/v1.2/champion?api_key=<KEY>";
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response;
            try
            {
                response = client.execute(get);
                InputStream content = response.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    data += s;
                }
                return data;
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Failed";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_champion_list, menu);
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
