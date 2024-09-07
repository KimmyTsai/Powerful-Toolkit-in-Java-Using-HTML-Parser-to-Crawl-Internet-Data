import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class TopStandardDeviations {
    public static void main(String[] args) throws IOException {
        int start = Integer.parseInt(args[0]) - 1; // Adjust for zero-based index
        int end = Integer.parseInt(args[1]) - 1;   // Adjust for zero-based index

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

        // Calculate the standard deviation for each stock
        List<Double> standardDeviations = new ArrayList<>();
        for (int i = 0; i < symbols.length; i++) {
            List<Double> selectedPrices = new ArrayList<>();
            for (int day = start; day <= end; day++) {
                selectedPrices.add(prices.get(day)[i]);
            }
            double mean = selectedPrices.stream().mapToDouble(a -> a).average().orElse(0.0);
            double sumOfSquares = selectedPrices.stream().mapToDouble(a -> (a - mean) * (a - mean)).sum();
            double standardDeviation = Math.sqrt(sumOfSquares / (selectedPrices.size() - 1)); // Sample standard deviation
            standardDeviations.add(standardDeviation);
        }

        // Sort the stocks by their standard deviation in descending order
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < standardDeviations.size(); i++) {
            indices.add(i);
        }
        indices.sort((i, j) -> Double.compare(standardDeviations.get(j), standardDeviations.get(i)));

        // Prepare decimal format to avoid trailing zeros
        DecimalFormat df = new DecimalFormat("0.##");

        // Write the output to a CSV file
        BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true));  // Set append to true
        bw.write(symbols[indices.get(0)] + "," + symbols[indices.get(1)] + "," + symbols[indices.get(2)] + "," + (start + 1) + "," + (end + 1) + "\n");
        bw.write(df.format(standardDeviations.get(indices.get(0))) + "," + df.format(standardDeviations.get(indices.get(1))) + "," + df.format(standardDeviations.get(indices.get(2))));
        bw.newLine();
        bw.close();
        System.out.println("Output written to output.csv");
    }
}
