import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class StockRegressionLineCalculator {
    public static void main(String[] args) throws IOException {
        String stock = args[0];
        int start1 = Integer.parseInt(args[1]) - 1; // Adjust for zero-based index
        int end1 = Integer.parseInt(args[2]) - 1;   // Adjust for zero-based index

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

        // Calculate the linear regression parameters
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        int n = end1 - start1 + 1;
        for (int i = start1; i <= end1; i++) {
            double x = i + 1; // Time in days, adjusted to start from 1
            double y = prices.get(i)[stockIndex];
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }
        double meanX = sumX / n;
        double meanY = sumY / n;
        double b1 = (sumXY - n * meanX * meanY) / (sumXX - n * meanX * meanX);
        double b0 = meanY - b1 * meanX;

        // Prepare decimal format to avoid trailing zeros
        DecimalFormat df = new DecimalFormat("0.##");

        // Write the output to a CSV file
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true)); // Set append to true
        bw.write(stock + "," + (start1 + 1) + "," + (end1 + 1) + "\n");
        bw.write(df.format(b1) + "," + df.format(b0));
        bw.newLine();
        bw.close();
        System.out.println("Output written to output.csv");
    }
}
