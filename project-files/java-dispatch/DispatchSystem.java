
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * dispatchsystem
 * --------------
 * this is the main class that runs the program and menu
 *
 * it uses:
 * - a queue to store incidents
 * - a set to store unique incident types
 *
 * file saving and loading is included
 */
public class DispatchSystem
{
    private final Deque<Incident> incidentQueue;
    private final Set<String> todayIncidentTypes;
    private final Set<String> yesterdayIncidentTypes;

    private final SystemLog<String> systemLog;

    private static final String QUEUE_FILE = "incident_queue.txt";
    private static final String TYPES_FILE = "incident_types_today.txt";

    public DispatchSystem()
    {
        incidentQueue = new ArrayDeque<>();
        todayIncidentTypes = new HashSet<>();
        systemLog = new SystemLog<>();

        yesterdayIncidentTypes = new HashSet<>();
        yesterdayIncidentTypes.add("fire");
        yesterdayIncidentTypes.add("medical");
        yesterdayIncidentTypes.add("traffic");
    }

    public void runMenu()
    {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running)
        {
            printMenu();
            int choice = readInt(sc, "choose an option: ");

            switch (choice)
            {
                case 1:
                    addNewIncident(sc);
                    break;
                case 2:
                    viewQueue();
                    break;
                case 3:
                    removeNextIncident();
                    break;
                case 4:
                    viewUniqueIncidentTypes();
                    break;
                case 5:
                    searchIncidents(sc);
                    break;
                case 6:
                    trendAnalysis();
                    break;
                case 7:
                    saveToFiles();
                    break;
                case 8:
                    loadFromFiles();
                    break;
                case 9:
                    systemLog.display();
                    break;
                case 0:
                    running = false;
                    System.out.println("goodbye");
                    break;
                default:
                    System.out.println("invalid option");
            }

            System.out.println();
        }

        sc.close();
    }

    private void printMenu()
    {
        System.out.println("=== emergency dispatch system ===");
        System.out.println("1. enter new incident");
        System.out.println("2. view all incidents in queue");
        System.out.println("3. remove next incident");
        System.out.println("4. view unique incident types today");
        System.out.println("5. search incidents");
        System.out.println("6. trend analysis");
        System.out.println("7. save data to file");
        System.out.println("8. load data from file");
        System.out.println("9. view system log");
        System.out.println("0. exit");
    }

    private void addNewIncident(Scanner sc)
    {
        System.out.print("enter incident type: ");
        String type = sc.nextLine().trim().toLowerCase();

        System.out.print("enter district: ");
        String district = sc.nextLine().trim().toLowerCase();

        int priority = readInt(sc, "enter priority (0 normal, 1 high): ");
        while (priority != 0 && priority != 1)
        {
            priority = readInt(sc, "enter priority (0 or 1): ");
        }

        Incident incident = new Incident(type, district, priority);

        if (priority == 1)
        {
            incidentQueue.addFirst(incident);
        }
        else
        {
            incidentQueue.addLast(incident);
        }

        todayIncidentTypes.add(type);

        System.out.println("incident added");
        System.out.println(incident);

        systemLog.add("added incident " + incident.getId());
    }

    private void viewQueue()
    {
        if (incidentQueue.isEmpty())
        {
            System.out.println("queue is empty");
            return;
        }

        int i = 1;
        for (Incident incident : incidentQueue)
        {
            System.out.println(i + ") " + incident);
            i++;
        }
    }

    private void removeNextIncident()
    {
        if (incidentQueue.isEmpty())
        {
            System.out.println("nothing to remove");
            return;
        }

        Incident removed = incidentQueue.removeFirst();
        System.out.println("removed incident:");
        System.out.println(removed);

        systemLog.add("removed incident " + removed.getId());
    }

    private void viewUniqueIncidentTypes()
    {
        if (todayIncidentTypes.isEmpty())
        {
            System.out.println("no incident types yet");
            return;
        }

        for (String t : todayIncidentTypes)
        {
            System.out.println("- " + t);
        }
    }

    private void searchIncidents(Scanner sc)
    {
        if (incidentQueue.isEmpty())
        {
            System.out.println("queue is empty");
            return;
        }

        System.out.println("1. search by type");
        System.out.println("2. search by district");

        int option = readInt(sc, "choose: ");
        System.out.print("enter search text: ");
        String query = sc.nextLine().trim().toLowerCase();

        boolean found = false;

        for (Incident incident : incidentQueue)
        {
            if (option == 1 && incident.getType().equalsIgnoreCase(query))
            {
                System.out.println(incident);
                found = true;
            }
            else if (option == 2 && incident.getDistrict().equalsIgnoreCase(query))
            {
                System.out.println(incident);
                found = true;
            }
        }

        if (!found)
        {
            System.out.println("no results found");
        }
    }

    private void trendAnalysis()
    {
        Set<String> union = new HashSet<>(todayIncidentTypes);
        union.addAll(yesterdayIncidentTypes);

        Set<String> intersection = new HashSet<>(todayIncidentTypes);
        intersection.retainAll(yesterdayIncidentTypes);

        Set<String> difference = new HashSet<>(todayIncidentTypes);
        difference.removeAll(yesterdayIncidentTypes);

        System.out.println("union: " + union);
        System.out.println("intersection: " + intersection);
        System.out.println("difference: " + difference);
    }

    private void saveToFiles()
    {
        try
        {
            BufferedWriter qw = new BufferedWriter(new FileWriter(QUEUE_FILE));
            for (Incident incident : incidentQueue)
            {
                qw.write(
                    incident.getType() + "," +
                    incident.getDistrict() + "," +
                    incident.getPriority()
                );
                qw.newLine();
            }
            qw.close();

            BufferedWriter tw = new BufferedWriter(new FileWriter(TYPES_FILE));
            for (String type : todayIncidentTypes)
            {
                tw.write(type);
                tw.newLine();
            }
            tw.close();

            System.out.println("data saved to file");
        }
        catch (IOException e)
        {
            System.out.println("error saving data");
        }
    }

    private void loadFromFiles()
    {
        incidentQueue.clear();
        todayIncidentTypes.clear();

        try
        {
            BufferedReader qr = new BufferedReader(new FileReader(QUEUE_FILE));
            String line;

            while ((line = qr.readLine()) != null)
            {
                String[] parts = line.split(",");
                String type = parts[0];
                String district = parts[1];
                int priority = Integer.parseInt(parts[2]);

                Incident incident = new Incident(type, district, priority);

                if (priority == 1)
                {
                    incidentQueue.addFirst(incident);
                }
                else
                {
                    incidentQueue.addLast(incident);
                }
            }
            qr.close();

            BufferedReader tr = new BufferedReader(new FileReader(TYPES_FILE));
            while ((line = tr.readLine()) != null)
            {
                todayIncidentTypes.add(line);
            }
            tr.close();

            System.out.println("data loaded from file");
        }
        catch (IOException e)
        {
            System.out.println("error loading data");
        }
    }

    private int readInt(Scanner sc, String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            try
            {
                return Integer.parseInt(sc.nextLine());
            }
            catch (NumberFormatException e)
            {
                System.out.println("please enter a number");
            }
        }
    }

    public static void main(String[] args)
    {
        DispatchSystem app = new DispatchSystem();
        app.runMenu();
    }
}
