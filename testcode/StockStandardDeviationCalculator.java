import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class StockStandardDeviationCalculator {
    public static void main(String[] args) throws IOException {
        String stock = args[0];
        int start = Integer.parseInt(args[1]) - 1; // Adjust for zero-based index
        int end = Integer.parseInt(args[2]) - 1;   // Adjust for zero-based index

        // Read the CSV file
        BufferedReader br = new BufferedReader(new FileReader("data.csv"));
        String line = br.readLine();  // Read header
        String[] symbols = line.split(",");
        List<double[]> prices = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            double[] dailyPrices = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                dailyPrices[i] = Double.parseDouble(data[i]);
            }
            prices.add(dailyPrices);
        }
        br.close();

        // Find the index of the stock
        int stockIndex = -1;
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].equals(stock)) {
                stockIndex = i;
                break;
            }
        }
        if (stockIndex == -1) {
            System.out.println("Stock symbol not found.");
            return;
        }

        // Calculate the standard deviation for the specified range
        List<Double> selectedPrices = new ArrayList<>();
        for (int day = start; day <= end; day++) {
            selectedPrices.add(prices.get(day)[stockIndex]);
        }

        double mean = selectedPrices.stream().mapToDouble(a -> a).average().orElse(0.0);
        double sumOfSquares = selectedPrices.stream().mapToDouble(a -> (a - mean) * (a - mean)).sum();
        double standardDeviation = Math.sqrt(sumOfSquares / (selectedPrices.size() - 1)); // Sample standard deviation

        // Prepare decimal format to avoid trailing zeros
        DecimalFormat df = new DecimalFormat("0.##");

        // Write the output to a CSV file
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true));  // Set append to true
        bw.write(stock + "," + (start + 1) + "," + (end + 1) + "\n"); // Adjust back for human-readable dates
        bw.write(df.format(standardDeviation));
        bw.newLine();
        bw.close();
        System.out.println("Output written to output.csv");
    }
}
