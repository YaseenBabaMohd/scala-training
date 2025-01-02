object ArrayExamples {
  def main(args: Array[String]): Unit = {
    // Initializing arrays
    val intArray: Array[Int] = Array(5, 6, 7, 8)
    val stringArray = Array("alpha", "beta")
    for (element <- intArray) {
      print(s" $element")
    }
    println("")
    for (item <- stringArray) {
      print(s" $item")
    }
    println("")

    // Single-dimensional array
    val smallArray: Array[Int] = Array(10, 20, 30)
    for (num <- smallArray) {
      print(s" $num")
    }
    println("")

    // Array with predefined values
    val filledArray: Array[Int] = Array.fill(4)(1)
    for (value <- filledArray) {
      print(s" $value")
    }
    println("")

    // Accessing elements in an array
    val valueArray: Array[Int] = Array(11, 22, 33, 44)
    val firstElement = valueArray(0)
    for (element <- valueArray) {
      print(s" $element")
    }
    println("")
    println(s"First Element: $firstElement")

    // Modifying elements in an array
    valueArray(2) = 99
    for (element <- valueArray) {
      print(s" $element")
    }
    println("")

    // Array length
    val arrayLength = valueArray.length
    println(s"Array Length: $arrayLength")

    // Printing elements
    valueArray.foreach(println)

    // Mapping elements to double their values
    val multipliedArray = valueArray.map(_ * 3)
    for (value <- multipliedArray) {
      print(s" $value")
    }
    println("")

    // Filtering even numbers
    val evenNumbers = valueArray.filter(_ % 2 == 0)
    for (value <- evenNumbers) {
      print(s" $value")
    }
    println("")

    // Reducing array to find the sum
    val totalSum = valueArray.reduce(_ + _)
    println(s"Sum of all elements: $totalSum")
  }
}
