import javax.jms.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author Marc Karassev
 *
 * Client application to MiniTwitter.
 */
public class MiniTwitterClient implements MessageListener {

    private MiniTwitter miniTwitter;
    private Session session;
    private MessageProducer defaultTopicPublisher;
    private String userName;
    private Set<String> topics;

    /**
     * Default constructor, creates a new client which only subscribes to the default hash tag.
     */
    public MiniTwitterClient() {
        connect();
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("user", "password", "tcp://localhost:61616");
            Connection connect = factory.createConnection("user", "password");
            session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic defaultTopic = session.createTopic(MiniTwitterImpl.DEFAULT_TOPIC);
            MessageConsumer defaultTopicSubscriber = session.createConsumer(defaultTopic);

            defaultTopicPublisher = session.createProducer(defaultTopic);
            defaultTopicSubscriber.setMessageListener(this);
            connect.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // connects to the MiniTwitterServer
    private void connect() {
        try {
            String name = "MiniTwitter";
            Registry registry = LocateRegistry.getRegistry("localhost", Server.REGISTRY_PORT);

            miniTwitter = (MiniTwitter) registry.lookup(name);
            userName = "me";
            topics = miniTwitter.listTopics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to default topic.
     *
     * @param contents the contents of the message to send
     */
    public void sendMessage(String contents) {
        try {
            MapMessage message = session.createMapMessage();

            message.setString("author", userName);
            message.setString("contents", contents);
            defaultTopicPublisher.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passes a message to the listener and prints it.
     *
     * @param message the message passed to the listener
     */
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;

        try {
            System.out.println("message received, author: " + mapMessage.getString("author") + ", contents: "
                    + mapMessage.getString("contents"));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
