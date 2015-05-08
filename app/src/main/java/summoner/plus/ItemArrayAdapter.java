package summoner.plus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nick on 5/7/2015.
 */
public class ItemArrayAdapter extends ArrayAdapter<Item>
{
    private Context context;
    private ArrayList<Item> buildItems;
    private ImageView currentItemImage;
    private int currentItemId;
    private HashMap<Integer, Bitmap> pictureMap;
    public ItemArrayAdapter(Context context, ArrayList<Item> items)
    {
        super(context, R.layout.item_list, items);
        buildItems = items;
        this.context = context;
        pictureMap = new HashMap<>();
        for(Item item : buildItems)
        {
            new GetPicture().execute(item.Id);
        }
    }

    private class GetPicture extends AsyncTask<Integer, Void, Bitmap>
    {
        int currId;
        @Override
        protected void onPostExecute(Bitmap picture)
        {
            pictureMap.put(currId, picture);
        }

        @Override
        protected Bitmap doInBackground(Integer... id)
        {
            Bitmap picture = null;

            try
            {
                URL imageUrl = new URL("http://ddragon.leagueoflegends.com/cdn/5.2.1/img/item/"+ id[0] + ".png");
                picture = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                currId = id[0];
                return picture;
            }
            catch(Exception e)
            {
                Log.v("Picture error", "Unable to load picture");
            }
            return picture;
        }
    }

    public View getView(int key, View convertView, ViewGroup parent)
    {
        String itemName = buildItems.get(key).Name;
        String description = buildItems.get(key).Description;
        int id = buildItems.get(key).Id;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list, parent, false);
        ImageView itemImage = (ImageView) rowView.findViewById(R.id.itemPicture);
        currentItemImage = itemImage;
        currentItemId = id;
        if(id == 3751 || itemName.contains("Luden"))
        {
            System.out.println("here");
        }
        itemImage.setImageBitmap(pictureMap.get(id));
        /*try
        {

        }
        catch(Exception e)
        {
            Log.v("Picture error", "Unable to load picture");
        }*/

        TextView itemNameView = (TextView) rowView.findViewById(R.id.itemName);
        TextView itemDescriptionView = (TextView) rowView.findViewById(R.id.itemDescription);
        rowView.setTag(id);
        itemNameView.setText(itemName);
        itemDescriptionView.setText(Html.fromHtml(description));
        return rowView;
    }
}
