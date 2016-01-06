package cli;

import static cli.AlgorithmManager.ALGORITHM_FOLDER_PATH;
import food.enums.Diet;
import food.objects.FoodMetaObjectMap;
import food.operations.query.GetFoodByDietQueryOperation;
import food.operations.query.KNNFoodDietQueryOperation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import messif.algorithms.Algorithm;
import messif.algorithms.AlgorithmMethodException;
import messif.objects.AbstractObject;
import messif.operations.AnswerType;
import messif.operations.query.GetAllObjectsQueryOperation;
import messif.operations.query.GetObjectByLocatorOperation;

/**
 * Part of the command-line interface, that deals with the search in a
 * serialized algorithm.
 *
 * @author tomco
 */
public class SearchManager {

    /**
     * Prompts the user to choose from a list of already created algorithms.
     *
     * @throws IOException
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     * @throws GoBackException returns back to the main menu
     */
    public static void manageAlgorithm() throws IOException, AlgorithmMethodException, NoSuchMethodException, GoBackException {
        while (true) {
            List<String> availableAlgorithms = listAvailableAlgorithms();
            int i = 1;
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Available algorithms:");
            for (String algName : availableAlgorithms) {
                //algName w/o .alg suffix
                System.out.println("\t" + i + ". " + algName.split("\\.", 2)[0]);
                i++;
            }
            System.out.println("\t" + i + ". Go Back");
            System.out.println("-------------------------------------------");
            System.out.println("Type in the name of the algorithm you want to boot or type back to go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                int input = scan.nextInt();

                if (input == i) {
                    throw new GoBackException();
                }

                if (input < i && input > 0) {
                    String chosenAlgorithmName = availableAlgorithms.get(input - 1);
                    Algorithm alg
                            = Algorithm.restoreFromFile(ALGORITHM_FOLDER_PATH + "\\" + chosenAlgorithmName);

                    chooseAlgorithmTask(alg, chosenAlgorithmName);
                } else {
                    throw new InputMismatchException();
                }

            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    /**
     * Retrieves a list of all available algorithms located in the default
     * algorithm folder.
     *
     * @return a list of available algorithms
     * @throws IOException
     */
    private static List<String> listAvailableAlgorithms() throws IOException {
        List<String> algorithmNameList = new ArrayList();

        Files.walk(ALGORITHM_FOLDER_PATH).
                forEach((Path filePath) -> {
                    if (Files.isRegularFile(filePath)) {
                        String[] fileName = filePath.getFileName().toString().split("\\.");
                        if (fileName[1].equals("alg")) {
                            algorithmNameList.add(filePath.getFileName().toString());
                        }
                    }
                });

        return Collections.unmodifiableList(algorithmNameList);
    }

    /**
     * Prompts the user to choose what to do with the algorithm. Search:
     * searches the indexed algorithm. List all: lists all food items from the
     * algorithm List by diet: lists food items, that are edible by a certain
     * diet
     *
     * @param alg serialized algorithm
     * @param algorithmName name of the algorithm used to identify it
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     * @throws GoBackException returns to the search menu
     */
    private static void chooseAlgorithmTask(Algorithm alg, String algorithmName) throws AlgorithmMethodException, NoSuchMethodException, GoBackException {
        Iterator iter;

        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Type in the number of a task to do next:");
            System.out.println("\t1. Default similarity search.");
            System.out.println("\t2. Custom similarity search.");
            System.out.println("\t3. List all loaded foods.");
            System.out.println("\t4. List all loaded foods with a filter applied to them.");
            System.out.println("\t5. Go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                int input = scan.nextInt();

                //go to main menu, if part of switch, the GoBackException would 
                //have been caught in the switch, which would cause staying 
                //in the search menu
                if (input == 5) {
                    throw new GoBackException();
                }

                try {
                    switch (input) {
                        case 1:
                            searchDefault(alg, algorithmName);
                            break;
                        case 2:
                            searchCustom(alg, algorithmName);
                            break;
                        case 3:
                            iter = listAllFoods(alg);
                            while (iter.hasNext()) {
                                System.out.println(((AbstractObject) iter.next()).getLocatorURI());
                            }
                            break;
                        case 4:
                            iter = listFoodsFilteredByDiet(alg);

                            while (iter.hasNext()) {
                                System.out.println(((AbstractObject) iter.next()).getLocatorURI());
                            }
                            break;
                        default:
                            throw new InputMismatchException();
                    }
                } catch (GoBackException ex) {
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    /**
     * Evaluates a KNN-search on the algorithm that is filtered by diet. Writes
     * its results on the standard output.
     *
     * @param alg serialized algorithm
     * @param algorithmName name of the algorithm used for printing out
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     * @throws GoBackException returns to the search menu
     */
    private static void searchCustom(Algorithm alg, String algorithmName) throws AlgorithmMethodException, NoSuchMethodException, GoBackException {
        FoodMetaObjectMap query = inputQueryName(alg);

        int numberOfResults = inputResultNumber();

        Diet filter = inputDietFilter();

        Iterator iter = alg.getQueryAnswer(new KNNFoodDietQueryOperation(query, numberOfResults, filter, AnswerType.ORIGINAL_OBJECTS));

        printResult(iter, algorithmName);
        /*
        
         System.out.println();
         System.out.println("-------------------------------------------");
         System.out.println("Results (" + algorithmName + "):");

         while (iter.hasNext()) {
         System.out.println(iter.next());
         }
         */
    }

    private static void searchDefault(Algorithm alg, String algorithmName) throws AlgorithmMethodException, NoSuchMethodException, GoBackException {
        FoodMetaObjectMap query = inputQueryName(alg);

        Iterator iter = alg.getQueryAnswer(new KNNFoodDietQueryOperation(query, 5, Diet.OMNIVOROUS, AnswerType.ORIGINAL_OBJECTS));
        printResult(iter, algorithmName);

    }

    /**
     * Prints results from an iterator on the standard output
     *
     * @param iter iterator pointed at the results
     * @param algorithmName name of the algorithm used for printing out
     */
    private static void printResult(Iterator iter, String algorithmName) {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Results (" + algorithmName + "):");

        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    /**
     * Prompts the user to input the name of the food item, which to run the
     * search on. For the list of all available foods see list foods in the
     * search menu.
     *
     * @param alg serialized algorithm
     * @return food item with the input locator
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     * @throws GoBackException returns to the search menu
     */
    private static FoodMetaObjectMap inputQueryName(Algorithm alg) throws AlgorithmMethodException, NoSuchMethodException, GoBackException {
        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Type in the query food you want to evaluate a similarity search on or type back to go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                String input = scan.nextLine();
                if (input.equals("back")) {
                    throw new GoBackException();
                }
                Iterator iter = alg.getQueryAnswer(new GetObjectByLocatorOperation(input));

                if (!iter.hasNext()) {
                    throw new IllegalArgumentException("There was a problem retrieving " + input + ", please try again.");
                }

                FoodMetaObjectMap query = (FoodMetaObjectMap) iter.next();

                if (iter.hasNext()) {
                    throw new IllegalStateException("The method has gotten to an illegal state, try again.");
                }

                return query;

            } catch (IllegalStateException | IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    /**
     * Prompts the user to enter the number of food items to retrieve by the
     * search.
     *
     * @return number of result food items
     * @throws GoBackException returns to the search menu
     */
    private static int inputResultNumber() throws GoBackException {
        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Type in the number of results you want to retrieve or 0 to go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                int input = scan.nextInt();

                if (input == 0) {
                    throw new GoBackException();
                }

                if (Integer.signum(input) == -1) {
                    throw new InputMismatchException("The number can't be negative, please try again.");
                }

                return input;

            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Prompts the user to choose a diet by which to filter the similarity
     * search.
     *
     * @return filter diet
     * @throws GoBackException returns to the search menu
     */
    private static Diet inputDietFilter() throws GoBackException {
        while (true) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Type in the number of a diet you want to filter by:");
            for (Diet diet : Diet.values()) {
                System.out.println("\t" + diet.toStringWithOrdinal());
            }
            System.out.println("\t" + (Diet.length() + 1) + ". Go back.");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                int input = scan.nextInt();
                Diet filter;

                if (input <= Diet.length()) {
                    filter = Diet.getByOrdinal(input);
                } else {
                    if (input == Diet.length() + 1) {
                        throw new GoBackException();
                    } else {
                        throw new InputMismatchException();
                    }
                }
                return filter;

            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    /**
     * Lists all the food items from the indexing algorithm.
     *
     * @param alg serialized algorithm
     * @return a list of all food items in the algorithm
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     */
    private static Iterator<FoodMetaObjectMap> listAllFoods(Algorithm alg) throws AlgorithmMethodException, NoSuchMethodException {
        return (Iterator<FoodMetaObjectMap>) alg.getQueryAnswer(new GetAllObjectsQueryOperation());
    }

    /**
     * Lists all the food items, that are edible by the filter diet.
     *
     * @param alg serialized algorithm
     * @return a list of filtered food items
     * @throws AlgorithmMethodException
     * @throws NoSuchMethodException
     * @throws GoBackException returns to the search menu
     */
    private static Iterator<FoodMetaObjectMap> listFoodsFilteredByDiet(Algorithm alg) throws AlgorithmMethodException, NoSuchMethodException, GoBackException {
        Diet filter = inputDietFilter();

        return (Iterator<FoodMetaObjectMap>) alg.getQueryAnswer(new GetFoodByDietQueryOperation(filter));
    }
}
