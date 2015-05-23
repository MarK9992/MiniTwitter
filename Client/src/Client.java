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
