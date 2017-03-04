import java.util.*

fun main(args: Array<String>) {
    val forest = Forest(150, 400)
    while (forest.hasLife) {
        forest.process()
    }
}


val prob = listOf(18, 19, 16, 19, 18, 5, 5) // распределение животных

fun getAnimalType(ind: Int): AnimalType {   // возвращаем тип в соответствии с распределением
    var sum = 0
    for (i in 0..6) {
        sum += prob[i]
        if (ind % 100 < sum) {
            return AnimalType.values()[i]
        }
    }
    return AnimalType.EAGLE
}

class Forest(val treeCnt: Int, val animalCnt: Int) {
    val rand = Random()
    var time = 0
    var hasLife = true
    var trees = ArrayList<Tree>()
    var animals = ArrayList<Animal>()

    var lastAnimal = 1

    private val bornAnimals = ArrayList<Animal>()

    private val graph = ArrayList<ArrayList<Int>>()

    init {                                  // задаем граф, генерируем животных и деревья
        for (i in 1..treeCnt) {
            trees.add(Tree(i))
        }
        for (i in 1..animalCnt) {
            val curType = getAnimalType(rand.nextInt())
            animals.add(newAnimal(this, curType))
        }
        for (i in 0..treeCnt - 1) {
            graph.add(ArrayList<Int>())
        }
        while (!checkConnectivity()) {
            var u: Int
            var v: Int
            do {
                u = rand.nextInt(treeCnt)
                v = rand.nextInt(treeCnt)
            } while (u == v)
            graph[u].add(v)
            graph[v].add(u)
        }
        for (list in graph) {
            val values = list.distinct()
            list.clear()
            values.toCollection(list)
        }
    }

    private fun checkConnectivity(): Boolean {      // для проверки графа на связность
        val used = Array(treeCnt, { false })        // мы же хотим, чтобы можно было по всему лесу ходить
        dfs(0, used)
        for (status in used) {
            if (!status) {
                return false
            }
        }
        return true
    }

    private fun dfs(vert: Int, used: Array<Boolean>) {
        used[vert] = true
        graph[vert]
                .filterNot { used[it] }
                .forEach { dfs(it, used) }
    }

    fun process() {                     // здесь самое интересное происходит;
        ++time                          // лес оживает;
                                        // запускаем process() у каждого объекта и он производит действия
        for (tree in trees) {           // за одну единицу времени
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
            println("Time $time: animals - ${animals.size} | ${calcStats()}")
        }
    }

    fun calcStats(): String {
        val map = HashMap<AnimalType, Int>()
        for (animal in animals) {
            val cnt = map.getOrDefault(animal.type, 0)
            map[animal.type] = cnt + 1
        }
        var res = ""
        for ((key, value) in map) {
            res += "$key - $value | "
        }
        return res
    }

    fun populate(bornAnimals: ArrayList<Animal>) {
        this.bornAnimals.addAll(bornAnimals)
    }

    fun getAdjacentTree(tree: Tree): Tree {
        val cnt = graph[tree.id - 1].size
        val ind = graph[tree.id - 1][rand.nextInt(cnt)]
        return trees[ind]
    }

}