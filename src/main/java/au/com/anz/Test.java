package au.com.anz;

import au.com.anz.exception.CurrencyConverterException;
import au.com.anz.service.CurrencyExchangeService;
import au.com.anz.service.CurrencyExchangeServiceImpl;

import java.math.BigDecimal;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        CurrencyExchangeService currencyExchangeService = new CurrencyExchangeServiceImpl();
        currencyExchangeService.loadGraph();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input != null && !input.isEmpty()) {
                String[] tokens = input.split(" ");
                if (currencyExchangeService.validInput(tokens)) {
                    try {
                        System.out.println(currencyExchangeService.calculateCurrency(tokens[0], tokens[3], new BigDecimal(tokens[1])));
                    } catch (CurrencyConverterException c) {
                        System.out.println(c.getMessage());
                    }
                } else {
                    System.out.println("Input parameters invalid");
                }
            }
        }
    }

}
