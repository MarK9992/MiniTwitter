import java.rmi.RemoteException;
import java.util.*;

/**
 * @author Marc Karassev
 *
 * Implements MiniTwitter's remote interface.
 * Has a map where keys are hashtags and values tweet lists.
 */
public class MiniTwitterImpl implements MiniTwitter {

    private HashMap<String, List<Tweet>> topics;

    /**
     * Default constructor, constructs a new MiniTwitterServer with an empty map of topics and tweets.
     */
    public MiniTwitterImpl() {
        topics = new HashMap<String, List<Tweet>>();
    }

    /**
     * Posts a new tweet.
     *
     * @param tag the tweet hash tag
     * @param message the tweet
     * @param author the tweet author
     * @throws RemoteException
     */
    @Override
    public void post(String tag, String message, String author) throws RemoteException {
        tag = tag.trim();
        if (topics.keySet().contains(tag)) {
            topics.get(tag).add(new Tweet(author, message));
        }
        else {
            ArrayList<Tweet> tweetList = new ArrayList<Tweet>();

            tweetList.add(new Tweet(author, message));
            topics.put(tag, tweetList);
        }
    }

    /**
     * Lists all hash tags.
     *
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    @Override
    public Set<String> listTopics() throws RemoteException {
        return new HashSet<String>(topics.keySet());
    }

    /**
     * Lists all tweets related to a hash tag.
     *
     * @param tag the hash tag
     * @return a set of Tweet instances matching the tag, null if none
     * @throws RemoteException
     */
    @Override
    public List<Tweet> listTopicsTweets(String tag) throws RemoteException {
        return topics.get(tag);
    }
}
