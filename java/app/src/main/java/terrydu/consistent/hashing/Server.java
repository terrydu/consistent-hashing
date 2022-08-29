package terrydu.consistent.hashing;

public class Server {
    public String id;
    public String name;
    public int weight;

    public Server(String id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }
}
