package summoner.plus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nick on 3/15/2015.
 */
public class ChampionArrayAdapter extends ArrayAdapter<Champion>
{
    private Context context;
    private ArrayList<Champion> champions;

    public ChampionArrayAdapter(Context context, ArrayList<Champion> champs)
    {
        super(context, R.layout.fragment_champion_grid, champs);
        this.context = context;
        this.champions = champs;
    }

    public View getView(int key, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_champion_grid, parent, false);
        TextView champName = (TextView) rowView.findViewById(R.id.championListName);
        champName.setText(champions.get(key).championName + " " + champions.get(key).title);
        return rowView;
    }
}
