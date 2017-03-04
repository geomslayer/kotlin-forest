import java.util.*

val MALE = 0
val FEMALE = 1

val FOOD_VALUE = 50
val MOVE_TIME = 3
val EAT_TIME = 3

val LIFE_TIME = 1000
val PREGNANT_TIME = 200
val WITHOUT_FOOD = 50

val CHILDREN_CNT = 3
val PRED_CHILDREN_CNT = 1

val GO_PROBABILITY = 10

enum class AnimalType {
    SQUIRREL, CHIPMUNK, BADGER, FLY_SQUIRREL, WOODPECKER, WOLF, EAGLE
}

val TYPES = AnimalType.values().size


open class Animal(val forest: Forest, val type: AnimalType) : Comparable<Animal> {
    val rand = Random()
    val sex = rand.nextInt(2)
    val name = (forest.lastAnimal++).toString()

    var curTree = getRandomTree()
    var lifetime = LIFE_TIME
    var fedTime = 0
    var busy = 0
    var pregnant = 0
    var dead = false

    init {
        curTree.settle(this)
    }

    fun process() {
        --fedTime
        --lifetime
        busy = Math.max(0, busy - 1)
        pregnant = Math.max(0, pregnant - 1)

        if (pregnant == 1) {
            val bornAnimals = ArrayList<Animal>()       // рожает потомство
            val cnt = if (isPredator(type)) PRED_CHILDREN_CNT else CHILDREN_CNT
            for (i in 1..cnt) {
                bornAnimals.add(newAnimal(forest, type))
            }
            forest.populate(bornAnimals)
        }

        if (lifetime == 0) {
            die("old age")
            return
        }
        if (fedTime <= 0) {     // если животное голодно
            if (WITHOUT_FOOD == -1 * fedTime) {
                die("hunger")
                return
            }
            searchFood()
        } else if (wantChildren()) {
            for (partner in curTree.animals) {
                if (partner.type == type && partner.sex xor sex == 1 && partner.wantChildren()) {
                    if (sex == FEMALE) {
                        pregnant = PREGNANT_TIME
                    } else {
                        partner.pregnant = PREGNANT_TIME
                    }
                    break
                }
            }
            if (rand.nextInt() % GO_PROBABILITY == 0) {
                go()
            }
        }
    }

    open fun searchFood() {
        for (fType in getFoodTypes()) {
            if (curTree.tryEat(fType)) {
                feedMyself()
                return
            }
        }
        go()
    }

    fun wantChildren(): Boolean {
        return !dead && fedTime > 0 && pregnant == 0 && busy == 0
    }

    private fun getFoodTypes(): List<FoodType> {
        return when (type) {
            AnimalType.SQUIRREL -> listOf(FoodType.CONE, FoodType.NUT)
            AnimalType.CHIPMUNK -> listOf(FoodType.FALLEN_CONE, FoodType.FALLEN_NUT)
            AnimalType.BADGER -> listOf(FoodType.ROOT)
            AnimalType.FLY_SQUIRREL -> listOf(FoodType.MAPLE_LEAVE)
            AnimalType.WOODPECKER -> listOf(FoodType.WORM)
            else -> listOf(FoodType.WORM)
        }
    }

    protected fun feedMyself() {
        busy += EAT_TIME
        fedTime += FOOD_VALUE
    }

    private fun getRandomTree(): Tree {
        return forest.trees[rand.nextInt(forest.trees.size)]
    }

    protected fun go() {
        val nextTree = forest.getAdjacentTree(curTree)
        curTree.moveOut(this)
        curTree = nextTree
        curTree.settle(this)
        busy += MOVE_TIME
    }

    fun die(reason: String) {
        dead = true
//        System.err.println("Animal $name died due to $reason")
    }

    override fun compareTo(other: Animal): Int {
        return name.compareTo(other.name)
    }

}

class Predator(forest: Forest, type: AnimalType) : Animal(forest, type) {
    override fun searchFood() {
        for (animal in curTree.animals) {
            if (animal.dead) {
                continue
            }
            when (type) {
                AnimalType.EAGLE -> when (animal.type) {
                    AnimalType.WOODPECKER, AnimalType.FLY_SQUIRREL -> {
                        animal.die("was eaten")
                        feedMyself()
                        return
                    }
                }
                AnimalType.WOLF -> when (animal.type) {
                    AnimalType.CHIPMUNK, AnimalType.SQUIRREL, AnimalType.BADGER -> {
                        animal.die("was eaten")
                        feedMyself()
                        return
                    }
                }
            }
        }
        go()
    }

}

fun newAnimal(forest: Forest, type: AnimalType): Animal {
    return when (type) {
        AnimalType.EAGLE, AnimalType.WOLF -> Predator(forest, type)
        else -> Animal(forest, type)
    }
}

fun isPredator(type: AnimalType): Boolean {
    return type == AnimalType.EAGLE || type == AnimalType.WOLF
}