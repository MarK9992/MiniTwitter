import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marc Karassev, Quentin Cornevin
 *
 * Implements MiniTwitterConnection's remote interface. Keeps track of system's registered users and has a MiniTwitter
 * implementator.
 */
public class MiniTwitterConnectionImpl implements MiniTwitterConnection {

    private Map<String, String> userMap;
    private MiniTwitter miniTwitterStub;

    /**
     * Constructs a new MiniTwitterConnection with default users and the given MiniTwitter stub.
     *
     * @param miniTwitterStub the MiniTwitter stub
     */
    public MiniTwitterConnectionImpl(MiniTwitter miniTwitterStub) {
        userMap = new HashMap<String, String>();
        userMap.put("Marc", "marc");
        userMap.put("Quentin", "quentin");
        this.miniTwitterStub = miniTwitterStub;
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
                return miniTwitterStub;
            }
            else {
                return null;
            }
        }
        else {
            userMap.put(login, password);
            return miniTwitterStub;
        }
    }
}
