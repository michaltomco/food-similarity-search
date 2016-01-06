package cli;

import csvparser.CSVParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Part of the command-line interface, that  deals with the parsing of csv files
 * to data text files.
 * 
 * @author tomco
 */
public class NutrientDataManager {

    /**
     * Prompts the user to choose whether to use the default input and output files
     * or whether he wants to input custom ones.
     * 
     * @throws GoBackException returns to the main menu
     */
    public static void manageNutrientData() throws GoBackException {
        while (true) {
            int intInput;

            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Manage nutrient data file");
            System.out.println("-------------------------------------------");
            System.out.println("Type in the number of an option you want to do next:");
            System.out.println("\t1. Parse default input folder (" + CSVParser.DEFAULT_CSV_FILES_PATH
                    + ") to the default output file (" + CSVParser.DEFAULT_DATA_FILE_PATH + ")");
            System.out.println("\t2. Parse from and into custom files");
            System.out.println("\t3. Go back");
            System.out.println("-------------------------------------------");

            try {
                Scanner scan = new Scanner(System.in);
                intInput = scan.nextInt();

                CSVParser parser;

                switch (intInput) {
                    case 1:
                        parser = new CSVParser();
                        break;
                    case 2:
                        parser = getCustomNutrientParser();
                        break;
                    case 3:
                        throw new GoBackException();
                    default:
                        throw new InputMismatchException();
                }

                parser.addFile();

            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Prompts the user to enter paths to input and output files, as well as 
     * the open option by which to open the output file.
     * 
     * @return a new CSVParser with custom input, output and openOption
     * @throws IOException
     * @throws GoBackException returns to the main menu
     */
    private static CSVParser getCustomNutrientParser() throws IOException, GoBackException {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Managing a custom data file.");

        Path inputPath = manageCustomNutrientCSVFile();

        Path outputPath = manageCustomNutrientDataFile();

        StandardOpenOption option = manageCustomNutrientDataOpenOption();

        return new CSVParser(inputPath, outputPath, option);
    }

    /**
     * Prompts the user to enter the custom file, where the .csv food items are located.
     * 
     * @return path to a csv folder/file
     * @throws GoBackException returns to the main menu
     */
    private static Path manageCustomNutrientCSVFile() throws GoBackException {
        while (true) {
            String input = "";
            System.out.println("-------------------------------------------");
            System.out.println("Insert an input folder path from which to read .csv files"
                    + " or type 'back' to go back.");

            try {
                Scanner scan = new Scanner(System.in);
                input = scan.nextLine();
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
     * Prompts the user to enter the path to a custom data file to which write the
     * food items data to.
     * 
     * @returnpath to a data file
     * @throws GoBackException returns to the main menu
     */
    private static Path manageCustomNutrientDataFile() throws GoBackException {
        while (true) {
            String input = "";
            System.out.println("-------------------------------------------");
            System.out.println("Insert an output data file path to which to write the data"
                    + " or type 'back' to go back.");

            Scanner scan = new Scanner(System.in);
            input = scan.nextLine();
            if (input.equals("back")) {
                throw new GoBackException();
            }

            return Paths.get(input);

        }
    }

    /**
     * Prompts the user to enter the open option by which to open the data file.
     * 
     * @return open option
     * @throws GoBackException returns to the main menu
     */
    private static StandardOpenOption manageCustomNutrientDataOpenOption() throws GoBackException {
        while (true) {
            int input;
            System.out.println("-------------------------------------------");
            System.out.println("Type in a number to choose whether:");
            System.out.println("\t1. Append .csv files to the output data file");
            System.out.println("\t2. Truncate the existing output data file");
            System.out.println("\t3. Go back");

            try {
                Scanner scan = new Scanner(System.in);
                input = scan.nextInt();
                switch (input) {
                    case 1:
                        return StandardOpenOption.APPEND;
                    case 2:
                        return StandardOpenOption.TRUNCATE_EXISTING;
                    case 3:
                        throw new GoBackException();
                    default:
                        throw new InputMismatchException();
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Invalid input, try again.");
            }
        }
    }
}
