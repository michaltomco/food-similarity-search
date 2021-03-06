package food.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import messif.objects.LocalAbstractObject;

/**
 * An object that represents a food with a metric function that calculates the
 * sum of Manhattan distances between micro nutrient vectors.
 *
 * @author tomco
 */
public class FoodMetaObjectMapMicronutrients extends FoodMetaObjectMap {

    /**
     * Creates an object from a stream created by CSVParser beforehand, with
     * vectors initialized by FoodMetaObjectMap.
     *
     * @param stream an input stream
     * @throws IOException
     */
    public FoodMetaObjectMapMicronutrients(BufferedReader stream) throws IOException {
        super(stream);
    }

    /**
     * Retrieves the distance from the object represented by the Manhattan
     * distance between the micro nutrient vectors (vitamins and minerals).
     *
     * @param obj object to which to calculate the distance
     * @param metaDistances precomputed distances
     * @param distThreshold max distance to calculate to
     * @return the Euclidean distance between micro nutrient vectors
     */
    @Override
    protected float getDistanceImpl(LocalAbstractObject obj, float[] metaDistances, float distThreshold) {
        FoodMetaObjectMap object = (FoodMetaObjectMap) obj;
        float dist = 0;
        int i = 0;
        for (String name : Arrays.asList("Minerals", "Vitamins")) {
            LocalAbstractObject oLocal = getObject(name);
            LocalAbstractObject oOther = object.getObject(name);
            if (oLocal != null && oOther != null) {
                float d = oLocal.getDistance(oOther);
                dist += d;
                if (metaDistances != null) {
                    metaDistances[i] = d;
                }
            }
            i++;
        }
        return dist;
    }
}
