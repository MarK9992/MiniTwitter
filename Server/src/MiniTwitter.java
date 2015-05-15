import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * @author Marc Karassev
 *
 * MiniTwitter's remote interface. Defines remote methods available to system's users.
 */
public interface MiniTwitter extends Remote {

    /**
     * Lists all hash tags.
     *
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    public Set<String> listTopics() throws RemoteException;
}
