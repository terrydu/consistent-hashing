package terrydu.consistent.hashing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Loop {

//    hm.put(100,"Amit");
    //for (Map.Entry m:hm.entrySet()) {
    //    System.out.println(m.getKey()+" "+m.getValue());
    //}

    HashMap<Integer, HashElement> personLoop = new HashMap<>();
    HashMap<Integer, ServerInstance> serverLoop = new HashMap<>();       // Note that the same server will be added multiple times, once for each weight.
    int personMaxId = 0;
    int serverMaxId = 0;

    public void addPerson(HashElement hash) {
        personLoop.put(hash.modOneHundred, hash);
        personMaxId++;
    }

    public void addServer(Server server) {
        // For each 'weight' of our new server...
        for (int i=0; i<server.weight; i++) {
            int index = identifyInsertionPoint();
            addServerInstanceToLoop(index, server);
        }
        serverMaxId++;
    }

    /**
     * A server has an Id, which is the same across all of its instances.
     * We are going to remove all ServerInstances that have a matching ServerId.
     * @param serverIdText The ID of the server, as text, that we wish to remove.
     */
    public void removeServer(String serverId) {
        ArrayList<Integer> serverInstancesToRemove = new ArrayList<>();

        Set<Integer> serverInstanceLocs = serverLoop.keySet();
        for (int serverInstanceLoc : serverInstanceLocs) {
            var srv = serverLoop.get(serverInstanceLoc).server;
            if (serverId.equals(srv.id)) {
                serverInstancesToRemove.add(serverInstanceLoc);
            }
        }

        for (int serverInstanceKey : serverInstancesToRemove) {
            serverLoop.remove(serverInstanceKey);
        }

        System.out.println("We've removed serverId " + serverId);
    }

    /**
     * Find biggest gap/span between servers in our loop.
     * Add this server instance in the middle of the gap.
     */
    private int identifyInsertionPoint() {
        int size = serverLoop.size();

        if (size == 0) {
            // Start at half-way mark.
            return Constants.PRINT_RANGE / 2;
        }

        // Get all the keys (the locations), and sort them in order.
        Set<Integer> serverKeys = serverLoop.keySet();
        List<Integer> serverList = new ArrayList<Integer>(serverKeys);
        Collections.sort(serverList);

        int longestGap = 0;     // Biggest gap we've seen so far.
        int insertPoint = 0;    // The half-way point in the biggest gap we've seen so far.
        int cursor = 0;         // Our current location in the server loop.
        int prevCur = 0;        // Our previous location in the server loop.
        if (size > 1) {
            // If there is only 1 element, then it's handled by the edge case below this 'if' statement. 
            // But if there is more than 1 then we wish to start by looking at diff between 2nd element and 1st element.
            prevCur = serverList.get(0);
            for (int i=1; i<size; i++) {
                cursor = serverList.get(i);
                int diff = cursor - prevCur;
                if (diff > longestGap) {
                    longestGap = diff;
                    insertPoint = (diff / 2) + prevCur;
                }

                prevCur = cursor;
            }
        }
        // And the last (edge) case: When we wrap around from the last element to the first element.
        int endLen = Constants.PRINT_RANGE - serverList.get(size-1);
        int startLen = serverList.get(0);
        if (endLen + startLen > longestGap) {
            int halfDiff = (endLen + startLen) / 2;
            if (halfDiff + serverList.get(size-1) > Constants.PRINT_RANGE) {
                // The index will be in the start section.
                insertPoint = serverList.get(0) - halfDiff;
            } else {
                // The index will be in the end section.
                insertPoint = serverList.get(size-1) + halfDiff;
            }
        }

        return insertPoint;
    }

    /**
     * Add the ServerInstance to the Server, and also add it to the (server) loop.
     */
    private void addServerInstanceToLoop(int index, Server server) {
        var serverInstance = new ServerInstance(index, server);
        serverLoop.put(index, serverInstance);
    }
}
