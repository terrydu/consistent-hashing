/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package terrydu.consistent.hashing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class App {

    static Loop loop = new Loop();
    static ArrayList<String> personNames = new ArrayList<>(
        Arrays.asList(
            "john",
            "bill",
            "jane",
            "alan",
            "mila",
            "ella",
            "liam",
            "aria",
            "zoey",
            "noah",
            "leah",
            "ruby",
            "luke",
            "ezra",
            "lily",
            "jose",
            "adam"
        ));
    static InputStreamReader inputStream = null;
    static BufferedReader br = null;

    public void start() {
        System.out.println("We'll start with 3 servers, each with equal weight (2 weight), and 3 people on a 'ring' (loop) that is conceptually 100 in length.");
        System.out.println("");

        createPeople();
        printPeople();

        createServers();
        printServers();

        //associatePeopleToServers();

        visualize(loop.personLoop.keySet(), loop.serverLoop.keySet());

        while (true) {
            // Start our interactive loop.
            boolean isQuit = prompt();
            if (isQuit) { return; }

            //associatePeopleToServers();
            visualize(loop.personLoop.keySet(), loop.serverLoop.keySet());
        }
    }

    /**
     * Create the people, then add them to the loop.
     */
    void createPeople() {
        Arrays.asList(
            new HashElement("1", personNames.get(0)),
            new HashElement("2", personNames.get(1)),
            new HashElement("3", personNames.get(2))
        )
        .stream().forEach(elem -> 
            loop.addPerson(elem)
        );
    }

    /**
     * Print the people.
     */
    void printPeople() {
        System.out.println("The existing inputs (people) are as follows:");

        Set<Integer> personKeys = loop.personLoop.keySet();
        personKeys.stream().forEach(key -> {
            HashElement hash = loop.personLoop.get(key);
            System.out.println("  ID " + hash.id + ": '" + hash.name + "', hash " + hash.hash + ", loc: " + hash.modOneHundred);
        });
    }

    /**
     * Create the servers, then add them to the loop.
     */
    void createServers() {
        Arrays.asList(
            new Server("1", "Server1", 2),
            new Server("2", "Server2", 2),
            new Server("3", "Server3", 2)
        )
        .stream().forEach(server -> 
            loop.addServer(server)
        );
    }

    /**
     * Print existing server instances.
     */
    void printServers() {
        System.out.println("And the existing servers are arranged on our loop as follows:");
        Set<Integer> serverKeys = loop.serverLoop.keySet();
        serverKeys.stream().forEach(key -> {
            var s = loop.serverLoop.get(key);
            System.out.println("  ID " + s.server.id + ": " + s.server.name + ", loc: " + s.loc);
        });
    }

    /**
     * Find the server that should be associated with this incoming request (this person).  We 
     * search for servers that are "ahead" of us on the loop.
     * @param person The incoming request.
     * @return The ServerInstance (not the Server) that was ahead of the person on the loop. 
     * Typically we'll use that to get the actual Server.
     */
    ServerInstance resolvePersonToServer(HashElement person) {
        Set<Integer> serverInstanceKeys = loop.serverLoop.keySet();
        List<Integer> serverInstanceList = new ArrayList<Integer>(serverInstanceKeys);
        Collections.sort(serverInstanceList);

        for (Integer serverInstanceKey : serverInstanceList) {
            if (serverInstanceKey < person.modOneHundred) {
                continue;
            } else {
                return loop.serverLoop.get(serverInstanceKey);
            }
        }

        // If we get this far, then it means we searched the whole thing and
        // couldn't find a server with a bigger location. Hence we need to wrap to the start.
        return loop.serverLoop.get(serverInstanceList.get(0));
    }

    boolean prompt() {
        System.out.println("");
        System.out.println("Choose what to do next:");
        System.out.println("  1 - Add a server");
        System.out.println("  2 - Add a person");
        System.out.println("  3 - Remove a server");
        System.out.println("  0 - Quit");

        String input = readLine();
        switch (input) {
            case "1": {
                System.out.println("What weight should it have? (2 is default)");
                String weightText = readLine();

                int nextId = loop.serverMaxId + 1;
                String serverId = Integer.toString(nextId);
                var srv = new Server(serverId, "Server" + serverId, Integer.parseInt(weightText));
                loop.addServer(srv);
                break;
            }
            case "2": {
                var personIdText = Integer.toString(loop.personMaxId + 1);
                var hash = new HashElement(personIdText, personNames.get(loop.personMaxId));
                loop.addPerson(hash);
                break;
            }
            case "3": {
                System.out.println("Which server Id should be removed?");
                String serverIdText = readLine();
                loop.removeServer(serverIdText);
                break;
            }
            default: {
                System.out.println("Goodbye");
                return true;
            }
        }
        return false;
    }

    String readLine() {
        String newLine = null;
        if (inputStream == null) {
            inputStream = new InputStreamReader(System.in);
        }
        if (br == null) {
            br = new BufferedReader(inputStream);
        }
        try {
            newLine = br.readLine();
        } catch(Exception e) {
            String errorMsg = "ERROR - Unable to read input from STDIN! Details: " + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return newLine;
    }

    /**
     * Decide which format you'd like to visualize the 'ring' (the loop) as.
     */
    void visualize(Set<Integer> personKeys, Set<Integer> serverKeys) {
        //display1(personKeys, serverKeys);
        display2(personKeys, serverKeys);
    }

    /**
     * This is a very abbreviated version, just showing numbers for people and servers.
     */
    void display1(Set<Integer> personKeys, Set<Integer> serverKeys) {
        System.out.println("");
        System.out.println("We'll map everything to our 'ring' (loop) that we'll visualize as being 100 in length.");
        System.out.println("");

        StringBuffer firstLine  = new StringBuffer("People:      ");
        StringBuffer secondLine = new StringBuffer("Loop:    [0] ");
        StringBuffer thirdLine  = new StringBuffer("Servers:     ");
        
        List<Integer> peopleList = new ArrayList<Integer>(personKeys);
        Collections.sort(peopleList);

        List<Integer> serverList = new ArrayList<Integer>(serverKeys);
        Collections.sort(serverList);

        for (int i=1; i<=100; i++) {
            boolean appendedPerson = false;
            for (Integer personKey : personKeys) {
                if (personKey.intValue() == i) {
                    firstLine.append(loop.personLoop.get(personKey).id);
                    appendedPerson = true;
                }
            }
            if (!appendedPerson) { firstLine.append(" "); }

            secondLine.append("-");

            boolean appendedServer = false;
            for (Integer serverKey : serverKeys) {
                if (serverKey.intValue() == i) {
                    thirdLine.append(loop.serverLoop.get(serverKey).server.id);
                    appendedServer = true;
                    break;
                }
            }
            if (!appendedServer) { thirdLine.append(" "); }
        }
        secondLine.append(" [100]");

        System.out.println(firstLine);
        System.out.println(secondLine);
        System.out.println(thirdLine);
    }

    /**
     * This is a more verbose way to visualize the loop, with a separate line for each person and server instance.
     */
    void display2(Set<Integer> personKeys, Set<Integer> serverKeys) {
        System.out.println("");
        System.out.println("We'll map everything to our 'ring' (loop) that we'll visualize as being 100 in length.");
        System.out.println("");

        List<Integer> peopleList = new ArrayList<Integer>(personKeys);
        //Collections.sort(peopleList);

        List<Integer> serverList = new ArrayList<Integer>(serverKeys);
        //Collections.sort(serverList);

        System.out.println("People:      ");
        for (Integer personKey : peopleList) {
            StringBuffer personBuffer = new StringBuffer("             ");
            int len = personKey.intValue() - 1;
            for (int i=0; i<len; i++) {
                personBuffer.append(" ");
            }
            personBuffer.append("[" + personKey.intValue() + "] " + loop.personLoop.get(personKey).name);
            System.out.println(personBuffer);
        }
        System.out.println("Loop:    [0] ---------------------------------------------------------------------------------------------------- [100]");
        System.out.println("Servers:     ");
        for (Integer serverKey : serverList) {
            StringBuffer serverBuffer = new StringBuffer("             ");
            int len = serverKey.intValue() - 1;
            for (int i=0; i<len; i++) {
                serverBuffer.append(" ");
            }
            serverBuffer.append("[" + serverKey.intValue() + "] " + loop.serverLoop.get(serverKey).server.name);
            System.out.println(serverBuffer);
        }

        System.out.println("");
        System.out.println("Load balancing resolutions:");
        Collections.sort(peopleList);
        for (int personKey : peopleList) {
            HashElement hash = loop.personLoop.get(personKey);
            ServerInstance srv = resolvePersonToServer(hash);
            System.out.println("  Person '" + hash.name + "' at [" + hash.modOneHundred +"] is resolved to '" + srv.server.name + "' [" + srv.loc + "]");
        }
    }

    public static void main(String[] args) {
        new App().start();
    }
}
