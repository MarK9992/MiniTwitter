/**
 * @author Marc Karassev
 *
 * Demonstration of client application to MiniTwitter.
 */
public class Client {

    private MiniTwitterClient miniTwitterClient;

    public Client() {
        miniTwitterClient = new MiniTwitterClient();
    }

    /**
     * Runs a demonstration scenario.
     */
    public void runDemonstration() {
        miniTwitterClient.sendMessage("Hello!");
    }

    /**
     * Launches the client.
     *
     * @param args none
     */
    public static void main(String[] args) {
        Client client = new Client();

        client.runDemonstration();
    }
}
