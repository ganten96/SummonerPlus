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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
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
public class GameListFragment extends Fragment implements AbsListView.OnItemClickListener {

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView gameList;
    private ArrayList<Game> summonerGames;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private GameArrayAdapter gameAdapter;
    private long summonerId;


    // TODO: Rename and change types of parameters
    public static GameListFragment newInstance(String param1, String param2)
    {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // Set the adapter
        gameList = (AbsListView) view.findViewById(R.id.game_list);
        summonerId = this.getArguments().getLong("SummonerId");
        summonerGames = new ArrayList<>();
        new DownloadGameData().execute(summonerId);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private class DownloadGameData extends AsyncTask<Long, Void, ArrayList<Game>>
    {
        @Override
        protected void onPostExecute(ArrayList<Game> games)
        {
            summonerGames = games;
            gameAdapter = new GameArrayAdapter(getActivity(), summonerGames);
            gameList.setAdapter(gameAdapter);
        }

        @Override
        protected ArrayList<Game> doInBackground(Long... summonerName)
        {
            HttpClient client = new DefaultHttpClient();
            Long sId = summonerName[0];
            HttpPost post = new HttpPost("http://ganter.azurewebsites.net/Summoner/GetSummonerRecentGames");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("summonerId", sId + ""));
            ArrayList<Game> games = new ArrayList<>();
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
                //json mapping
                try
                {
                    JSONArray gameData = new JSONArray(data);
                    for(int i = 0; i < gameData.length(); i++)
                    {
                        JSONObject gameObj = gameData.getJSONObject(i);

                        String gameMode = gameObj.getString("GameMode");
                        long createDate = gameObj.getLong("CreateDate");
                        Date gameDate = new Date(createDate);
                        JSONObject rawStats = gameObj.getJSONObject("Stats");
                        int assists = rawStats.getInt("Assists");
                        int champsKilled = rawStats.getInt("ChampionsKilled");
                        int goldEarned = rawStats.getInt("GoldEarned");
                        int numDeaths = rawStats.getInt("NumDeaths");
                        boolean win = rawStats.getBoolean("Win");
                        RawStats stats = new RawStats();
                        stats.Win = win;
                        stats.GoldEarned = goldEarned;
                        stats.ChampionsKilled = champsKilled;
                        stats.Assists = assists;
                        stats.NumDeaths = numDeaths;

                        Game g = new Game();
                        g.Date = gameDate;
                        g.CreateDate = createDate;
                        g.GameMode = gameMode;
                        g.Stats = stats;
                        games.add(g);
                    }
                }
                catch(Exception e)
                {
                    Log.v("Game Mapping", "Failed to map games");
                }
            }
            catch(Exception ex)
            {
                Log.v("Game Data", "Error getting game data for: " + sId);
            }

            return games;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
