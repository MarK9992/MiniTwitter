import javax.jms.JMSException;
import java.util.Scanner;

/**
 * @author Marc Karassev
 *
 * Demonstration of client application to MiniTwitter.
 */
public class Client {

    private MiniTwitterClient miniTwitterClient;
    private Scanner scanner;

    public Client() throws JMSException {
        miniTwitterClient = new MiniTwitterClient();
        scanner = new Scanner(System.in);
    }

    /**
     * Runs a demonstration scenario.
     */
    public void runDemonstration() throws JMSException {
        boolean run = true;
        System.out.println("Welcome to MiniTwitter!");
        while (run) {
            System.out.println("What do you want to do?\n1 - Post a tweet\n2 - Quit");
            switch (scanner.nextInt()) {
                case 1:
                    scanner.nextLine();
                    tweet();
                    break;
                case 2:
                    scanner.nextLine();
                    run = false;
                    break;
                default:
            }
        }
    }

    // allows the user to tweet
    private void tweet() throws JMSException {
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
