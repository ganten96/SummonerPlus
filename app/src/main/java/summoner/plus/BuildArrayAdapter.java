package summoner.plus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 5/10/2015.
 */
public class BuildArrayAdapter extends ArrayAdapter<Build>
{
    private Context context;
    private List<Build> builds;
    public BuildArrayAdapter(Context context, List<Build> objects)
    {
        super(context, R.layout.build_row, objects);
        builds = objects;
        this.context = context;
    }

    public View getView(int key, View convertView, ViewGroup parent)
    {
        final int position = key;
        String buildName = builds.get(key).BuildName;
        String itemString = builds.get(key).ItemString;
        final int buildId = builds.get(key).BuildID;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.build_row, parent, false);

        ((TextView) rowView.findViewById(R.id.build_name)).setText(buildName);
        ((TextView) rowView.findViewById(R.id.itemString)).setText(itemString);

        Button deleteButton = (Button) rowView.findViewById(R.id.delete_build);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DeleteItemBuild().execute(buildId);
                builds.remove(position);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }

    private class DeleteItemBuild extends AsyncTask<Integer, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Integer... buildId)
        {
            int bId = buildId[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://ganter.azurewebsites.net/Item/DeleteItemBuild");
            HttpResponse response;
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("BuildID", bId + ""));
            Boolean isDeleted = false;
            try
            {
                post.setEntity(new UrlEncodedFormEntity(params));
                response = client.execute(post);
                InputStream content = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                String data = "";
                String s = "";

                while((s = reader.readLine()) != null)
                {
                    data += s;
                }
                isDeleted = new Boolean(data);
            }
            catch(Exception ex)
            {
                Log.v("Unable to delete build.", "BuildID: " + buildId);
                Toast.makeText(getContext(), "Unable to delete build.", Toast.LENGTH_LONG);
            }
            return isDeleted;
        }
    }
}
