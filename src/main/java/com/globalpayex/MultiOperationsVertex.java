package com.globalpayex;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

public class MultiOperationsVertex {

    private static final Logger logger = LoggerFactory.getLogger(WinnerGreeterAsync.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Scanner s = new Scanner(System.in);

        while (true) {
            logger.info("Enter the value of a : ");
            int a = s.nextInt();
            logger.info("Enter the value of b : ");
            int b = s.nextInt();
            vertx.setTimer(4000, id -> performAddition(a,b));
            vertx.setTimer(3000, id -> performMultiplication(a,b));
        }
    }

    public static void performAddition(int a, int b) {
        logger.info("Addition : " + (a + b));
    }

    public static void performMultiplication(int a, int b) {
        logger.info("Multiplication : " + (a * b));
    }
}
 