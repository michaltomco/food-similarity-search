package cli;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import messif.algorithms.AlgorithmMethodException;
import messif.buckets.CapacityFullException;

/**
 * The main menu of a command-interface of the similarity search.
 * 
 * @author tomco
 */
public class FoodSimilaritySearch {

    public static void main(String[] args) throws IOException, NullPointerException, ClassNotFoundException, AlgorithmMethodException, NoSuchMethodException, InstantiationException, CloneNotSupportedException, InterruptedException, CapacityFullException {
        System.out.println("Food similarity search");

        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Type in a number of a task to do next:");
            System.out.println("-------------------------------------------");
            System.out.println("\t1. Manage nutrient data file");
            System.out.println("\t2. Create a binary algorithm file from a given data");
            System.out.println("\t3. Search in an already created algorithm file");
            System.out.println("\t4. Quit");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                
                int input = scan.nextInt();

                switch (input) {
                    case 1:
                        NutrientDataManager.manageNutrientData();
                        break;
                    case 2:
                        AlgorithmManager.algorithmCreator();
                        break;
                    case 3:
                        SearchManager.manageAlgorithm();
                        break;
                    case 4:
                        return;
                    default:
                        throw new InputMismatchException();
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            } catch (GoBackException ex){}            
        }
    }
}
