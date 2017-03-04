import java.util.*

val FOOD_APPEAR_PROBABILITY = 10
val FOOD_AMOUNT = 5

enum class TreeType {
    PINE, OAK, BIRCH, MAPLE, WALNUT
}

enum class FoodType {
    WORM, CONE, FALLEN_CONE, ROOT, NUT, FALLEN_NUT, MAPLE_LEAVE
}

open class Tree(val id: Int): Comparable<Tree> {
    val rand = Random()
    val type = TreeType.values()[rand.nextInt(TreeType.values().size)]
    val food = HashMap<FoodType, Int>()
    val animals = TreeSet<Animal>()

    fun settle(animal: Animal) {
        animals.add(animal)
    }

    fun moveOut(animal: Animal) {
        animals.remove(animal)
    }

    fun tryEat(type: FoodType): Boolean {
        val count = food.getOrDefault(type, 0)
        if (count > 0) {
            food[type] = count - 1
            return true
        }
        return false
    }

    fun process() {
        if (rand.nextInt() % FOOD_APPEAR_PROBABILITY == 0) {
            increaseFood()
        }
    }

    open fun increaseFood() {
        food[FoodType.WORM] = FOOD_AMOUNT
        food[FoodType.ROOT] = FOOD_AMOUNT
        when (type) {
            TreeType.PINE -> {
                food[FoodType.FALLEN_CONE] = Math.min(FOOD_AMOUNT, food.getOrDefault(FoodType.FALLEN_CONE, 0) + food.getOrDefault(FoodType.CONE, 0))
                food[FoodType.CONE] = FOOD_AMOUNT
            }
            TreeType.WALNUT -> {
                food[FoodType.FALLEN_NUT] = Math.min(FOOD_AMOUNT, food.getOrDefault(FoodType.FALLEN_NUT, 0) + food.getOrDefault(FoodType.NUT, 0))
                food[FoodType.NUT] = FOOD_AMOUNT
            }
            TreeType.MAPLE -> {
                food[FoodType.MAPLE_LEAVE] = FOOD_AMOUNT
            }
        }
    }

    override fun compareTo(other: Tree): Int {
        return id.compareTo(other.id)
    }

}