package com.example.demonavigation.ui.theme

import com.example.demonavigation.TestVeee
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

sealed class BEEE
 sealed interface BEEE3
data class Success(val data: String) : BEEE(),BEEE3

data class Test1 (var a: Int): BEEE() {

    fun test() {
        val ab = 1
        with(a) {
            this
        }
    }
    fun testBase(x: Int, y: Int, funPlus: (Int, Int) -> Int): Int {
        return funPlus(x,y)
    }

    fun funmuly(x: Int, y: Int, funa: (Int, Int) -> Int): Int {
        val list : Array<Int> = arrayOf(1,2,3)
        val s = "3"
       val tt : String = list.abc(1)
        return funa(x,y)
    }




}

fun <T,Y> Array<T>.abc(bit: Int): Y {
    return this[bit] as Y
}
object Test2 {

}
 fun execute(block: () -> Unit) {
     println("Không chạy tới đây1")
    block()
     println("Không chạy tới đây3")
}

fun test() {
    execute {
        println("Không chạy tới đây")
    }

}
inline fun <reified T> checkType3(value: Any): Boolean {
    return value is T
}
val flow = MutableSharedFlow<Int>(replay = 1)



fun main() {
    GlobalScope.launch {
        flow.emit(1)
        flow.emit(2)
        flow.emit(3)

        flow.collect {
            println(it)
        }
        flow.collect {
            println(it)
        }
    }

//    val a = Test1(1)
//    val bc = listOf("1","2")
//    bc.forEach {
//
//    }
//    val dd= a.testBase(5,7) {
//        x, y -> x +y +y
//    }
//    println(dd)
//    println(a.a)
//    val b= a.let { it ->
//        it.a = 2
//        5
//    }
//    println(b)
}