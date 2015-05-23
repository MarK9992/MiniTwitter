import javax.jms.JMSException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Marc Karassev
 *
 * Demonstration of client application to MiniTwitter.
 */
public class Client {

    private MiniTwitterClient miniTwitterClient;
    private Scanner scanner;

    /**
     * Connects to the MiniTwitterServer and initializes a client subscribing to the default topic.
     *
     * @throws JMSException
     */
    public Client() throws JMSException {
        try {
            // TODO ask registry host
            Registry registry = LocateRegistry.getRegistry("localhost", Server.REGISTRY_PORT);
            MiniTwitter miniTwitter = (MiniTwitter) registry.lookup(Server.STUB_NAME);
            Set<String> topics;
            String login;

            scanner = new Scanner(System.in);
            System.out.println("What's your username?");
            login = scanner.nextLine();
            miniTwitterClient = new MiniTwitterClient(miniTwitter, miniTwitter.connect(login), login);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs a demonstration scenario.
     *
     * @throws JMSException
     */
    public void runDemonstration() throws JMSException {
        boolean run = true;
        System.out.println("Welcome to MiniTwitter!");
        while (run) {
            System.out.println("What do you want to do?\n1 - Post a tweet\n2 - Follow a new hash tag\n3 - Quit");
            switch (scanner.nextInt()) {
                case 1:
                    scanner.nextLine();
                    tweet();
                    break;
                case 2:
                    scanner.nextLine();
                    follow();
                    break;
                case 3:
                    run = false;
                    break;
                default:
            }
        }
    }

    // allows the user to tweet
    private void tweet() throws JMSException {
        String topic, message;

        System.out.println("Here is the hash tag list you subscribed:");
        for (String listedTopic: miniTwitterClient.getTopics()) {
            System.out.println(listedTopic);
        }
        System.out.println("Please type the hash tag you want to tweet to:");
        topic = scanner.nextLine();
        System.out.println("Please type your message:");
        message = scanner.nextLine();
        miniTwitterClient.sendMessage(topic, message);
    }

    // allows the user to follow a new hash tag
    private void follow() throws JMSException {
        String topic;

        System.out.println("Please enter the hash tag you want to follow:");
        topic = scanner.nextLine();
        if (miniTwitterClient.subscribeToTopic(topic)) {
            System.out.println("You successfully subscribed to " + topic + ".");
        }
        else {
            System.out.println("Failed to subscribe to " + topic + ", the hash tag probably does not exist.");
        }
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
