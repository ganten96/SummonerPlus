package summoner.plus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
        String GameMode = games.get(key).GameMode;
        Date GameDate = games.get(key).Date;
        int champId = games.get(key).ChampionId;
        int assists = games.get(key).Stats.Assists;
        int goldEarned = games.get(key).Stats.GoldEarned;
        int numDeaths = games.get(key).Stats.NumDeaths;
        int champsKilled = games.get(key).Stats.ChampionsKilled;
        boolean isWin = games.get(key).Stats.Win;

        String winString = isWin ? "Win" : "Lost";
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.game_row, parent, false);

        ((TextView) rowView.findViewById(R.id.gameMode)).setText(GameMode);
        ((TextView) rowView.findViewById(R.id.gameDate)).setText(GameDate.toString());
        ((TextView) rowView.findViewById(R.id.winLoss)).setText(winString);
        ((TextView) rowView.findViewById(R.id.deaths)).setText( "Deaths: " + numDeaths + " Assists: "
                + assists + " KDA: "  + champsKilled / numDeaths);
        ((TextView) rowView.findViewById(R.id.goldEarned)).setText(goldEarned);
        return rowView;
    }
}
