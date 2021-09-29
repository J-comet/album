package hs.project.album.test

fun main() {
    // val - 상수
    // var - 변수
    var x = 10
    var str1 = "as"
    var isMarred = true

    var a: Int // 코틀린에서 선호하지 않는 방식

    println(method(10,10))
    arrayAndList()
    methodIf()
    methodFor()
    methodWhen()
}

// return 타입 메서드
fun method(a: Int, b:Int): Int{
    return a + b
}
// 한줄로 나타낼 수 있을때 줄인 return 메서드
fun method2(a: Int, b:Int): Int = a + b


// return 이 없는 함수
fun noReturn(a: Int, b:Int): Unit{  // Unit = void 일반적으로 Unit 은 생략
    print(a + b)
}

// 배열,리스트
fun arrayAndList(){
    val items = arrayOf(1,2,3,4,5) // 배열
    println(items)

    val items2 = listOf(1,2,3) // 리스트 [불변]
    println(items2)

    val items3 = arrayListOf(9,8,7,6) // 변경가능한 리스트

    // add 는 항상 마지막에 값이 추가됨
    items3.add(1)
    items3.remove(6)

    // 원하는 위치에 넣고 싶을 경우 2가지 방법
    items3.set(0,11)  // 인덱스로 위치 확인
    items3[2] = 22
    println(items3)
}


// if, for , when
fun methodIf(){
    val x = 2
    if (x % 2 ==0){
        println("짝")
    } else {
        println("홀")
    }

    // 위의 식을 줄이기
    val isEven = if (x % 2 == 0) "짝" else "홀"
}

fun methodFor(){
    // 기본 for 문
    for (i in 0..9){  // 0 부터 9 까지
        print(i)
    }

    // for each 문
    val numbers = listOf(9,8,7,6)
    for (i in numbers) {
        print(i)
    }

    println()
}

fun methodWhen(){
    var x = 1

    when(x){
        1 -> println("정답")
        2 -> println("오답")
        4,5,6 -> println("모두 오답")
        in 10..20 -> println("10부터 20사이의 숫자입니다")
        !in 20..30 -> println("!in")
        else -> println("else")
    }
}














