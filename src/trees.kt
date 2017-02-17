import java.util.*

val FOOD_APPEAR_PROBABILITY = 10

class Tree {
    val rand = Random()
    var foodCnt = 10
    var animals = HashSet<Animal>()

    fun settle(animal: Animal) {
        animals.add(animal)
    }

    fun moveOut(animal: Animal) {
        animals.remove(animal)
    }

    fun eatOne() {
        if (foodCnt <= 0) {
            throw IllegalAccessError("Food <= 0")
        }
        --foodCnt
    }

    fun process() {
        if (rand.nextInt() % FOOD_APPEAR_PROBABILITY == 0) {
            foodCnt += 10
        }
    }
}