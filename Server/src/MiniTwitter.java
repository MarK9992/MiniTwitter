import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/**
 * @author Marc Karassev
 *
 * MiniTwitter's remote interface. Defines methods available to system's users.
 */
public interface MiniTwitter extends Remote {

    /**
     * Posts a new tweet.
     *
     * @param topic the tweet hash tag
     * @param message the tweet message
     * @param author the tweet author
     * @throws RemoteException
     */
    public void post(String topic, String message, String author) throws RemoteException;

    /**
     * Lists all hash tags.
     *
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    public Set<String> listTopics() throws RemoteException;

    /**
     * Lists all tweets related to a hash tag.
     *
     * @param topic the hash tag
     * @return an ordered list of Tweet instances matching the tag, null if none
     * @throws RemoteException
     */
    public List<Tweet> listTopicsTweets(String topic) throws RemoteException;
}
