import java.util.*

val FOOD_VALUE = 50
val MOVE_TIME = 3
val EAT_TIME = 3

val PREGNANT_TIME = 200
val LIFE_TIME = 1000
val WITHOUT_FOOD = 50
val CHILDREN_CNT = 3

val GO_PROBABILITY = 10

val MALE = 0
val FEMALE = 1

class Animal(val forest: Forest, val name: String) {
    val rand = Random()
    val sex = rand.nextInt(2)

    var curTree = getRandomTree()
    var lifetime = LIFE_TIME
    var fedTime = FOOD_VALUE
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
            val bornAnimals = ArrayList<Animal>()
            for (i in 1..CHILDREN_CNT) {
                bornAnimals.add(Animal(forest, "$name->$i"))
            }
            forest.populate(bornAnimals)
        }

        if (lifetime == 0) {
            die("old age")
            return
        }
        if (fedTime <= 0) {
            if (WITHOUT_FOOD == -1 * fedTime) {
                die("hunger")
                return
            }
            searchFood()
        } else if (wantChildren()) {
            for (partner in curTree.animals) {
                if (partner.sex xor sex == 1 && partner.wantChildren()) {
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

    fun searchFood() {
        if (curTree.foodCnt > 0) {
            curTree.eatOne()
            feedMyself()
        } else {
            go()
        }
    }

    fun wantChildren() : Boolean {
        return !dead && fedTime > 0 && pregnant == 0 && busy == 0;
    }

    private fun feedMyself() {
        busy += EAT_TIME
        fedTime += FOOD_VALUE
    }

    private fun getRandomTree(): Tree {
        return forest.trees[rand.nextInt(forest.trees.size)]
    }

    private fun go() {
        var nextTree: Tree
        do {
            nextTree = getRandomTree()
        } while (curTree === nextTree)
        curTree.moveOut(this)
        curTree = nextTree
        curTree.settle(this)
        busy += MOVE_TIME
    }

    private fun die(reason: String) {
        dead = true
        System.err.println("Animal $name died due to $reason")
    }
}