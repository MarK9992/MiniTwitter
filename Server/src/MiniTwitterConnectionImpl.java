import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Marc Karassev
 *
 * Implements MiniTwitterConnection's remote interface. Keeps track of system's registered users and has a MiniTwitter
 * implementator.
 */
public class MiniTwitterConnectionImpl implements MiniTwitterConnection {

    private Map<String, String> userMap;
    private MiniTwitter miniTwitterImpl;

    /**
     * Default constructor, constructs a new MiniTwitterConnection with default users.
     */
    public MiniTwitterConnectionImpl() {
        userMap = new HashMap<String, String>();
        userMap.put("Marc", "marc");
        userMap.put("Quentin", "quentin");
        miniTwitterImpl = new MiniTwitterImpl();
    }

    /**
     * Default constructor, connects a user given his login and password. If the user does not exists, created an
     * account. Returns a remote interface available to connected users.
     *
     * @param login the user's login
     * @param password his password
     * @return a MiniTwitter remote interface implementation, null if wrong password
     */
    @Override
    public MiniTwitter connect(String login, String password)  throws RemoteException {
        if (userMap.containsKey(login)) {
            if (userMap.get(login).equals(password)) {
                return miniTwitterImpl;
            }
            else {
                return null;
            }
        }
        else {
            userMap.put(login, password);
            return miniTwitterImpl;
        }
    }
}
