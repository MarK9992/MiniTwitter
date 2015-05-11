import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Marc Karassev
 *
 * Tweet class, has an author, contents and a date.
 */
public class Tweet implements Serializable {

    private String author;
    private Calendar date;
    private String contents;

    /**
     * Default constructor, constructs a new tweet with default author and contents.
     */
    public Tweet() {
        this("nobody", "nothing");
    }

    /**
     * Creates a new tweet with the specified author and contents.
     *
     * @param author the tweet author
     * @param message the tweet contents
     */
    public Tweet(String author, String message) {
        this.author = author;
        date = Calendar.getInstance();
        contents = message;
    }

    /**
     * Returns a string representation of the current instance;
     *
     * @return the tweet string representation
     */
    @Override
    public String toString() {
        return date.getTime() + ", " + author + ": " + contents;
    }
}
