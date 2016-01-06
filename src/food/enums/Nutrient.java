package food.enums;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Nutrient enum hierarchy measured by the USDA National Nutrient Database 
 * for Standard Reference that can practically retrieve nutrient categories.
 * Single nutrients in nutrient group hierarchy.
 * 
 * @author tomco
 */
public enum Nutrient {

    NUTRIENT(null,0),    
        /** Nutrients that provide mainly energy for the body. */
        MACRONUTRIENT(NUTRIENT, 0),
            /** Water measured in grams. */
            WATER(MACRONUTRIENT,3700),
            /** Protein measured in grams. */
            PROTEIN(MACRONUTRIENT,56),
            /** Lipids measured in grams. */
            LIPIDS(MACRONUTRIENT,65),
            /** Carbohydrates measured in grams. */
            CARBOHYDRATE(MACRONUTRIENT,130),
            /** Fiber measured in grams. */
            FIBER(MACRONUTRIENT,38),
        /** Inorganic nutrients that mainly support various bodily functions */
        MINERAL(NUTRIENT,0),
            /** Calcium measured in miligrams. */
            CALCIUM(MINERAL,800),
            /** Iron measured in miligrams. */
            IRON(MINERAL,14),
            /** Magnesium measured in miligrams. */
            MAGNESIUM(MINERAL,400),
            /** Phosphorus measured in miligrams. */
            PHOSPHORUS(MINERAL,700),
            /** Potassium measured in miligrams. */
            POTASSIUM(MINERAL,2000),
            /** Sodium measured in miligrams. */
            SODIUM(MINERAL,1500),
            /** Zinc measured in miigrams. */
            ZINC(MINERAL,10),
        /** Organic nutrients that mainly support various bodily functions */
        VITAMIN(NUTRIENT,0),
            /** Vitamin C or L-ascorbic acid measured in miligrams. */
            VITAMIN_C(VITAMIN,80),
            /** Vitamin B1 or thiamine measured in miligrams. */
            VITAMIN_B1(VITAMIN,1.1),
            /** Vitamin B2 or riboflavin measured in miligrams. */
            VITAMIN_B2(VITAMIN,1.4),
            /** Vitamin B3 or niacin measured in miligrams. */
            VITAMIN_B3(VITAMIN,16),
            /** Vitamin B6 or pyridoxine measured in miligrams. */
            VITAMIN_B6(VITAMIN,1.4),
            /** Vitamin B9 or folic acid measured in micrograms. */
            VITAMIN_B9(VITAMIN,200),
            /** Vitamin B12 or cyanocobalamin measured in micrograms. */
            VITAMIN_B12(VITAMIN,2.5),
            /**
            * Vitamin A or beta-carotene and palmitate measured in 
            * international units.
            */
            VITAMIN_A(VITAMIN,3000),
            /** Vitamin E or D-alpha tocopheryl succinate meausred in miligrams. */
            VITAMIN_E(VITAMIN,12),
            /** Vitamin D or cholecalciferol measured in international units. */
            VITAMIN_D(VITAMIN,600),
            /** Vitamin K or phytonadione measured in micrograms. */
            VITAMIN_K(VITAMIN,75);

    /** Parent category of a nutrient. */
    private final Nutrient parent;
    /** Recommended daily intake */
    private final double rdi;
    /** The list of children of a nutrient category. */
    private final List<Nutrient> children = new ArrayList<>();

    /**
     * Creates an enum hierarchy.
     * 
     * @param parent nutrient's parent category
     */
    private Nutrient(Nutrient parent, double rdi) {
        this.parent = parent;
        this.rdi = rdi;

        if (this.parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * Adds a child of a category to it's children list.
     * 
     * @param nutrient child
     */
    public void addChild(Nutrient nutrient) {
        if (nutrient != null) {
            this.children.add(nutrient);
        }
    }
    
    /**
     * Finds out whether the nutrient is a parent to other nutrients.
     * 
     * @return true if a parent, false otherwise
     */
    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }
    
    /**
     * Retrieves all children of a nutrient category.
     * 
     * @return nutrient category children
     */
    public Collection<Nutrient> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    /**
     * Retrieves the recommended daily intake of the nutrient or 0 if nutrient category.
     * 
     * @return rdi
     */
    public double getRDI() {
        return rdi;
    }
    
    /**
     * Retrieves a string of nutrients in NutrientType
     * 
     * @param group
     * @return format: (1,2,3,...)
     */
    public static String childrenToString(Nutrient group){
        if(!group.hasChildren()) {
            throw new NullPointerException("NutrientType is not a nutrient group but a nutrient.");
        }
        
        StringBuilder build = new StringBuilder();
            build.append("(");
        
        for(Nutrient nutrientGroup : group.getChildren()){
            build.append(nutrientGroup);
            build.append(",");
        }
        
        build.setCharAt(build.length() - 1, ')');
        
        return build.toString();
    }
}
