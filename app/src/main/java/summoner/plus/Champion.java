package summoner.plus;

import org.json.JSONObject;

/**
 * Created by Nick on 3/15/2015.
 */
public class Champion
{
    public String champtionName;
    public int id;
    public String title;
    public String key;

    public Champion(String name, int id, String title,
                    String key)
    {
        champtionName = name;
        this.id = id;
        this.title = title;
        this.key = key;
    }

    public Champion()
    {

    }
}
