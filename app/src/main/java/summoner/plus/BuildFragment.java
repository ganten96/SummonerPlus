package summoner.plus;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import summoner.plus.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class BuildFragment extends Fragment implements AbsListView.OnItemClickListener
{

    private OnFragmentInteractionListener mListener;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView buildList;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BuildArrayAdapter buildAdapter;

    // TODO: Rename and change types of parameters
    public static BuildFragment newInstance(String param1, String param2)
    {
        BuildFragment fragment = new BuildFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BuildFragment()
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
        View view = inflater.inflate(R.layout.fragment_build, container, false);
        buildList = (AbsListView) view.findViewById(R.id.build_list_view);
        new DownloadItemBuilds().execute(ChampionList.currentUserId);

                // Set OnItemClickListener so we can be notified on item clicks
        //buildList.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            //mListener = (OnFragmentInteractionListener) activity;
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
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    private class DownloadItemBuilds extends AsyncTask<Integer, Void, ArrayList<Build>>
    {
        @Override
        protected void onPostExecute(ArrayList<Build> result)
        {
            buildAdapter = new BuildArrayAdapter(getActivity(), result);
            buildList.setAdapter(buildAdapter);
        }
        @Override
        protected ArrayList<Build> doInBackground(Integer... userID)
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://ganter.azurewebsites.net/Item/GetItemBuildsByUserID");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("userID", ChampionList.currentUserId + ""));
            ArrayList<Build> builds = new ArrayList<>();
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
                JSONArray buildData = new JSONArray(data);

                for(int i = 0; i < buildData.length(); i++)
                {
                    JSONObject currBuild = buildData.getJSONObject(i);

                    String buildName = currBuild.getString("BuildName");
                    String itemString = currBuild.getString("ItemString");
                    int buildId = currBuild.getInt("BuildID");

                    Build b =  new Build();
                    b.BuildID = buildId;
                    b.BuildName = buildName;
                    b.ItemString = itemString;

                    builds.add(b);
                }
            }
            catch(Exception ex)
            {
                Log.v("Err downloading builds", "Error");
                //Toast.makeText(getActivity().getApplicationContext(), "Unable to download Builds.", Toast.LENGTH_LONG);
            }
            return builds;
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
