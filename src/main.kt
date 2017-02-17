import java.util.*

fun main(args: Array<String>) {
    val forest = Forest(5, 30)
    while (forest.hasLife) {
        forest.process()
    }
}

class Forest {
    var time = 0
    var hasLife = true
    var trees = ArrayList<Tree>()
    var animals = ArrayList<Animal>()

    private val bornAnimals = ArrayList<Animal>()

    constructor(treeCnt: Int, animalCnt: Int) {
        for (i in 1..treeCnt) {
            trees.add(Tree())
        }
        for (i in 1..animalCnt) {
            animals.add(Animal(this, i.toString()))
        }
    }

    fun process() {
        ++time

        for (tree in trees) {
            tree.process()
        }

        val toDelete = ArrayList<Animal>()
        for (animal in animals) {
            animal.process()
            if (animal.dead) {
                toDelete.add(animal)
            }
        }
        animals.removeAll(toDelete)
        animals.addAll(bornAnimals)
        bornAnimals.clear()

        if (animals.isEmpty()) {
            hasLife = false
        }

        if (time % 25 == 0) {
            println("Time $time: animals - ${animals.size}")
        }
    }

    fun populate(bornAnimals: ArrayList<Animal>) {
        this.bornAnimals.addAll(bornAnimals)
    }
}