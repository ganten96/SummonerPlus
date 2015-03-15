package summoner.plus;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Nick on 3/15/2015.
 */
public class Champion implements Serializable
{
    public String championName;
    public int id;
    public String title;
    public String key;

    public Champion(String name, int id, String title,
                    String key)
    {
        this.championName = name;
        this.id = id;
        this.title = title;
        this.key = key;
    }

    public Champion()
    {

    }
}
