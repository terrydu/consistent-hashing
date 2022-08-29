package terrydu.consistent.hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

public class HashElement {
    public String id;
    public String name;
    public HashCode hash;
    public BigInteger bigInt;
    public int modOneHundredK;
    public int modOneHundred;

    private static final BigInteger oneHundredK = new BigInteger(String.valueOf(Constants.RANGE));
    private static final BigInteger oneHundred = new BigInteger(String.valueOf(Constants.PRINT_RANGE));

    public HashElement(String id, String name) {
        this.id = id;
        this.name = name;
        hash = hash(name);
        bigInt = new BigInteger(hash.asBytes());
        modOneHundredK = bigInt.mod(oneHundredK).intValue();
        modOneHundred = bigInt.mod(oneHundred).intValue();
    }

    private HashCode hash(String key) {
        return Hashing.sha256()
            .hashString(key, StandardCharsets.UTF_8);
    }
}
