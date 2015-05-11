import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Marc Karassev
 *
 * Client application to MiniTwitter.
 */
public class Client {

    /**
     * Launches the client.
     *
     * @param args none
     */
    public static void main(String[] args) {
        try {
            String name = "MiniTwitter";
            Registry registry = LocateRegistry.getRegistry(args[0], Server.REGISTRY_PORT);
            MiniTwitter miniTwitter = (MiniTwitter) registry.lookup(name);

            miniTwitter.post("#AppServer", "Hello!", "me");
            System.out.println(miniTwitter.listTopics());
            System.out.println(miniTwitter.listTopicsTweets("#AppServer"));
        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
    }
}
