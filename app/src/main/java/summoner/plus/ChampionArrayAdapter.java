package summoner.plus;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Nick on 3/15/2015.
 */
public class ChampionArrayAdapter extends ArrayAdapter<Champion>
{
    private AssetManager assetManager = getContext().getAssets();
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
        String name = champions.get(key).championName;
        String champKey = champions.get(key).key;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_champion_grid, parent, false);
        ImageView championSquare = (ImageView) rowView.findViewById(R.id.champThumbnail);
        try
        {
            InputStream stream = assetManager.open("ChampionSquares/" + champKey + ".png");
            Bitmap b = BitmapFactory.decodeStream(stream);
            championSquare.setImageBitmap(b);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        TextView champName = (TextView) rowView.findViewById(R.id.championListName);
        champName.setText(name);
        rowView.setTag(key);
        return rowView;
    }
}
