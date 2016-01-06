package cli;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;
import messif.algorithms.Algorithm;
import messif.algorithms.AlgorithmMethodException;
import messif.algorithms.impl.SequentialScan;
import messif.buckets.CapacityFullException;
import messif.objects.util.StreamGenericAbstractObjectIterator;
import messif.operations.data.InsertOperation;
import csvparser.CSVParser;
import food.objects.*;

/**
 * Part of the command-line interface, that deals with the serialization of
 * algorithms. Algorithm needs to be serializable, support GetObjectByLocator,
 * GetAllObjectsQuery, KNNQueryOperation and InsertOperation.
 *
 *
 * @author tomco
 */
public class AlgorithmManager {

    /**
     * a constant, that represents the path to the folder that holds serialized
     * algorithms
     */
    public static final Path ALGORITHM_FOLDER_PATH = Paths.get("serializedAlgorithms");

    /**
     * Creates the serialized algorithm file from the the standard input.
     *
     * @throws CapacityFullException
     * @throws InstantiationException
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws cli.GoBackException
     */
    public static void algorithmCreator() throws CapacityFullException, InstantiationException, AlgorithmMethodException, NoSuchMethodException, IOException, GoBackException {
        Class algorithmType = inputAlgorithmType();

        String name = inputAlgorithmName();

        Path dataFilePath = inputDataFilePath();

        Class objectClass = inputObjectClass();

        Algorithm algorithm;

        if (algorithmType.equals(SequentialScan.class)) {
            algorithm = new SequentialScan();
        } else {
            throw new IllegalStateException("Algorithm not yet supported.");
        }

        BufferedReader reader = new BufferedReader(Files.newBufferedReader(dataFilePath));

        StreamGenericAbstractObjectIterator iter
                = new StreamGenericAbstractObjectIterator<>(objectClass, reader);

        while (iter.hasNext()) {
            InsertOperation insert = new InsertOperation(iter.next());
            algorithm.executeOperation(insert);
        }

        algorithm.storeToFile(ALGORITHM_FOLDER_PATH + "\\" + name);
    }

    /**
     * Retrieves the path to the data file that is used as an input for
     * serialization. User is prompted to choose whether to use the default data
     * file path or whether he wants to use his own custom file.
     *
     * @return path to the data file
     */
    private static Path inputDataFilePath() throws GoBackException {
        Path dataFilePath;

        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Input a type of an input file to use to create the algorithm");
            System.out.println("-------------------------------------------");
            System.out.println("\t1. Default data file");
            System.out.println("\t2. Custom data file");
            System.out.println("\t3. Go back");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                int input = scan.nextInt();

                switch (input) {
                    case 1:
                        dataFilePath = CSVParser.DEFAULT_DATA_FILE_PATH;
                        break;
                    case 2:
                        dataFilePath = inputCustomDataFilePath();
                        if (dataFilePath == null) {
                            return null;
                        }
                        break;
                    case 3:
                        throw new GoBackException();
                    default:
                        throw new InputMismatchException();
                }

                return dataFilePath;

            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    /**
     * Prompts the user to input the path to a custom food data file.
     *
     * @return path to the food data file
     */
    private static Path inputCustomDataFilePath() throws GoBackException {
        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Input a path to a custom data file or type back to go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                String input = scan.nextLine();
                if (input.equals("back")) {
                    throw new GoBackException();
                }
                if (!Files.exists(Paths.get(input))) {
                    throw new FileNotFoundException("Input file does not exist, please enter the path again.");
                }

                return Paths.get(input);

            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    /**
     * Prompts the user to enter the type of the algorithm.
     *
     * @return class of the desired algorithm
     * @throws GoBackException returns to the main menu
     */
    private static Class<? extends Algorithm> inputAlgorithmType() throws GoBackException {
        return SequentialScan.class;
    }

    /**
     * Prompts the user to input the type of objects, that hold the metric by
     * which the algorithm will calculate the distance between food items.
     *
     * @return class of a food object
     * @throws GoBackException returns to the main menu
     */
    private static Class inputObjectClass() throws GoBackException {
        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Type in the number of the function you want to base this algorithm on:");
            System.out.println("-------------------------------------------");
            System.out.println("\t1. All nutrients");
            System.out.println("\t2. Micronutrients");
            System.out.println("\t3. Macronutrients");
            System.out.println("\t4. Minerals");
            System.out.println("\t5. Vitamins");
            System.out.println("\t6. Macronutrients limited to the same food category");            
            System.out.println("\t7. Go back");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                int input = scan.nextInt();

                switch (input) {
                    case 1:
                        return FoodMetaObjectMapAllNutrients.class;
                    case 2:
                        return FoodMetaObjectMapMicronutrients.class;
                    case 3:
                        return FoodMetaObjectMapMacronutrients.class;
                    case 4:
                        return FoodMetaObjectMapMinerals.class;
                    case 5:
                        return FoodMetaObjectMapVitamins.class;
                    case 6:
                        return FoodMetaObjectMapMacronutrientsWithCategory.class;
                    case 7:
                        throw new GoBackException();
                    default:
                        throw new InputMismatchException();
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    /**
     * Prompts the user to input the name of the algorithm that is to be
     * created.
     *
     * @return name of the algorithm, to which the .alg suffix is appended
     * @throws GoBackException returns back to the main menu
     */
    private static String inputAlgorithmName() throws GoBackException {
        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Input a name for the algorithm (.alg appended automatically) or type back to go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                String input = scan.nextLine();
                if (input.equals("")) {
                    System.out.println("The name you have entered is invalid, try again.");
                    throw new IllegalArgumentException("Invalid file name.");
                }

                if (input.equals("back")) {
                    throw new GoBackException();
                }

                return input + ".alg";

            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
