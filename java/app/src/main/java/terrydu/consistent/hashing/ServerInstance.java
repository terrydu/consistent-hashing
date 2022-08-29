package terrydu.consistent.hashing;

public class ServerInstance {
    public int loc;
    Server server;

    public ServerInstance(int loc, Server server) {
        this.loc = loc;
        this.server = server;
    }
}
