public class Fibonacci {

    public static void main(String[] args) {
        System.out.println(getFibonacci(10));
        System.out.println(getFibonacci(10, 1, 1, 1));
    }

    private static long getFibonacci(int n) {
        long[] array = new long[]{1, 1, 2};
        int i = 0;
        while (i + 3 < n) {
            System.out.println(String.format("%d, %d, %d", array[0], array[1], array[2]));
            array[0] = array[1] + array[2];
            array[1] = array[2] + array[0];
            array[2] = array[0] + array[1];
            i += 3;
        }
        System.out.println(String.format("%d, %d, %d", array[0], array[1], array[2]));
        return array[n - i - 1];
    }

    private static long getFibonacci(int n, int count, long a, long b) {
        return count >= n ? a : getFibonacci(n, ++count, b, a + b);
    }
}
