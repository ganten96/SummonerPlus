package summoner.plus;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ChampionList extends ActionBarActivity
{
    private boolean isLoggedIn;
    private ArrayList<Champion> champions;
    private User currentUser;
    private String[] navTitles;
    private DrawerLayout navDrawer;
    private ListView navDrawerList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_list);
        navTitles = new String[]{"Champions", "Item Builds", "My Games", "Settings"};
        navDrawer = (DrawerLayout) findViewById(R.id.appNav);
        navDrawerList = (ListView) findViewById(R.id.list_drawer);

        navDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.nav_item_list, navTitles));
        Intent intent = getIntent();
        currentUser = (User) intent.getExtras().getSerializable("User");
        isLoggedIn = intent.getBooleanExtra("isLoggedIn", isLoggedIn);
        champions = new ArrayList<>();
        new DownloadAllChampData().execute();
            Log.v("Champions Filled", champions.size() + "");
    }

    private class DownloadAllChampData extends AsyncTask<String, Void, ArrayList<Champion>>
    {
        @Override
        protected ArrayList<Champion> doInBackground(String... urls)
        {
            String data =  getAllChampions();

            return processChampionData(data);
        }

        @Override
        protected void onPostExecute(ArrayList<Champion> result)
        {

            champions = result;
            ChampionGridFragment frag = populateChampionList(champions);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.championListParent, frag);
            ft.commit();
        }

        private ArrayList<Champion> processChampionData(String obj)
        {
            ArrayList<Champion> champs = new ArrayList<>();
            if(obj != null)
            {
                try
                {
                    JSONArray champData = new JSONArray(obj);
                    for(int i = 0; i < champData.length(); i++)
                    {
                        JSONObject currChampJson = champData.getJSONObject(i);
                        String name = currChampJson.getString("Name");
                        String title = currChampJson.getString("Title");
                        Integer id = currChampJson.getInt("Id");
                        String key = currChampJson.getString("Key");
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
            String url = "http://ganter.azurewebsites.net/api/RiotApi";
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

    private ChampionGridFragment populateChampionList(ArrayList<Champion> champs)
    {
        Bundle bundle = new Bundle();
        ChampionGridFragment fragment = new ChampionGridFragment();
        bundle.putSerializable("Champions", champs);
        fragment.setArguments(bundle);

        return fragment;
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
