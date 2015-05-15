import javax.jms.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author Marc Karassev
 *
 * Client application to MiniTwitter.
 */
public class Client implements MessageListener {

    private MiniTwitter miniTwitter;
    private Session session;
    private MessageProducer topicPublisher;
    //private Map<String, Topic> topicMap;

    public Client() {
        //topicMap = new HashMap<String, Topic>();
        connect();
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("user", "password", "tcp://localhost:61616");
            Connection connect = factory.createConnection ("user", "password");
            session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(miniTwitter.listTopics().iterator().next());
            MessageConsumer topicSubscriber = session.createConsumer(topic);

            topicPublisher = session.createProducer(topic);
            //topicMap.put("#HelloWorld", topic);
            topicSubscriber.setMessageListener(this);
            connect.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            String name = "MiniTwitter";
            Registry registry = LocateRegistry.getRegistry("localhost", Server.REGISTRY_PORT);

            miniTwitter = (MiniTwitter) registry.lookup(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void runDemonstration() {
        try {
            MapMessage message = session.createMapMessage();

            message.setString("author", "me");
            message.setString("contents", "Hello!");
            topicPublisher.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

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
