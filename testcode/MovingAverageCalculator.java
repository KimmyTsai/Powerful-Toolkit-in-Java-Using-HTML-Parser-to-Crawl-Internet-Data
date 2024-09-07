import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class MovingAverageCalculator {
    public static void main(String[] args) throws IOException {
        String stock = args[0];
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);

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

        // Calculate the moving averages
        List<Double> averages = new ArrayList<>();
        for (int day = start - 1; day <= end - 5; day++) {
            double sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += prices.get(day + i)[stockIndex];
            }
            averages.add(sum / 5);
        }

        // Prepare decimal format to avoid trailing zeros
        DecimalFormat df = new DecimalFormat("0.##");

        // Append the output to a CSV file without overwriting the existing content
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true));  // Set append to true
        bw.write(stock + "," + start + "," + end + "\n");
        for (int i = 0; i < averages.size(); i++) {
            bw.write(df.format(averages.get(i)));
            if (i < averages.size() - 1) {
                bw.write(",");
            }
        }
        bw.newLine();
        bw.close();
        System.out.println("Output written to output.csv");
    }
}
