import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author Marc Karassev
 *
 * Implements MiniTwitter's remote interface.
 * Keeps track of hashtags list.
 * Creates the default hashtag.
 */
public class MiniTwitterImpl implements MiniTwitter {

    public static final String DEFAULT_TOPIC = "#HelloWorld";

    private Set<String> topics;

    /**
     * Default constructor, constructs a new MiniTwitterServer with a default hash tag.
     */
    public MiniTwitterImpl() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("user", "password", "tcp://localhost:61616");
            Connection connect = factory.createConnection("user", "password");
            Session session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic defaultTopic = session.createTopic(DEFAULT_TOPIC);

            topics = new HashSet<String>();
            topics.add(defaultTopic.getTopicName());
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
}
