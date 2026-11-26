
import java.util.UUID;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * incident
 * -------
 * this class represents 1 emergency incident
 *
 * it stores:
 * - type
 * - district
 * - priority
 * - a unique id
 * - the date and time it was recorded
 */
public class Incident
{
    private final UUID id;
    private final String type;
    private final String district;
    private final int priority;
    private final Instant timeRecorded;

    public Incident(String type, String district, int priority)
    {
        this.id = UUID.randomUUID();
        this.type = type;
        this.district = district;
        this.priority = priority;
        this.timeRecorded = Instant.now();
    }

    public UUID getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getDistrict()
    {
        return district;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getFormattedTime()
    {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                             .withZone(ZoneId.systemDefault());

        return formatter.format(timeRecorded);
    }

    public String toString()
    {
        String p = (priority == 1) ? "high" : "normal";
        return "id: " + id +
               " | type: " + type +
               " | district: " + district +
               " | priority: " + p +
               " | recorded: " + getFormattedTime();
    }
}
