package hs.project.album.test

fun main() {
    // java => Person person = new Person();
    // kotlin => val person = Person()
    val person = Person("홍길동")

    // name 이 'val' 라면 이런식으로 변경 불가능
    person.name = "투투"
    println(person.name)

    val info = Info("정보",20)
    println(info)
}

// 생성자 바로 작은괄호에 선언가능
// name 은 'var' , 'val' 인지에 따라 변경될수있는지 결정
class Person(var name: String){
    init {  // 기본생성자
        // 생성자를 통해 인스턴스가 만들어 질때 호출
        println(name)
    }
}


// data 클래스
data class Info(
    val name: String,
    val age: Int
)


