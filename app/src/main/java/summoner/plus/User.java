package summoner.plus;

import android.os.Message;
import android.os.Parcelable;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nick on 4/25/2015.
 */
public class User implements Serializable
{
    private int UserID;
    private String Summonername;
    private String Username;
    private byte[] Password;

    public User(int Id, String summonerName, String userName, String pass)
    {
        UserID = Id;
        Summonername = summonerName;
        Username = userName;
        Password = encryptPassword(pass);
    }

    public User(String summonerName, String userName, String pass)
    {
        Summonername = summonerName;
        Username = userName;
        Password = encryptPassword(pass);
    }

    public User(String username, String password)
    {
        Password = encryptPassword(password);
        Username = username;
    }

    public void setUserID(int id)
    {
        UserID = id;
    }

    public void setSummonername(String summonerName)
    {
        Summonername = summonerName;
    }

    public void setUsername(String userName)
    {
        Username = userName;
    }

    public byte[] getEncryptedPassword()
    {
        return Password;
    }

    public String getUsername()
    {
        return Username;
    }

    public String getSummonername()
    {
        return Summonername;
    }

    public int getUserID()
    {
        return UserID;
    }

    public byte[] encryptPassword(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            byte[] encrypted = md.digest();

            return encrypted;
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
