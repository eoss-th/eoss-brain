package com.brainy.service.console;

import com.brainy.service.Endpoint;

import java.util.Scanner;

public class ConsoleEndpointTest {

    public static void main(String [] args) {

        Endpoint endpoint = new ConsoleEndpoint("examples/ocm");

        Scanner scanner = new Scanner(System.in, "UTF-8");

        while(true) {
            System.out.print("You:>>");
            System.out.print(endpoint.process(scanner.nextLine()));
            System.out.println();
        }

    }

}