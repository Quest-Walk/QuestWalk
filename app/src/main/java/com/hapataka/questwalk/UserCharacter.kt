package com.hapataka.questwalk

data class UserCharacter(
    val characterId: Int,
    val charImage: Int
)


open class Bird {
    fun cry(){}
    fun eat(bird: String){}
}

interface Fly {
    fun fly()
}

class Eagle: Bird(), Fly {
    override fun fly() {
        TODO("Not yet implemented")
    }
}
fun main() {
    val food = "food"


}
