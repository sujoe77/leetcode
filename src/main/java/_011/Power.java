package _011;

import static java.lang.Math.abs;

public class Power {

    public static void main(String[] args) {
        System.out.println(power(3, 5));
        System.out.println(power2(3, 5));
    }

    public static long power(long n, long pow) {
        if (pow == 0 || n == 1) {
            return 1;
        }
        if (n == 0) {
            return 0;
        }

        long result = 1;
        long check = 1;
        long temp = n;

        while (check <= abs(pow)) {
            if ((pow & check) != 0) {
                result *= temp;
            }
            temp *= temp;
            check *= 2;
        }
        return pow > 0 ? result : 1 / result;
    }

    public static long power2(long n, long pow) {
        if (pow == 0 || n == 1) {
            return 1;
        }
        if (n == 0) {
            return 0;
        }

        long result = 1;
        long check = pow;
        long temp = n;

        while (check > 0) {
            if (check % 2 != 0) {
                result *= temp;
            }
            temp *= temp;
            check = check / 2;
        }
        return pow > 0 ? result : 1 / result;
    }
}
