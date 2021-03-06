import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoadRoute {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public ArrayList<RouteGenerator> returnerOfRoutes(String fileName) throws FileNotFoundException {

        String file_name = "Bad_Routes_Report.txt";
        CleanFile.clean ( file_name  );
        String csvFile = fileName;
        LoadRoute loadRoute = new LoadRoute();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(csvFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<RouteGenerator> routeGenerators = new ArrayList<>();

        Timestamp timestampZero = new Timestamp(0);
        while (scanner.hasNext()) {

            List<String> line = loadRoute.parseLine(scanner.nextLine());
            RouteGenerator temp = new RouteGenerator();
            int x, y;

            String tempString = line.get(0);
            String sanitized = tempString.replaceAll("[\uFEFF-\uFFFF]", "");
            Timestamp timestamp = new Timestamp(System.nanoTime());
            try {
                timestamp.setTime(Long.parseLong(sanitized));
            } catch (NumberFormatException e) {
                System.out.println("Zły format Timestamp: " + sanitized);
                continue;
            }

            if (timestamp.after(timestampZero)) {

                temp.setOrder(sanitized);
                temp.setDriverName(line.get(1));
                for (int i = 2; i <= 10; i += 2) {
                    if (line.size() > i) {
                        try {
                            x = Integer.parseInt(line.get(i));
                            y = Integer.parseInt(line.get(i + 1));
                        } catch (NumberFormatException e) {
                            System.out.println("Zły format paczki dla Timestamp: " + sanitized);
                            continue;
                        }
                        temp.setParcel(new Parcel(x, y));
                    } else {
                        temp.setParcel(new Parcel(0, 0));
                    }
                }
                routeGenerators.add(temp);
                timestampZero.setTime(timestamp.getTime());
            } else {
                System.out.print("Paczki z timestamp: " + line.get(0) + " Kierowca: " + line.get(1));
                System.out.println(" - Odebrana za późno");


                FileWrite.writefile ( "Paczki z timestamp: " + line.get(0) + " Kierowca: " + line.get(1) + " - Odebrana za późno\n" , file_name );
            }

        }
        //RouteGenerator temp = routeGenerators.get(0);
        //for (int i = 0; i < routeGenerators.size(); i++){
        //    if(temp.getOrder() <= routeGenerators.get(i).getOrder()){

        scanner.close();
        return routeGenerators;
    }

    public static void main(String[] args) throws Exception {
        LoadRoute loadRoute = new LoadRoute();
        ArrayList<RouteGenerator> routeGenerators = loadRoute.returnerOfRoutes("DataInputGroupWT1115.txt");

        for (RouteGenerator rg : routeGenerators) {
            rg.writeParcels();
        }
    }


    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {
            // ustawiane okreslonych symbolo jako defaultowe oraz walidacja bledow roznego rodzaju
        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;
                } else if ((ch == '\r') || (ch == '(') || (ch == ')') || (ch == ';')) {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

}