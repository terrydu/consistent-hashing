package terrydu.consistent.hashing;

import java.math.BigInteger;

public class Constants {

    public static final int RANGE = 100000;
    public static final int PRINT_RANGE = 100;

    public static final BigInteger ONE_HUNDRED_K = new BigInteger(String.valueOf(RANGE));
    public static final BigInteger ONE_HUNDRED = new BigInteger(String.valueOf(PRINT_RANGE));

}
