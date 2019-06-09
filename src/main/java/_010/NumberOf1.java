package _010;

import java.nio.ByteBuffer;

public class NumberOf1 {
    public static final int INT_MAX_MINUS = -2147483648;
    public static final int INT_MAX_PLUS = 2147483647;
    public static final int INT_LENGTH = 32;

    public static void main(String[] args) {
        int count = getCount(125);
        System.out.println("final result is: " + count);
    }

    private static int getCount(int input) {
        int test = 1;
        int count = 0;
        int i = 0;
        printBytes(toBytes(input));
        while (i < INT_LENGTH) {
            if ((input & (test << i++)) != 0) {
                count++;
            }
        }
        return count;
    }

    private static void printDebug(int result) {
        System.out.println(result);
        printBytes(toBytes(result));
    }

    private static void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(String.format("0x%02X", b));
        }
    }

    private static int convert(byte[] arr) {
        ByteBuffer wrapped = ByteBuffer.wrap(arr);
        return wrapped.getInt();
    }

    private static byte[] toBytes(int num) {
        ByteBuffer dbuf = ByteBuffer.allocate(4);
        dbuf.putInt(num);
        return dbuf.array();
    }
}
