import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<MyObject> data;
    static int parQuantity;

    public static void main(String[] args) throws IOException {

//        data = ProcessFile("Data//data.txt", Spliters.WHITE); // comma / white
        data = ProcessFile("Data//iris_kmeans.txt", Spliters.COMMA); // comma / white

        int k = 0;
        while (true) {
            k = setK();
            if (k == -1) {
                break;
            }
            KMeans kMeans = new KMeans(k, data);
        }
    }

    public static ArrayList<MyObject> ProcessFile(String fname, Spliters mode) throws IOException {
        String spliter = "";

        if (mode == Spliters.WHITE) {
            spliter = "\\s+";
        } else if (mode == Spliters.COMMA) {
            spliter = ",";
        } else {
            throw new RuntimeException("Unsupported mode");
        }

        {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String temp = br.readLine();
            parQuantity = temp.trim().split(spliter).length - 1;
            br.close();
        }

        ArrayList<MyObject> group = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fname));
        String line = "";
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.trim().split(spliter);
            double[] parameters = new double[parQuantity];
            for (int i = 0; i < parQuantity; i++) {
                parameters[i] = Double.parseDouble(parts[i].replace(",", "."));
            }
            group.add(new MyObject(parameters, parts[parQuantity]));
        }
        return group;
    }

    public static int setK() {
        int k;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Wprowadz parametr 'k' lub '-1' aby wyjsc: ");
            k = scanner.nextInt();
            if (k == -1 || k > 0 && k < data.size()) {
                break;
            } else {
                System.out.println("K must be between 0 and " + (data.size() - 1));
            }
        }
        System.out.println();
        return k;
    }
}