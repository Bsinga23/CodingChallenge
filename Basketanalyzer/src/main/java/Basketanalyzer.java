import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Basketanalyzer {

    static class FruitEntry {
        String name;
        int size;
        String color;
        String shape;
        int days;

        FruitEntry(String name, int size, String color, String shape, int days) {
            this.name = name;
            this.size = size;
            this.color = color.trim();
            this.shape = shape.trim();
            this.days = days;
        }
    }

    public static void main(String[] args) {
        //if CSV file arguments passed by the user, if not exit without further step
        if (args.length < 1) {
            System.out.println("CSV arguments not passed by Comcast for reading file");
            return;
        }


        String fileName = args[0];//Passing string arguments for File Path
      //  String fileName = "C:\\EDrive\\Studies\\Java\\basket 1.csv";
        List<FruitEntry> fruits = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String name = parts[0].trim();
                int size = Integer.parseInt(parts[1].trim());
                String color = parts[2].trim();
                String shape = parts[3].trim();
                int days = Integer.parseInt(parts[4].trim());

                fruits.add(new FruitEntry(name, size, color, shape, days));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        // 1. Total number of fruit by same name grouping with different shape and color too present , only name is unique
        Map<String, Long> fruitCountstotal = fruits.stream()
                .collect(Collectors.groupingBy(f -> f.name, Collectors.summingLong(f ->f.size)));
       // int totalFruits1 = fruits.add(fruits.entrySet().stream().count());


        // 1. Total number of fruits by same name with unique shape and color, if color or shape changed then it will not count
       Long totalFruits =fruits.stream().count();

        Long totalofAllFruitsintheBasket = fruits.stream()
                .collect(Collectors.summingLong(f -> f.size));



        // 2. Total types of fruits grouping by name only
        Map<String, Long> fruitCounts = fruits.stream()
                .collect(Collectors.groupingBy(f -> f.name, Collectors.counting()));

        // 3. Count of each fruit type in descending order
        List<Map.Entry<String, Long>> sortedCounts = fruitCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        // 4. Characteristics of each fruit
        Map<String, List<String>> characteristics = new HashMap<>();
        for (FruitEntry f : fruits) {
            String desc = f.color + ", " + f.shape;
            characteristics.computeIfAbsent(f.size +" " +f.name, k -> new ArrayList<>()).add(desc);
        }

        // 5. Fruits over 3 days
        Map<String, Long> over3Days = fruits.stream()
                .filter(f -> f.days > 3)
                .collect(Collectors.groupingBy(f -> f.name, Collectors.counting()));

        // Output

        System.out.println("Total Collection of fruit by same unique name only, with different shape or color might present: " + fruitCountstotal);
        System.out.println("Total number of All fruits in the collection by same name,but different color and different Shape(eg:Apple with Red as 1 and Apple with Orange color as 2,.. : " + totalFruits);
        System.out.println("Types of fruit: " + fruitCounts.size());
        System.out.println("Total of All Fruits in the Basket irrespective of name,color,size and Shape= " + totalofAllFruitsintheBasket);
        System.out.println("The number of each type of fruit in descending order:");
        for (Map.Entry<String, Long> entry : sortedCounts) {
            System.out.println(entry.getKey() + ": " + entry.getValue());

          }

        System.out.println("The characteristics (size, color, shape, etc.) of each fruit by type:");
        for (Map.Entry<String, List<String>> entry : characteristics.entrySet()) {
            System.out.println(entry.getKey() + ": " + String.join(" | ", entry.getValue()));
        }

        System.out.println("Have any fruit been in the basket for over 3 days:");
        if (over3Days.isEmpty()) {
            System.out.println("No fruits have been in the basket for over 3 days.");
        } else {
            for (Map.Entry<String, Long> entry : over3Days.entrySet()) {
                System.out.println(entry.getValue() + " " + entry.getKey() + "(s) are over 3 days old");
            }
        }
    }
}