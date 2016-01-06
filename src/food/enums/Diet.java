package food.enums;

import java.util.EnumSet;
import java.util.Set;
import static food.enums.FoodCategory.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A diet enumeration that is mainly used to filter distance computations that
 * are not desired by the user following a certain diet.
 *
 * @author tomco
 */
public enum Diet {

    /**
     * People that restrict don't restrict their diet.
     */
    OMNIVOROUS(1, EnumSet.allOf(FoodCategory.class)),
    /**
     * People that restrict their diet by eating mostly meat.
     */
    CARNIVOROUS(2, EnumSet.complementOf(EnumSet.of(
            GLUTEN_FREE_CEREAL,
            GLUTEN_CEREAL,
            LEGUME,
            NUT_OR_SEED,
            FRUIT,
            VEGETABLE,
            DAIRY,
            MUSHROOM
    ))),
    /**
     * People that restrict their diet by eating mostly protein and fat rich
     * food.
     */
    KETOGENIC(3, EnumSet.complementOf(EnumSet.of(
            GLUTEN_FREE_CEREAL,
            GLUTEN_CEREAL,
            LEGUME,
            FRUIT
    ))),
    /**
     * People that restrict their diet by not eating meat except fish and
     * seafood.
     */
    PESCETARIAN(4, EnumSet.complementOf(EnumSet.of(
            POULTRY,
            BEEF,
            PORK,
            LAMB,
            GAME
    ))),
    /**
     * People that restrict their diet by not eating meat.
     */
    VEGETARIAN(5, EnumSet.complementOf(EnumSet.of(
            POULTRY,
            FISH,
            SEAFOOD,
            BEEF,
            PORK,
            LAMB,
            GAME
    ))),
    /**
     * People that restrict their diet by not eating animal products.
     */
    VEGAN(6, EnumSet.of(GLUTEN_CEREAL,
            GLUTEN_FREE_CEREAL,
            LEGUME,
            NUT_OR_SEED,
            VEGETABLE,
            FRUIT,
            MUSHROOM,
            UNCATEGORIZED
    )),
    /**
     * People that restrict their diet by eating only fresh food prepared by not
     * heating it above 48 °C.
     */
    VITARIAN(7, EnumSet.of(LEGUME,
            NUT_OR_SEED,
            VEGETABLE,
            FRUIT,
            MUSHROOM,
            UNCATEGORIZED
    )),
    /**
     * People that restrict their diet by eating food that is allowed by Halal
     */
    ISLAMIC(8, EnumSet.complementOf(EnumSet.of(
            PORK
    ))),
    /**
     * People that restrict their diet by following the lighter restrictions of
     * the Hindu dietary law by not eating beef and eating only animals that are
     * killed by Jhatka (fast death).
     */
    HINDU(9, EnumSet.complementOf(EnumSet.of(
            BEEF
    ))),
    /**
     * People that restrict their diet by eating only kosher food and animals
     * killed by a ritual death of slow bleeding.
     */
    JEWISH(10, EnumSet.complementOf(EnumSet.of(
            SEAFOOD,
            PORK,
            GAME
    ))),
    /**
     * People that restrict their diet by eating food that our ancestor's used
     * to eat
     */
    PALEO(11, EnumSet.complementOf(EnumSet.of(
            GLUTEN_FREE_CEREAL,
            GLUTEN_CEREAL,
            LEGUME
    ))),
    /**
     * People that restrict their diet by eating mostly fruit
     */
    FRUITARIAN(12, EnumSet.of(
            GLUTEN_FREE_CEREAL,
            GLUTEN_CEREAL,
            NUT_OR_SEED,
            FRUIT,
            UNCATEGORIZED
    )),
    /**
     * People that restrict their diet by not eating food that contains gluten.
     */
    CELIAC(13, EnumSet.complementOf(EnumSet.of(
            GLUTEN_CEREAL
    )));

    /**
     * A set of food categories that are consumed by a certain diet.
     */
    private final Set<FoodCategory> consumables;

    private final int ordinal;

    /**
     * A statically initialized map field that is used to easily locate a food
     * category by it's ordinal number.
     */
    static final Map<Integer, Diet> dietMap = new HashMap<>();
    static {
        for (Diet diet : Diet.values()) {
            dietMap.put(diet.getOrdinal(), diet);
        }
    }

    private Diet(int ordinal, EnumSet<FoodCategory> consumables) {
        this.ordinal = ordinal;
        this.consumables = consumables;
    }

    /**
     * Retrieves a boolean value that represents the edibility of a food category
     * by a certain diet.
     * 
     * @param cat
     * @return true if edible, else false
     */
    public Boolean isEdible(FoodCategory cat) {
        return consumables.contains(cat);
    }

    /**
     * Retrieves all food categories that this diet can eat.
     * 
     * @return edible food categories
     */
    public Collection<FoodCategory> getConsumables() {
        return Collections.unmodifiableSet(consumables);
    }

    /**
     * Retrieves the ordinal number of a category.
     * 
     * @return ordinal
     */
    public int getOrdinal() {
        return this.ordinal;
    }

    /**
     * Retrieves a food category that is represented by this ordinal number.
     * 
     * @param dietOrdinal a number that represents a food category
     * @return category
     * @throws IllegalArgumentException if there is no such category with this ordinal
     */
    public static Diet getByOrdinal(int dietOrdinal) throws IllegalArgumentException {
        if (dietOrdinal > 0 && dietOrdinal <= Diet.length()) {
            return dietMap.get(dietOrdinal);
        } else {
            throw new IllegalArgumentException("Invalid ordinal number.");
        }
    }

    /**
     * Retrieves the number of food categories currently implemented.
     * 
     * @return number of food categories
     */
    public static int length() {
        return Diet.values().length;
    }

    /**
     * Retrieves a string containing the food category ordinal number and the name
     * of this food category.
     * 
     * @return "(ordinal number). (FoodCategory name)"
     */
    public String toStringWithOrdinal() {
        return ordinal + ". " + name();
    }
}
