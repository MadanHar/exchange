package au.com.anz.service;

import au.com.anz.exception.CurrencyConverterException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class CurrencyExchangeServiceImplTest {

    private CurrencyExchangeService currencyExchangeService;

    @Before
    public void setUp() {
        currencyExchangeService = new CurrencyExchangeServiceImpl();
        currencyExchangeService.loadGraph();
    }

    @Test
    public void testAudToUsd() {
        BigDecimal amount = currencyExchangeService.calculateCurrency("AUD", "USD", new BigDecimal("1"));
        Assert.assertEquals(amount.setScale(2), new BigDecimal("0.83"));
    }

    @Test
    public void testSelfExchange() {
        BigDecimal amount = currencyExchangeService.calculateCurrency("AUD", "AUD", new BigDecimal("100"));
        Assert.assertEquals(amount.setScale(2), new BigDecimal("100.00"));
    }

    @Test(expected = CurrencyConverterException.class)
    public void testSourceDestinationNotFound() {
        currencyExchangeService.calculateCurrency("AUD", "XYZ", new BigDecimal("100"));
    }

    @Test
    public void testInvalidInput() {
        String[] tokens = {"AUD", "100.oo", "in", "USD"};
        Boolean isValid = currencyExchangeService.validInput(tokens);
        Assert.assertEquals(isValid, Boolean.FALSE);
    }

    @Test
    public void testValidInput() {
        String[] tokens = {"AUD", "100.00", "in", "USD"};
        Boolean isValid = currencyExchangeService.validInput(tokens);
        Assert.assertEquals(isValid, Boolean.TRUE);
    }

}
