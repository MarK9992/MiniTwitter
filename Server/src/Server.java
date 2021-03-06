import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Marc Karassev
 *
 * The server is deployed at port 10 000 of a rmi registry port 2001.
 */
public class Server {

    public static final int REGISTRY_PORT = 2001, MINI_TWITTER_PORT = 10000;

    /**
     * Creates and launches a new MiniTwitterServer instance.
     *
     * @param args no arguments
     */
    public static void main(String[] args) {
        try {
            String name = "MiniTwitter";
            MiniTwitter miniTwitterImpl = new MiniTwitterImpl();
            MiniTwitter stub = (MiniTwitter) UnicastRemoteObject.exportObject(miniTwitterImpl, MINI_TWITTER_PORT);
            Registry registry = LocateRegistry.getRegistry(REGISTRY_PORT);

            registry.rebind(name, stub);
            System.out.println("MiniTwitterServer bound");
        } catch (Exception e) {
            System.err.println("MiniTwitterServer exception:");
            e.printStackTrace();
        }
    }
}
