package au.com.anz.service;

import au.com.anz.exception.CurrencyConverterException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    public List<String> graph = new ArrayList<>();

    public Set<String> nodes = new HashSet<>();

    public Map<String, String> currencyProperties = new HashMap<>();

    public HashMap<String, Integer> myHashMap = new HashMap<>();

    public BigDecimal[][] adMatrix;

    public Boolean validInput(String[] tokens) {
        if (tokens.length != 4) {
            return Boolean.FALSE;
        }
        try {
            new BigDecimal(tokens[1]);
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public void loadGraph() {

        Properties prop = new Properties();
        InputStream input;
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource("application.properties").getFile());
        try {
            input = new FileInputStream(file);
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        findCurrencyAvailable(prop);
        String[] nodesArray = nodes.toArray(new String[nodes.size()]);
        for (int i = 0; i < nodes.size(); i++) {
            myHashMap.put(nodesArray[i], i);
        }
        createMatrix();
    }

    public void findCurrencyAvailable(Properties prop) {

        for (Object setItem : prop.keySet()) {
            String key = (String) setItem;
            String value = prop.getProperty(key);
            currencyProperties.put(key, value);
            graph.add(key);
            nodes.add(key.substring(0, 3));
            nodes.add(key.substring(3, 6));
        }
    }

    public BigDecimal calculateCurrency(String source, String destination, BigDecimal amount) {
        Integer sourceIndex = myHashMap.get(source);
        Integer destinationIndex = myHashMap.get(destination);
        if (sourceIndex == null || destinationIndex == null) {
            throw new CurrencyConverterException("Unable to find " + source + "/" + destination);
        }
        ArrayList<Integer> unVisted = new ArrayList<>();
        for (int i = 0; i < myHashMap.size(); i++) {
            unVisted.add(i);
        }
        BigDecimal weight[] = new BigDecimal[myHashMap.size()];

        for (int i = 0; i < weight.length; i++) {
            weight[i] = new BigDecimal(Integer.MAX_VALUE);
        }
        weight[sourceIndex] = BigDecimal.ONE;
        while (!unVisted.isEmpty()) {
            int minimumIndex = getMinimum(unVisted, weight);
            for (int i = 0; i < adMatrix.length; i++) {
                if (!adMatrix[minimumIndex][i].equals(BigDecimal.ZERO) && unVisted.contains(i) && weight[minimumIndex].multiply(adMatrix[minimumIndex][i]).compareTo(weight[i]) < 0) {
                    weight[i] = weight[minimumIndex].multiply(adMatrix[minimumIndex][i]).setScale(2, RoundingMode.FLOOR);
                }
            }
            int index = unVisted.indexOf(minimumIndex);
            unVisted.remove(index);
        }

        return weight[destinationIndex].multiply(amount).setScale(4, RoundingMode.FLOOR);
    }

    public int getMinimum(ArrayList<Integer> unVisited, BigDecimal[] weight) {
        BigDecimal minimum = new BigDecimal(Float.MAX_VALUE);
        int position = -1;
        for (int i : unVisited) {
            if (weight[i].compareTo(minimum) < 0) {
                minimum = weight[i];
                position = i;
            }
        }
        return position;
    }

    public BigDecimal[][] createMatrix() {
        create2DBigDecimalArray();
        for (int i = 0; i < nodes.size(); i++) {
            myHashMap.put((String) nodes.toArray()[i], i);
        }
        String[] curr = new String[2];
        for (int i = 0; i < graph.size(); i++) {
            String line = graph.get(i);
            curr[0] = line.substring(0, 3);
            curr[1] = line.substring(3, 6);
            adMatrix[myHashMap.get(curr[0])][myHashMap.get(curr[1])] = new BigDecimal(currencyProperties.get(line)).setScale(4, RoundingMode.FLOOR);
            adMatrix[myHashMap.get(curr[1])][myHashMap.get(curr[0])] = new BigDecimal("1.00").divide(new BigDecimal(currencyProperties.get(line)), 4, RoundingMode.FLOOR);
        }
        return adMatrix;
    }

    public void create2DBigDecimalArray() {
        adMatrix = new BigDecimal[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                adMatrix[i][j] = new BigDecimal(BigInteger.ZERO);
            }
        }
    }

}
