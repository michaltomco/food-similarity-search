package food.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates the food categories that are mainly used to filter by diets.
 * 
 * @author tomco
 */
public enum FoodCategory {
    GLUTEN_CEREAL(1),
    GLUTEN_FREE_CEREAL(2),
    LEGUME(3),
    NUT_OR_SEED(4),
    VEGETABLE(5),
    FRUIT(6),
    POULTRY(7),
    FISH(8),
    SEAFOOD(9),
    BEEF(10),
    PORK(11),
    LAMB(12),
    GAME(13),
    EGG(14),
    DAIRY(15),
    MUSHROOM(16),
    UNCATEGORIZED(17);

    private final int ordinal;

    /**
     * A statically initialized map field that is used to easily locate a 
     * food category by it's ordinal number.
     */
    static final Map<Integer, FoodCategory> categoryMap = new HashMap<>();
    static {
        for (FoodCategory category : FoodCategory.values()) {
            categoryMap.put(category.getOrdinal(), category);
        }
    }

    private FoodCategory(int ordinal) {
        this.ordinal = ordinal;
    }

    /**
     * Retrieves the ordinal value of this category.
     * 
     * @return 
     */
    public int getOrdinal() {
        return this.ordinal;
    }

    /**
     * Retrieves a food category represented by a category ordinal.
     * 
     * @param categoryOrdinal
     * @return a food category, that is represented by this ordinal
     * @throws IllegalArgumentException if the number does not represent a food category
     */
    public static FoodCategory getByOrdinal(int categoryOrdinal) throws IllegalArgumentException{
        if (categoryOrdinal > 0 && categoryOrdinal <= FoodCategory.length()) {
            return categoryMap.get(categoryOrdinal);
        } else {
            throw new IllegalArgumentException("Invalid ordinal number.");
        }
    }

    /**
     * Retrieves the number of food categories.
     * 
     * @return food category number
     */
    public static int length() {
        return FoodCategory.values().length;
    }

    /**
     * Retrieves a string, that used to list categories with their ordinal numbers
     * 
     * @return "(ordinal number)\tab(category name)"
     */
    public String toStringWithOrdinal() {
        return ordinal + "\t" + this.name();
    }    
}
