import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class HtmlParser {
    public static void main(String[] args) throws IOException {
        String url = "https://pd2-hw3.netdb.csie.ncku.edu.tw/";
        

        if(args[0].equals("0")){
            try {
                String filePath = "data.csv";
                Document doc = Jsoup.connect(url).get();

                // 抓取標題
                String title = doc.title();

                // 抓天數
                Pattern pattern = Pattern.compile("day(\\d+)");
                Matcher matcher = pattern.matcher(title);
                int dayNumber = -1;
                if (matcher.find()) {
                    dayNumber = Integer.parseInt(matcher.group(1));
                }

                if (dayNumber == -1) {
                    System.out.println("天數錯誤");
                    return;
                }

                List<String> lines = new ArrayList<>();
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    lines = Files.readAllLines(path);
                }

                while (lines.size() <= dayNumber) {
                    lines.add("");
                }

                // 抓股票價格
                String bodyText = doc.body().text();
                Matcher priceMatcher = Pattern.compile("\\b\\d+\\.\\d{1,2}\\b").matcher(bodyText);
                List<String> prices = new ArrayList<>();
                while (priceMatcher.find()) {
                    prices.add(priceMatcher.group());
                }

                lines.set(dayNumber, String.join(",", prices));

                // 只有第一次要印股票價格
                if (lines.get(0).isEmpty()) {
                    Matcher nameMatcher = Pattern.compile("\\b[A-Z]+(-[A-Z]+)?\\b").matcher(bodyText);
                    List<String> names = new ArrayList<>();
                    while (nameMatcher.find()) {
                        names.add(nameMatcher.group());
                    }
                    lines.set(0, String.join(",", names));
                }

                // 將更新的內容寫回 CSV 文件，行尾不加逗號
                Files.write(path, lines);

                // System.out.println("CSV file has been updated successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(args[0].equals("1")){
            
            if(args[1].equals("0")){
                try (FileInputStream fis = new FileInputStream("data.csv");
                FileOutputStream fos = new FileOutputStream("output.csv")) {
                byte[] buffer = new byte[1024];
                int length;
                
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            else{
                String stock = args[2];
                int start = Integer.parseInt(args[3]);
                int end = Integer.parseInt(args[4]);

                if(args[1].equals("1")){
                
                    BufferedReader br = new BufferedReader(new FileReader("data.csv"));
                    String line = br.readLine(); 
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

                    int stockIndex = -1;
                    for (int i = 0; i < symbols.length; i++) {
                        if (symbols[i].equals(stock)) {
                            stockIndex = i;
                            break;
                        }
                    }
                    if (stockIndex == -1) {
                        System.out.println("找不到股票名稱");
                        return;
                    }

                    // 算移動平均
                    List<Double> averages = new ArrayList<>();
                    for (int day = start - 1; day <= end - 5; day++) {
                        double sum = 0;
                        for (int i = 0; i < 5; i++) {
                            sum += prices.get(day + i)[stockIndex];
                        }
                        averages.add(sum / 5);
                    }

                    // 去除尾端的0
                    DecimalFormat df = new DecimalFormat("0.##");

                    // 不要覆寫
                    BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true)); //true代表不要清檔
                    bw.write(stock + "," + start + "," + end + "\n");
                    for (int i = 0; i < averages.size(); i++) {
                        bw.write(df.format(averages.get(i)));
                        if (i < averages.size() - 1) {
                            bw.write(",");
                        }
                    }
                    bw.newLine();
                    bw.close();
                }
                else if(args[1].equals("2")){
                    int start1 = start-1; //順序需減1
                    int end1 = end -1;

                    BufferedReader br = new BufferedReader(new FileReader("data.csv"));
                    String line = br.readLine();  
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

                    int stockIndex = -1;
                    for (int i = 0; i < symbols.length; i++) {
                        if (symbols[i].equals(stock)) {
                            stockIndex = i;
                            break;
                        }
                    }
                    if (stockIndex == -1) {
                        System.out.println("找不到股票名稱");
                        return;
                    }

                    // 算標準差
                    List<Double> selectedPrices = new ArrayList<>();
                    for (int day = start1; day <= end1; day++) {
                        selectedPrices.add(prices.get(day)[stockIndex]);
                    }

                    double mean = selectedPrices.stream().mapToDouble(a -> a).average().orElse(0.0);
                    double sumOfSquares = selectedPrices.stream().mapToDouble(a -> (a - mean) * (a - mean)).sum();
                    double standardDeviation = Math.sqrt(sumOfSquares / (selectedPrices.size() - 1));

                    DecimalFormat df = new DecimalFormat("0.##");

                    BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true));
                    bw.write(stock + "," + (start1 + 1) + "," + (end1 + 1) + "\n"); // 加回去，變成正常的天數
                    bw.write(df.format(standardDeviation));
                    bw.newLine();
                    bw.close();
                }
                else if(args[1].equals("3")){

                    int start1 = start-1; //順序需減1
                    int end1 = end -1;

                    BufferedReader br = new BufferedReader(new FileReader("data.csv"));
                    String line = br.readLine(); 
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

                    // 算標準差
                    List<Double> standardDeviations = new ArrayList<>();
                    for (int i = 0; i < symbols.length; i++) {
                        List<Double> selectedPrices = new ArrayList<>();
                        for (int day = start1; day <= end1; day++) {
                            selectedPrices.add(prices.get(day)[i]);
                        }
                        double mean = selectedPrices.stream().mapToDouble(a -> a).average().orElse(0.0);
                        double sumOfSquares = selectedPrices.stream().mapToDouble(a -> (a - mean) * (a - mean)).sum();
                        double standardDeviation = Math.sqrt(sumOfSquares / (selectedPrices.size() - 1)); // Sample standard deviation
                        standardDeviations.add(standardDeviation);
                    }

                    // 排序
                    List<Integer> indices = new ArrayList<>();
                    for (int i = 0; i < standardDeviations.size(); i++) {
                        indices.add(i);
                    }
                    indices.sort((i, j) -> Double.compare(standardDeviations.get(j), standardDeviations.get(i)));

                    DecimalFormat df = new DecimalFormat("0.##");

                    // 放進CSV
                    BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true));  // Set append to true
                    bw.write(symbols[indices.get(0)] + "," + symbols[indices.get(1)] + "," + symbols[indices.get(2)] + "," + (start1 + 1) + "," + (end1 + 1) + "\n");
                    bw.write(df.format(standardDeviations.get(indices.get(0))) + "," + df.format(standardDeviations.get(indices.get(1))) + "," + df.format(standardDeviations.get(indices.get(2))));
                    bw.newLine();
                    bw.close();
                }
                else if(args[1].equals("4")){
                    int start1 = start-1; //順序需減1
                    int end1 = end -1;

                    BufferedReader br = new BufferedReader(new FileReader("data.csv"));
                    String line = br.readLine(); 
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

                    int stockIndex = -1;
                    for (int i = 0; i < symbols.length; i++) {
                        if (symbols[i].equals(stock)) {
                            stockIndex = i;
                            break;
                        }
                    }
                    if (stockIndex == -1) {
                        System.out.println("找不到股票名稱");
                        return;
                    }

                    // 開始計算回歸直線
                    double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
                    int n = end1 - start1 + 1;
                    for (int i = start1; i <= end1; i++) {
                        double x = i + 1; // Time in days, adjusted to start from 1(GPT)
                        double y = prices.get(i)[stockIndex];
                        sumX += x;
                        sumY += y;
                        sumXY += x * y;
                        sumXX += x * x;
                    }
                    double meanX = sumX / n;
                    double meanY = sumY / n;

                    double sXX = sumXX - sumX * meanX;
                    double sXY = sumXY - sumX * meanY;
                    double b1 = sXY / sXX;
                    double b0 = meanY - b1 * meanX;

                    DecimalFormat df = new DecimalFormat("0.##");

                    BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true));
                    bw.write(stock + "," + (start1 + 1) + "," + (end1 + 1) + "\n");
                    bw.write(df.format(b1) + "," + df.format(b0));
                    bw.newLine();
                    bw.close();
                }
            }
        }
    }
}
