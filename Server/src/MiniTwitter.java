import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * @author Marc Karassev
 *
 * MiniTwitter's remote interface. Defines remote methods available to system's users.
 */
public interface MiniTwitter extends Remote {

    // TODO sécuriser listTopics() et addsubscription + ajouter getUserTopics dans une autre interface Remote renvoyée par connect

    /**
     * Lists all hash tags.
     *
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    public Set<String> listTopics() throws RemoteException;

    /**
     * Connects a user given his login and password. If the user does not exists, created an account. Returns the set
     * of hash tags the matching user follows.
     *
     * @param login the user's login
     *              TODO add password
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    public Set<String> connect(String login) throws RemoteException;

    /**
     * Adds the given topic to the topics followed by the given user.
     *
     * @param user the user adding subscription
     * @param topic the topic the user subscribes to
     * @throws RemoteException
     */
    public void addSubscription(String user, String topic) throws RemoteException;
}
