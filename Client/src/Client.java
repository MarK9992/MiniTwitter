import javax.jms.JMSException;

/**
 * @author Marc Karassev
 *
 * Demonstration of client application to MiniTwitter.
 */
public class Client {

    private MiniTwitterClient miniTwitterClient;

    public Client() throws JMSException {
        miniTwitterClient = new MiniTwitterClient();
    }

    /**
     * Runs a demonstration scenario.
     */
    public void runDemonstration() throws JMSException {
        miniTwitterClient.sendMessage("Hello!");
    }

    /**
     * Launches the client.
     *
     * @param args none
     */
    public static void main(String[] args) {
        try {
            Client client = new Client();

            client.runDemonstration();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
