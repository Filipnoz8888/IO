import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {


        AdjacentMatrixGenerator adjacentMatrixGenerator = new AdjacentMatrixGenerator();
        CheckInputData checkInputData = new CheckInputData();
        RouteGenerator routeGenerator = new RouteGenerator();
        Floyd_Arraylist floyd_arraylist = new Floyd_Arraylist ();

        try {
            checkInputData.read("data.txt");
        } catch (IOException e) {
            System.out.println("there is no such a file");
        }

        checkInputData.separate();
        //checkInputData.print_hor();
        //System.out.println ();
        //checkInputData.print_ver();
        //System.out.println ();

        adjacentMatrixGenerator.adjacentGenerator(checkInputData.getHorizontal(), checkInputData.getVertical());
        //adjacentMatrixGenerator.print(adjacentMatrixGenerator.getAdjacent());

        // floyd_arraylist.print ( floyd_arraylist.Floyd ( adjacentMatrixGenerator.getAdjacent () ) );
        floyd_arraylist.startPath ( adjacentMatrixGenerator.getAdjacent());
        floyd_arraylist.infinity (  adjacentMatrixGenerator.getAdjacent () );
        //System.out.println ();
        //System.out.println ("Macierz Sasiedztwa");

        //System.out.println ("Macierz Odleglosci");
        //floyd_arraylist.print ( floyd_arraylist.floyd ( adjacentMatrixGenerator.getAdjacent () ) );
        //System.out.println ();
        //System.out.println ("Macierz sciezek");
        //floyd_arraylist.print ( floyd_arraylist.P );
        //floyd_arraylist.choose_the_shortest ( 6,12,8, floyd_arraylist.floyd ( adjacentMatrixGenerator.getAdjacent () ) );

        int numberOfElementsInRow = checkInputData.getVertical().get(0).size();
        //routeGenerator.generateParcels(5, numberOfElementsInRow);
        //routeGenerator.writeParcels();
        //routeGenerator.generateParcelsNumbers(routeGenerator.getParcels(), numberOfElementsInRow);
        //System.out.println(routeGenerator.getParcelsNumber());
        //floyd_arraylist.choose_the_shortest ( routeGenerator.getParcelsNumber(), floyd_arraylist.floyd ( adjacentMatrixGenerator.getAdjacent () ) );


        String csvFile = "routes.txt";
        LoadRoute loadRoute = new LoadRoute ();
        Scanner scanner = new Scanner(new File (csvFile));
        ArrayList<RouteGenerator> routeGenerators= new ArrayList<>();
        while (scanner.hasNext()) {
            List<String> line = loadRoute.parseLine(scanner.nextLine());
            RouteGenerator temp = new RouteGenerator();
            temp.setOrder(line.get(0));
            temp.setDriverName(line.get(1));
            for (int i = 2; i <= 10; i+=2){
                temp.setParcel(new Parcel(Integer.parseInt(line.get(i)), Integer.parseInt(line.get(i+1))));
            }
            routeGenerators.add(temp);
        }

        scanner.close();

        for(RouteGenerator rg : routeGenerators){
            rg.generateParcelsNumbers(rg.getParcels(),numberOfElementsInRow);
            rg.writeParcels();
            System.out.println(rg.getParcelsNumber());
            floyd_arraylist.startPath ( adjacentMatrixGenerator.getAdjacent () );
            floyd_arraylist.choose_the_shortest ( rg.getParcelsNumber(), floyd_arraylist.floyd ( adjacentMatrixGenerator.getAdjacent () ) );
        }
        floyd_arraylist.hopCounter(1,5);
    }
}
