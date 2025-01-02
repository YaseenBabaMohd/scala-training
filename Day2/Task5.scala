object Taks5 {
  def main(args: Array[String]): Unit = {
    // Defining Arrays
    val x: Array[Int] = Array(1, 2, 3, 4)
    val y = Array("sri", "sree")

    // Iterating over Arrays
    println("Elements of x:")
    for (m1 <- x) {
      print(s" $m1")
    }
    println("")

    println("Elements of y:")
    for (m1 <- y) {
      print(s" $m1")
    }
    println("\n")

    // Another Array Example
    val numbers: Array[Int] = Array(1, 2, 3, 4)

    println("Accessing and Modifying Elements:")
    val firstNumber = numbers(0) // Accessing the first element
    println(s"First Number: $firstNumber")

    numbers(1) = 0 // Modifying an element
    println("Modified numbers array:")
    for (num <- numbers) {
      print(s" $num")
    }
    println("\n")

    // Array Length
    val length = numbers.length
    println(s"Length of the array: $length")

    // Using foreach
    println("Using foreach to print elements:")
    numbers.foreach(println)

    // Array Transformation
    val doubled = numbers.map(_ * 2)
    println("Doubled elements:")
    for (m1 <- doubled) {
      print(s" $m1")
    }
    println("")

    // Filtering Elements
    val evens = numbers.filter(_ % 2 == 0)
    println("Even elements:")
    for (m1 <- evens) {
      print(s" $m1")
    }
    println("")

    // Summing Elements
    val sum = numbers.reduce(_ + _)
    println(s"Sum of elements: $sum")
  }
}
