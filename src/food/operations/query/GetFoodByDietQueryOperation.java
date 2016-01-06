package food.operations.query;

import food.enums.Diet;
import food.objects.FoodMetaObjectMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import messif.objects.LocalAbstractObject;
import messif.objects.util.AbstractObjectIterator;
import messif.operations.AbstractOperation;
import messif.operations.AnswerType;
import messif.operations.ListingQueryOperation;
import messif.operations.query.GetAllObjectsQueryOperation;

/**
 * A query operation that retrieves all the food items from an indexing algorithm,
 * that are consumable by a certain diet.
 * 
 * @author tomco
 */
public class GetFoodByDietQueryOperation extends ListingQueryOperation {

    /**
     * A diet to be filtered by.
     */
    private final Diet diet;

    /**
     * Creates a query operation with a given diet filter.
     * 
     * @param diet filter diet
     */
    public GetFoodByDietQueryOperation(Diet diet) {
        this.diet = diet;
    }

    /**
     * Creates a query operation with a given diet filter and an answerType.
     * 
     * @param diet filter diet
     * @param answerType type of objects to be retrieved from an indexing algorithm
     */
    public GetFoodByDietQueryOperation(Diet diet, AnswerType answerType) {
        super(answerType);
        this.diet = diet;
    }

    /**
     * Retrieve the diet filter.
     * 
     * @return diet filter
     */
    public Diet getDiet() {
        return diet;
    }

    @Override
    public Object getArgument(int index) throws IndexOutOfBoundsException {
        switch (index) {
            case 0:
                return diet;
            default:
                throw new IndexOutOfBoundsException("GetFoodByDietQueryOperation has only one argument");
        }
    }

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public int evaluate(AbstractObjectIterator<? extends LocalAbstractObject> objects) {
        GetAllObjectsQueryOperation all = new GetAllObjectsQueryOperation(AnswerType.ORIGINAL_OBJECTS);

        all.evaluate(objects);
        Iterator iter = all.getAnswer();
        // Iterate through all supplied objects
        try {
            while (iter.hasNext()) {
                FoodMetaObjectMap obj = (FoodMetaObjectMap) iter.next();
                if (diet.isEdible(obj.getCategory())) {
                    addToAnswer(obj);
                }
            }
            return 1;
        } catch (NoSuchElementException e) {
            // If there was no object with the specified locator
            return 0;
        }
    }

    @Override
    protected boolean dataEqualsImpl(AbstractOperation operation) {
        if (!(operation instanceof GetFoodByDietQueryOperation)) {
            return false;
        }
        GetFoodByDietQueryOperation newOp = (GetFoodByDietQueryOperation) operation;

        return diet.equals(newOp.getDiet());
    }

    @Override
    public int dataHashCode() {
        return diet.hashCode();
    }

}
