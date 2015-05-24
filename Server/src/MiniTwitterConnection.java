import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Marc Karassev, Quentin Cornevin
 *
 * MiniTwitter's remote connection interface. Define remote methods available to system's users in order to perform
 * connection.
 */
public interface MiniTwitterConnection extends Remote {

    /**
     * Default constructor, connects a user given his login and password. If the user does not exists, created an
     * account. Returns a remote interface available to connected users.
     *
     * @param login the user's login
     * @param password his password
     * @return a MiniTwitter remote interface implementation, null if wrong password
     */
    public MiniTwitter connect(String login, String password) throws RemoteException;
}
