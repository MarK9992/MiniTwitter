import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author Marc Karassev
 *
 * Implements MiniTwitter's remote interface.
 * Keeps track of hashtags list and user's subscribed hashtags.
 * Creates the default hashtag and the dedicated to new hashtags one.
 */
public class MiniTwitterImpl implements MiniTwitter, MessageListener {

    public static final String DEFAULT_TOPIC = "#HelloWorld", NEW_TOPICS_TOPIC = "#NewTopics", ACTIVE_MQ_USER = "user",
            ACTIVE_MQ_PASSWORD = "password", ACTIVE_MQ_HOST = "tcp://localhost:61616";

    private Set<String> topics;
    private Map<String, Set<String>> userTopics;

    /**
     * Default constructor, constructs a new MiniTwitterServer with a default hash tag and the one dedicated to new
     * ones. Also adds to the users map to users with default subscriptions.
     */
    public MiniTwitterImpl() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_MQ_USER, ACTIVE_MQ_PASSWORD,
                    ACTIVE_MQ_HOST);
            Connection connect = factory.createConnection(ACTIVE_MQ_USER, ACTIVE_MQ_PASSWORD);
            Session session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer newTopicsConsumer = session.createConsumer(session.createTopic(NEW_TOPICS_TOPIC));

            session.createTopic(DEFAULT_TOPIC);
            topics = new HashSet<String>();
            topics.add(DEFAULT_TOPIC);
            topics.add(NEW_TOPICS_TOPIC);
            userTopics = new HashMap<String, Set<String>>();
            userTopics.put("Marc", new HashSet<String>(topics));
            userTopics.put("Quentin", new HashSet<String>(topics));
            newTopicsConsumer.setMessageListener(this);
            connect.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lists all hash tags.
     *
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    @Override
    public Set<String> listTopics() throws RemoteException {
        return topics;
    }

    /**
     * Connects a user given his login and password. If the user does not exists, created an account. Returns the set
     * of hash tags the matching user follows.
     *
     * @param login the user's login
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    @Override
    public Set<String> connect(String login) throws RemoteException {
        if (userTopics.containsKey(login)) {
            return userTopics.get(login);
        }
        else {
            HashSet<String> defaultTopics = new HashSet<String>();

            defaultTopics.add(DEFAULT_TOPIC);
            defaultTopics.add(NEW_TOPICS_TOPIC);
            userTopics.put(login, defaultTopics);
            return defaultTopics;
        }
    }

    /**
     * Adds the given topic to the topics followed by the given user.
     *
     * @param user the user adding subscription
     * @param topic the topic the user subscribes to
     * @throws RemoteException
     */
    @Override
    public void addSubscription(String user, String topic) throws RemoteException {
        // TODO check if user exists
        Set<String> topics = userTopics.get(user);

        topics.add(topic);
        userTopics.put(user, topics);
    }

    /**
     * Passes a message to the listener and gets the new topic from it.
     *
     * @param message the message passed to the listener containing the new topic information
     */
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;

        try {
            topics.add(mapMessage.getString(MiniTwitterClient.NEW_TOPIC_KEY));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
