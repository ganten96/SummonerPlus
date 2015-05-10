package summoner.plus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemListFragment extends Fragment implements AbsListView.OnItemClickListener
{
    private ArrayList<Item> items;
    private int champId;
    ArrayList<Integer> selectedItems;


    private OnFragmentInteractionListener itemClickListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView itemListView;

    /**
 * The Adapter which will be used to populate the ListView/GridView with
 * Views.
 */
    private ItemArrayAdapter itemsAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        ArrayList<Item> items = new ArrayList<>();
        new DownloadItemData().execute();
        itemListView = (AbsListView) view.findViewById(R.id.item_list);
        champId = this.getArguments().getInt("champId");
        Button saveItemBuild = (Button) view.findViewById(R.id.saveItemBuild);
        saveItemBuild.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SaveItemBuild();
            }

        });
        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            //itemClickListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        itemClickListener = null;
    }


    public void SaveItemBuild()
    {
        selectedItems = itemsAdapter.getSelectedItems();
        Build build = new Build();
        EditText buildText = ((EditText) getView().findViewById(R.id.buildName));
        build.BuildName = buildText.getText().toString();
        build.ChampionID = champId;
        build.UserID = ChampionList.currentUserId;
        build.ItemIDs = selectedItems;
        new SubmitItemBuild().execute(build);
    }

    private class SubmitItemBuild extends AsyncTask<Build, Void, Boolean>
    {
        @Override
        public void onPostExecute(Boolean isSuccessful)
        {
            if(isSuccessful)
            {
                Toast.makeText(getActivity().getApplicationContext(), "Successfully saved Item Build.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Error: Unable to save Item Build.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public Boolean doInBackground(Build... build)
        {
            HttpClient client = new DefaultHttpClient();
            Build b = build[0];
            HttpPost post = new HttpPost("http://ganter.azurewebsites.net/Item/InsertItemBuild");
            List<NameValuePair> params = new ArrayList<>();
            String idString = TextUtils.join(",", selectedItems);
            for(int i = 0; i < selectedItems.size(); i++)
            {
                params.add(new BasicNameValuePair("ItemIDs", idString));
            }
            params.add(new BasicNameValuePair("buildName", b.BuildName));
            params.add(new BasicNameValuePair("userID", b.UserID + ""));
            params.add(new BasicNameValuePair("championID", b.ChampionID + ""));

            Boolean isSuccess = new Boolean(false);

            try
            {
                post.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(post);

                InputStream content = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                String data = "";
                String s = "";
                while((s = reader.readLine()) != null)
                {
                    data += s;
                }
                isSuccess = new Boolean(data);
                return isSuccess;
            }
            catch(Exception e)
            {
                Log.v("Post Error", "Item Save error " + e.getLocalizedMessage());
            }
            return isSuccess;
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
            items = result;
            itemsAdapter = new ItemArrayAdapter(getActivity(), items);
            itemListView.setAdapter(itemsAdapter);
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
                        try
                        {
                            URL imageUrl = new URL("http://ddragon.leagueoflegends.com/cdn/5.2.1/img/item/"+ id + ".png");
                            Bitmap picture = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                            currentItem.Picture = picture;
                        }
                        catch(FileNotFoundException ex)
                        {
                            Log.v("Item image not found for " + name, "ItemAsyncTask");
                        }
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        /*if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }*/
    }
    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText)
    {
        View emptyView = itemListView.getEmptyView();

        if (emptyView instanceof TextView)
        {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
