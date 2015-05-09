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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private ArrayList<Integer> itemIds;
    public ItemArrayAdapter(Context context, ArrayList<Item> items)
    {
        super(context, R.layout.item_list, items);
        buildItems = items;
        itemIds = new ArrayList<>();
        this.context = context;
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
        itemImage.setImageBitmap(buildItems.get(key).Picture);
        TextView itemNameView = (TextView) rowView.findViewById(R.id.itemName);
        TextView itemDescriptionView = (TextView) rowView.findViewById(R.id.itemDescription);
        rowView.setTag(id);
        itemNameView.setText(itemName);
        itemDescriptionView.setText(Html.fromHtml(description));

        CheckBox checkBox = (CheckBox)rowView.findViewById(R.id.isItemChecked);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    itemIds.add(currentItemId);
                }
                else
                {
                    itemIds.remove(currentItemId);
                }
            }
        });
        return rowView;
    }


    public ArrayList<Integer> getSelectedItems()
    {
        return itemIds;
    }

}
