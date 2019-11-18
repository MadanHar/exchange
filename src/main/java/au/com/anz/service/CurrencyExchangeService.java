package au.com.anz.service;

import java.math.BigDecimal;

public interface CurrencyExchangeService {

    Boolean validInput(String[] tokens);

    void loadGraph();

    BigDecimal calculateCurrency(String source, String destination, BigDecimal amount);

}
