package food.operations.query;

import food.enums.Diet;
import food.objects.FoodMetaObjectMap;
import messif.objects.LocalAbstractObject;
import messif.objects.util.AbstractObjectIterator;
import messif.objects.util.RankedSortedCollection;
import messif.operations.AbstractOperation;
import messif.operations.AnswerType;
import messif.operations.query.KNNQueryOperation;

/**
 * A k-nearest-neighbors operation that takes in account the diet that the user 
 * specifies in the search.
 * 
 * @author tomco
 */
public class KNNFoodDietQueryOperation extends KNNQueryOperation {
    /** Diet with which the operation filters it's search. */
    private final Diet diet;

    @AbstractOperation.OperationConstructor({"Query object", "Number of nearest objects", "Diet"})
    public KNNFoodDietQueryOperation(LocalAbstractObject queryObject, int k, Diet diet) {
        super(queryObject, k);
        this.diet = diet;
    }

    @AbstractOperation.OperationConstructor({"Query object", "Number of nearest objects", "Diet", "Answer type"})
    public KNNFoodDietQueryOperation(LocalAbstractObject queryObject, int k, Diet diet, AnswerType answerType) {
        super(queryObject, k, answerType);
        this.diet = diet;
    }

    @AbstractOperation.OperationConstructor({"Query object", "Number of nearest objects", "Diet", "Store the meta-object subdistances?", "Answer type"})
    public KNNFoodDietQueryOperation(LocalAbstractObject queryObject, int k, Diet diet, boolean storeMetaDistances, AnswerType answerType) {
        super(queryObject, k, storeMetaDistances, answerType);
        this.diet = diet;
    }

    @AbstractOperation.OperationConstructor({"Query object", "Number of nearest objects", "Diet", "Store the meta-object subdistances?", "Answer type", "Answer collection"})
    public KNNFoodDietQueryOperation(LocalAbstractObject queryObject, int k, Diet diet, boolean storeMetaDistances, AnswerType answerType, RankedSortedCollection answerCollection) {
        super(queryObject, k, storeMetaDistances, answerType, answerCollection);
        this.diet = diet;
    }

    /**
     * Retrieves the diet filter of this operation.
     * 
     * @return 
     */
    public Diet getDiet() {
        return diet;
    }

    @Override
    public int dataHashCode() {
        return (super.dataHashCode() << 8) + diet.hashCode();
    }

    @Override
    protected boolean dataEqualsImpl(AbstractOperation obj) {
        if (!(obj instanceof KNNFoodDietQueryOperation)) {
            return false;
        }

        KNNQueryOperation castObj = (KNNQueryOperation) obj;

        if (!super.dataEquals(castObj)) {
            return false;
        }

        return diet.equals(castObj.getArgument(2));
    }

    @Override
    public int evaluate(AbstractObjectIterator<? extends LocalAbstractObject> objects) {
        int beforeCount = getAnswerCount();

        while (objects.hasNext()) {
            FoodMetaObjectMap object = (FoodMetaObjectMap)objects.next();
            
            //don't compute if not edible for the user
            if(!diet.isEdible(object.getCategory())){
                continue;
            }

            if (getQueryObject().excludeUsingPrecompDist(object, getAnswerThreshold())) {
                continue;
            }

            addToAnswer(object, getAnswerThreshold());
        }

        return getAnswerCount() - beforeCount;
    }

    @Override
    public int getArgumentCount() {
        return 3;
    }

    @Override
    public Object getArgument(int index) throws IndexOutOfBoundsException {
        switch (index) {
            case 0:
            case 1:
                return super.getArgument(index);
            case 2:
                return diet;
            default:
                throw new IndexOutOfBoundsException("kNNFoodDietQueryOperation has only three arguments");
        }
    }
}
