package csvparser;

import food.enums.FoodCategory;
import food.enums.Nutrient;
import static food.enums.Nutrient.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import messif.objects.AbstractObject;
import messif.objects.impl.ObjectFloatVectorL1;

/**
 * A static class that transforms .csv files into a file trxt parsable by
 * FoodSimilaritySearch.
 *
 * @author tomco
 */
public class CSVParser {

    private final static Logger logger = Logger.getLogger(CSVParser.class.getName());

    //CONSTANTS    
    public static final Path DEFAULT_CSV_FILES_PATH = Paths.get("csv");
    public static final Path DEFAULT_DATA_FILE_PATH = Paths.get("FoodDataFile.data");
    public static final OpenOption DEFAULT_INPUT_OPTION = StandardOpenOption.APPEND;

    /**
     * csv folder/file that is to be added to the data file
     */
    private final Path input;
    /**
     * data file that holds all the food items
     */
    private final Path output;
    /**
     * option between keeping the previous data in the data file or rewriting it
     */
    private final OpenOption openOption;
    /**
     * National Nutrient Database for Standard Reference id's of food items
     * already present in the data file
     */
    private final Set<String> presentIds;

    /**
     * Sets all fields to their default values.
     *
     * @throws IOException
     */
    public CSVParser() throws IOException {

        this.input = DEFAULT_CSV_FILES_PATH;
        if (input == null) {
            throw new NullPointerException("No input file specified.");
        }

        this.output = DEFAULT_DATA_FILE_PATH;
        if (!Files.exists(output)) {
            Files.createFile(output);
        }
        this.openOption = DEFAULT_INPUT_OPTION;

        this.presentIds = getAlreadyPresentIds(output);
    }

    /**
     * Sets values to custom values.
     *
     * @param input folder with csv files
     * @param output test file where food objects are to be stored
     * @param openOption option to open the data file (keep the previous text or
     * truncate it)
     * @throws IOException
     */
    public CSVParser(Path input, Path output, StandardOpenOption openOption) throws IOException {
        if (input == null) {
            throw new NullPointerException("No input file specified.");
        }
        this.input = input;

        if (!Files.exists(output)) {
            Files.createFile(output);
        }
        this.output = output;

        this.openOption = openOption;

        this.presentIds = getAlreadyPresentIds(output);
    }

