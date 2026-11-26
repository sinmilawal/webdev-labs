
import java.util.ArrayList;
import java.util.List;

/**
 * systemlog<T>
 * ------------
 * this is a simple log class
 * it is generic so it can store different types of things
 *
 * example:
 * systemlog<string> for text
 * systemlog<integer> for numbers
 * systemlog<incident> for incidents
 */
public class SystemLog<T>
{
    private final List<T> items;

    public SystemLog()
    {
        items = new ArrayList<>();
    }

    // adds something to the log
    public void add(T item)
    {
        items.add(item);
    }

    // prints the log to the console
    public void display()
    {
        if (items.isEmpty())
        {
            System.out.println("system log is empty");
            return;
        }

        System.out.println("=== system log ===");
        for (T item : items)
        {
            System.out.println(item);
        }
    }
}
