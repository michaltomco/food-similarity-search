package food.objects;

import food.enums.FoodCategory;
import food.enums.Nutrient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import messif.objects.impl.MetaObjectMap;

/**
 * An object that represents a food item and its nutrient vectors.
 * 
 * @author tomco
 */
public abstract class FoodMetaObjectMap extends MetaObjectMap {

    private final static long serialVersionUID = 1L;
    private final int id;
    private final FoodCategory category;

    /**
     * Creates an object from a stream created by CSVParser beforehand, with
     * nutrient vectors represented in a map.
     *
     * @param stream an input stream
     * @throws IOException
     */
    public FoodMetaObjectMap(BufferedReader stream) throws IOException {
        super(stream);
        String line = stream.readLine();
        if (line.startsWith("#id")) {
            id = Integer.parseInt(line.split(" ")[1]);
        } else {
            throw new StreamCorruptedException("There was a problem while reading food id. Data file may be corrupted.");
        }

        line = stream.readLine();
        if (line.startsWith("#category")) {
            category = FoodCategory.valueOf(line.split(" ")[1]);
        } else {
            throw new StreamCorruptedException("There was a problem while reading food id. Data file may be corrupted.");
        }
    }

    /**
     * Retrieves the id used by National Nutrient Database for Standard Reference 
     * for this food item.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the food category assigned to this object.
     * 
     * @return category
     */
    public FoodCategory getCategory() {
        return category;
    }

    @Override
    protected void writeData(OutputStream stream) throws IOException {
        super.writeData(stream);

        PrintStream printStream = new PrintStream(stream);
        printStream.print("#id " + id + "\n");
        printStream.print("#category " + category + "\n");
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();

        build.append(getLocatorURI());
        build.append(", ");
        build.append(getCategory());
        build.append("\n");
        build.append("Macronutrients");
        build.append(" ");
        build.append(Nutrient.childrenToString(Nutrient.MACRONUTRIENT));
        build.append(":\n");
        build.append(getObject("Macronutrients"));
        build.append("\n");
        build.append("Minerals");
        build.append(" ");
        build.append(Nutrient.childrenToString(Nutrient.MINERAL));
        build.append(":\n");
        build.append(getObject("Minerals"));
        build.append("\n");
        build.append("Vitamins");
        build.append(" ");
        build.append(Nutrient.childrenToString(Nutrient.VITAMIN));
        build.append(":\n");
        build.append(getObject("Vitamins"));
        build.append("\n");

        return build.toString();
    }
}
