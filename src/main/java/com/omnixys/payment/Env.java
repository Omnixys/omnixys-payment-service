package com.omnixys.payment;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    static {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry ->
            {
//                System.out.println("Key=" + entry.getKey() + " Value=" + entry.getValue());
                System.setProperty(
                    entry.getKey(), entry.getValue()
                );
            }
        );
    }
}
