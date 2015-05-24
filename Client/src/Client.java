import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/**
 * @author Marc Karassev, Quentin Cornevin
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
            scanner = new Scanner(System.in);
            System.out.println("Please enter the server's host:");
            Registry registry = LocateRegistry.getRegistry(scanner.nextLine(), Server.REGISTRY_PORT);
            MiniTwitterConnection miniTwitterConnection = (MiniTwitterConnection) registry.lookup(Server.STUB_NAME);
            MiniTwitter miniTwitter;
            String login, password;

            do {
                System.out.println("What's your username? (if you don't have an account, it will be created)");
                login = scanner.nextLine();
                System.out.println("What's your password?");
                password = scanner.nextLine();
                miniTwitter = miniTwitterConnection.connect(login, password);
                if (miniTwitter == null) {
                    System.out.println("Wrong password.");
                }
                else {
                    System.out.println("Connection success!");
                }
            } while (miniTwitter == null);
            miniTwitterClient = new MiniTwitterClient(miniTwitter, miniTwitter.getUserTopics(login), login);
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
            System.out.println("What do you want to do?\n1 - Post a tweet\n2 - Follow a new hash tag\n3 - See tweets" +
                    "\n4 - Retweet \n5 - Quit");
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
                    read();
                    break;
                case 4 :
                    retweet(scanner);
                    break;
                case 5:
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

    //calls the MiniTwitterClient method to print all the timeline
    private void read() throws JMSException {
        miniTwitterClient.readTimeLine();
    }

    // allows the user to retweet
    private void retweet(Scanner scanner) throws JMSException {
        List<MapMessage> timeLine = miniTwitterClient.getTimeLine();
        System.out.println("Enter the number of the tweet you want to retweet:");
        int retweet = scanner.nextInt();
        scanner.nextLine();
        if(retweet > timeLine.size()) {
            System.out.println("Sorry, put a valid number please.");
        } else {
            retweet--;
            MapMessage tweet = timeLine.get(retweet);
            String topicName = tweet.getString(MiniTwitterClient.TOPIC_KEY);
            String contents = "retweeted from " + tweet.getString(MiniTwitterClient.AUTHOR_KEY) + " at "
                    + tweet.getString(MiniTwitterClient.DATE_KEY) + ": "
                    + tweet.getString(MiniTwitterClient.CONTENTS_KEY);
            miniTwitterClient.sendMessage(topicName, contents);
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
