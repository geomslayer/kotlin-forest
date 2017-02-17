import java.util.*

class Tree {
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
}