object Task8 {
  def main(args: Array[String]): Unit = {
    // Initialize a 2D Array
    val grid = Array(
      Array(10, 20, 30),
      Array(40, 50, 60),
      Array(70, 80, 90)
    )
    println("Accessing 2D Array Elements:")
    println(grid(0)(1)) // Accessing element at row 0, column 1
    println(grid(2)(2)) // Accessing element at row 2, column 2

    println("\nPrinting 2D Array:")
    for (row <- grid) {
      println(row.mkString(", ")) // Printing each row
    }

    // Using ofDim to create a predefined 2D array
    val matrix = Array.ofDim[Int](4, 3)
    for (i <- 0 until 4; j <- 0 until 3) {
      matrix(i)(j) = i * j
    }
    println("\n2D Array Created with ofDim:")
    for (row <- matrix) {
      println(row.mkString(", "))
    }

    // Initialize and print a 3D Array
    val cube = Array.ofDim[Int](2, 3, 2)
    for (i <- 0 until 2; j <- 0 until 3; k <- 0 until 2) {
      cube(i)(j)(k) = i * j + k
    }
    println("\nPrinting 3D Array:")
    for (layer <- cube) {
      for (row <- layer) {
        println(row.mkString(" | ")) // Print each row in a layer
      }
      println("Layer End")
    }

    // Creating a dynamic 2D Array with a pattern
    val rows = 4
    val cols = 6
    val patternArray = Array.ofDim[Int](rows, cols)
    for (i <- 0 until rows; j <- 0 until cols) {
      patternArray(i)(j) = if (i % 2 == 0 && j % 2 == 0) 1 else 0
    }
    println("\nDynamic 2D Array with Pattern:")
    for (row <- patternArray) {
      println(row.mkString(" "))
    }
  }
}
