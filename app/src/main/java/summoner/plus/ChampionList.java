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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
        navTitles = new String[]{"Champions", "Items", "Item Builds", "My Games", "Settings"};
        navDrawer = (DrawerLayout) findViewById(R.id.appNav);
        navDrawerList = (ListView) findViewById(R.id.list_drawer);

        navDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.nav_item_list, navTitles));
        navDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        Intent intent = getIntent();
        currentUser = (User) intent.getExtras().getSerializable("User");
        isLoggedIn = intent.getBooleanExtra("isLoggedIn", isLoggedIn);
        champions = new ArrayList<>();
        new DownloadAllChampData().execute();
            Log.v("Champions Filled", champions.size() + "");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectNavItem(position);
        }
        private void selectNavItem(int position)
        {
            switch (position)
            {
                case 0:
                    //champs
                    break;
                case 1:
                    //items
                    new DownloadItemData().execute();
                    break;
                case 2:
                    break;
            }
        }
    }

    private class DownloadItemData extends AsyncTask<String, Void, ArrayList<Item>>
    {
        @Override
        protected ArrayList<Item> doInBackground(String... urls)
        {
            String data = getAllItems();
            return processItemData(data);
        }

        @Override
        protected void onPostExecute(ArrayList<Item> result)
        {
            ArrayList<Item> items = result;
            ItemListFragment frag = populateItemList(items);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.championListParent, frag);
            ft.commit();
        }

        private ArrayList<Item> processItemData(String data)
        {
            ArrayList<Item> items = new ArrayList<Item>();
            if(data != null)
            {
                try
                {
                    JSONArray itemData = new JSONArray(data);
                    for(int i = 0; i < itemData.length(); i++)
                    {
                        JSONObject currItemJson = itemData.getJSONObject(i);
                        String name = currItemJson.getString("Name");
                        String description = currItemJson.getString("Description");
                        Integer id = currItemJson.getInt("Id");
                        Item currentItem = new Item();
                        currentItem.Id = id;
                        currentItem.Description = description;
                        currentItem.Name = name;
                        items.add(currentItem);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Unable to map item data.");
                }
            }
            return items;
        }

        private String getAllItems()
        {
            String data = "";
            String url = "http://ganter.azurewebsites.net/Item/GetAllItems";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            HttpResponse response;
            try
            {
                response = client.execute(post);
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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Failed";
        }
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

    private ItemListFragment populateItemList(ArrayList<Item> items)
    {
        Bundle bundle = new Bundle();
        ItemListFragment fragment = new ItemListFragment();
        bundle.putSerializable("Items", items);
        fragment.setArguments(bundle);

        return fragment;
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
