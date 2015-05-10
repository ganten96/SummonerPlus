package summoner.plus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Nick on 5/10/2015.
 */
public class GameArrayAdapter extends ArrayAdapter<Game>
{
    private Context context;
    private ArrayList<Game> games;

    public GameArrayAdapter(Context context, ArrayList<Game> summonerGames)
    {
        super(context, R.layout.game_row, summonerGames);
        games = summonerGames;
        this.context = context;
    }

    public View getView(int key, View convertView, ViewGroup parent)
    {
        return convertView;
    }
}