    /**
     * A static method that walks the input path and adds all .csv files it
     * encounters. If the .csv file has not been renamed yet from download.csv,
     * it then renames it to foodName_foodCategoryNumber.csv
     *
     * @throws IOException
     */
    public void addFile() throws IOException {
        try {
            Files.walk(input).forEach((Path filePath) -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        addCSV(filePath);
                    } catch (IOException ex) {
                        Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds a single .csv file to the data file. Furthermore, renames the .csv
     * file to a foodName_foodCategoryOrdinal.csv, if not done already. The file
     * is renamed for convenience, so that the user is not always prompted to
     * input the food's human-friendly name and food category in the future.
     *
     * @param inputPath path of the .csv file
     * @throws IOException
     */
    private void addCSV(Path filePath) throws IOException, FileNotFoundException {

        StringBuilder outputStringBuilder = new StringBuilder();
        String foodName = "";
        FoodCategory foodCategory = FoodCategory.UNCATEGORIZED;
        String foodId = "";
        String nutrients = "";

        String[] fileName = filePath.getFileName().toString().split("\\.");

        if (!fileName[1].equals("csv")) {
            System.out.println(filePath.getFileName() + " is not a .csv file.");
            return;
        }

        //reads .csv file
        try (BufferedReader inputReader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line = inputReader.readLine();
            if (!line.equals("Source: USDA National Nutrient Database for Standard Reference 28 Software v.2.3.2")) {
                throw new IOException("Illegal input file format. Use USDA National nutrient database .csv file.");
            }

            //reads lines until it gets \"Nutrient data for: key, name\"
            while (!line.startsWith("\"")) {
                line = inputReader.readLine();
            }

            //line:\"Nutrient data for: key, name\"
            String[] splitValues = line.split("\"");
            //splitValues:"", "Nutrient data for: key, name(may contain ,)", ""
            splitValues = splitValues[1].split(":");
            //splitValues:"Nutrient data for", " key, name(may contain ,)"
            splitValues = splitValues[1].split(",", 2);
            //splitValues:" key", " name"

            foodId = splitValues[0].trim();
            foodName = splitValues[1].trim();

            if (presentIds.contains(foodId)) {
                System.out.print("The data file already contains food " + foodId + ":" + foodName + ".\n");
                return;
            }
            nutrients = parseNutrients(inputReader);
        }

        //if the csv file name is in format foodName_foodCategoryNumber(1-17)
        if (Pattern.matches(".*_(([1-9])|([1][0-7]))$", fileName[0])) {
            String[] nameAndCategoryOrdinal = fileName[0].split("_");
            foodName = nameAndCategoryOrdinal[0];
            foodCategory = FoodCategory.getByOrdinal(Integer.parseInt(nameAndCategoryOrdinal[1]));
        } else {
            //get name and category from the user
            foodName = inputName(foodName);
            foodCategory = inputCategory(foodName);
            //rename the file to format foodName_foodCategoryNumber(1-17) for future reference
            renameCSVFile(filePath, foodName, foodCategory);
        }

        //prebuilds the string to be written to the .data file
        outputStringBuilder.append("#objectKey messif.objects.keys.AbstractObjectKey ").append(foodName).append("\n");
        outputStringBuilder.append(getNutrientNamesAndClasses(ObjectFloatVectorL1.class));
        outputStringBuilder.append(nutrients);
        outputStringBuilder.append("#id ").append(foodId).append("\n");
        outputStringBuilder.append("#category ").append(foodCategory).append("\n");

        //writes into the .data file
        try (BufferedWriter outputFile = Files.newBufferedWriter(output, openOption)) {
            outputFile.write(outputStringBuilder.toString());
            presentIds.add(foodId);
            System.out.println(foodName + " added to " + output.getFileName() + ".");
        }
    }

    /**
     * If file is not renamed, prompt the user to insert a user-friendlier name
     * and rename the file.
     *
     * @param formerName name assigned by the nutriDB, usually too descriptive
     * @return food user defined name
     */
    private String inputName(String formerName) {
        //input new name
        System.out.println("About to add " + formerName + " to the file.");
        System.out.println("Insert a human friendlier name:");

        Scanner scanName = new Scanner(System.in);
        String newName = scanName.nextLine();

        return newName;
    }

    /**
     * Prompts the user to define the food category of this food.
     *
     * @param newName name of the food
     * @return food's category
     */
    private FoodCategory inputCategory(String newName) {
        FoodCategory result = FoodCategory.UNCATEGORIZED;

        while (Boolean.TRUE) {
            System.out.println("Please type in the category number for " + newName + ":");
            for (FoodCategory c : FoodCategory.values()) {
                System.out.println(c.toStringWithOrdinal());
            }
            try {
                Scanner scanCategoryOrdinal = new Scanner(System.in);
                int categoryOrdinal = scanCategoryOrdinal.nextInt();
                result = FoodCategory.getByOrdinal(categoryOrdinal);
                return result;

            } catch (InputMismatchException | IllegalArgumentException ex) {
                System.out.println("Invalid input, try again.");
            }
        }
        return result;
    }

    /**
     * Reads nutrients line by line form the csv and writes the value contained
     * in 100 grams of the food item. Returns a string consisted of nutrient
     * vectors separated by new lines and each value separated by a ','.
     * Nutrient info is categorized into vectors according to the Nutrient.java
     * enum file.
     *
     *
     * @param inputReader .csv file
     * @return string of vectors separated by newline
     * @throws IOException
     */
    private String parseNutrients(BufferedReader inputReader) throws IOException {

        Map<Nutrient, Double> nutrients = new EnumMap<>(Nutrient.class);

        String line = inputReader.readLine();

        while (line != null) {
            if (line.startsWith("\"")) {
                //line:"Nutrient name",[unit],[in 100g],[in volume2],[in volume3]...
                String[] splitLine = line.split("\"");

                //splitLine[2]:,[unit],[in 100g],[in volume2],[in volume3]...
                String[] nutrientInDifferentVolumes = splitLine[2].split(",");

                Nutrient foundNutrient = null;

                //splitLine[1]:Nutrient name
                switch (splitLine[1]) {
                    case "Water":
                        foundNutrient = WATER;
                        break;
                    case "Energy":
                        //not a measured value
                        break;
                    case "Protein":
                        foundNutrient = PROTEIN;
                        break;
                    case "Total lipid (fat)":
                        foundNutrient = LIPIDS;
                        break;
                    case "Carbohydrate, by difference":
                        foundNutrient = CARBOHYDRATE;
                        break;
                    case "Fiber, total dietary":
                        foundNutrient = FIBER;
                        break;
                    case "Sugars, total":
                        //not a measured value
                        break;
                    case "Calcium, Ca":
                        foundNutrient = CALCIUM;
                        break;
                    case "Iron, Fe":
                        foundNutrient = IRON;
                        break;
                    case "Magnesium, Mg":
                        foundNutrient = MAGNESIUM;
                        break;
                    case "Phosphorus, P":
                        foundNutrient = PHOSPHORUS;
                        break;
                    case "Potassium, K":
                        foundNutrient = POTASSIUM;
                        break;
                    case "Sodium, Na":
                        foundNutrient = SODIUM;
                        break;
                    case "Zinc, Zn":
                        foundNutrient = ZINC;
                        break;
                    case "Vitamin C, total ascorbic acid":
                        foundNutrient = VITAMIN_C;
                        break;
                    case "Thiamin":
                        foundNutrient = VITAMIN_B1;
                        break;
                    case "Riboflavin":
                        foundNutrient = VITAMIN_B2;
                        break;
                    case "Niacin":
                        foundNutrient = VITAMIN_B3;
                        break;
                    case "Vitamin B-6":
                        foundNutrient = VITAMIN_B6;
                        break;
                    case "Folate, DFE":
                        foundNutrient = VITAMIN_B9;
                        break;
                    case "Vitamin B-12":
                        foundNutrient = VITAMIN_B12;
                        break;
                    case "Vitamin A, RAE":
                        //using only IU value of vitamin A
                        break;
                    case "Vitamin A, IU":
                        foundNutrient = VITAMIN_A;
                        break;
                    case "Vitamin E (alpha-tocopherol)":
                        foundNutrient = VITAMIN_E;
                        break;
                    case "Vitamin D (D2 + D3)":
                        //using only IU value of vitamin D
                        break;
                    case "Vitamin D":
                        foundNutrient = VITAMIN_D;
                        break;
                    case "Vitamin K (phylloquinone)":
                        foundNutrient = VITAMIN_K;
                        break;
                    case "Fatty acids, total saturated":
                        //not a measured value
                        break;
                    case "Fatty acids, total monounsaturated":
                        //not a measured value
                        break;
                    case "Fatty acids, total polyunsaturated":
                        //not a measured value
                        break;
                    case "Fatty acids, total trans":
                        //not a measured value
                        break;
                    case "Cholesterol":
                        //not a measured value
                        break;
                    case "Caffeine":
                        //not a measured value
                        break;
                    default:
                        logger.log(Level.WARNING, "The line is of a different format.", line);
                        throw new IOException(line + " not a known string in this format.");
                }
                if (foundNutrient != null) {
                    double value = Double.parseDouble(nutrientInDifferentVolumes[2]) / foundNutrient.getRDI();
                    value *= 100;
                    value = Math.round(value * 1000.0d) / 1000.0d;

                    nutrients.put(foundNutrient, value);
                }
            }
            line = inputReader.readLine();
        }

        StringBuilder nutrientVectors = new StringBuilder();

        //create a string categorized by the NUTRIENT enum into lines of nutrient vector values
        for (Nutrient nutrientType : NUTRIENT.getChildren()) {
            if (nutrientType.hasChildren()) {
                for (Nutrient nutrient : nutrientType.getChildren()) {
                    nutrientVectors.append(nutrients.getOrDefault(nutrient, 0.0));
                    nutrientVectors.append(',');
                }
                //changes the last ',' character from the nutrient vector line
                //into a \n character to create 
                nutrientVectors.setCharAt(nutrientVectors.length() - 1, '\n');
            } else {
                //if the NUTRIENT enum is not further categorized
                nutrientVectors.append(nutrients.getOrDefault(nutrientType, 0.0));
                nutrientVectors.append("\n");
            }
        }

        return nutrientVectors.toString();
    }

    /**
     * Renames a .csv file that has yet had it's name renamed from download.csv
     * or download (number).csv to foodName_foodCategoryNumber.csv
     *
     * @param inputPath
     * @param name
     * @param categoryName
     * @return
     */
    private void renameCSVFile(Path inputPath, String name, FoodCategory category) {
        String newName = name + "_" + category.getOrdinal() + ".csv";
        String newNameWithPath = inputPath.getParent() + "/" + newName;
        if (inputPath.toFile().renameTo(new File(newNameWithPath))) {
            System.out.println("Renamed the .csv file to " + newName + ".");
        } else {
            System.out.println("There was a problem renaming the file.");
        }
    }

    /**
     * Reads the input file and retrieves a map of food keys already contained
     * in the food file in order to prevent the creation of duplicates.
     *
     * @return map of locator keys and names of foods already in the data file
     */
    private Set<String> getAlreadyPresentIds(Path output) {

        Set<String> result = new HashSet<>();

        try (BufferedReader inputReader = Files.newBufferedReader(output)) {

            String line = inputReader.readLine();
            while (line != null) {
                //e.g. #id 123
                if (line.startsWith("#id")) {
                    String key = line.split(" ")[1];
                    result.add(key);
                }
                line = inputReader.readLine();
            }

        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Retrieves a string, that represents nutrient vector names and classes
     * separated by a ';'.
     *
     * @param clazz nutrient vector class
     * @return "(nutrient name);(nutrient vector class);"
     */
    private String getNutrientNamesAndClasses(Class<? extends AbstractObject> clazz) {
        return "Macronutrients;"
                + clazz.getName()
                + ";Minerals;"
                + clazz.getName()
                + ";Vitamins;"
                + clazz.getName()
                + ";\n";
    }
}
