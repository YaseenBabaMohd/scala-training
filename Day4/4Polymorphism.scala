// Implicit class to extend Double with a range finder
implicit class EnhancedDouble(val x: Double) {
  def rangeFinder(): String = {
    if(x >= 0.0 && x <= 100.0)
      "In the Range of 0-100"
    else if(x > 100.0)
      "In the Range of 100+"
    else
      "Negative"
  }
}

// Abstract class for shapes with area and perimeter methods
abstract class Shape {
  def area(params: Double*): Double // Abstract method to calculate area with variable parameters
  def perimeter(params: Double*): Double // Abstract method to calculate perimeter
}

// Square class inheriting from Shape
class Square(val xtype: String) extends Shape {
  def this() = {
    this("Square")
  }

  override def area(params: Double*): Double = {
    if (params.length != 1) {
      println("Pass only one parameter for square")
      return 0
    }
    val side = params(0)
    val a = side * side
    println(s"Area of $xtype is $a")
    a
  }

  override def perimeter(params: Double*): Double = {
    if (params.length != 1) {
      println("Pass only one parameter for square")
      return 0
    }
    val side = params(0)
    val p = 4 * side
    println(s"Perimeter of $xtype is $p")
    p
  }
}

// Rectangle class inheriting from Shape
class Rectangle(val xtype: String) extends Shape {
  def this() = {
    this("Rectangle")
  }

  override def area(params: Double*): Double = {
    if (params.length != 2) {
      println("Pass two parameters for rectangle")
      return 0
    }
    val length = params(0)
    val breadth = params(1)
    val a = length * breadth
    println(s"Area of $xtype is $a")
    a
  }

  override def perimeter(params: Double*): Double = {
    if (params.length != 2) {
      println("Pass two parameters for rectangle")
      return 0
    }
    val length = params(0)
    val breadth = params(1)
    2 * (length + breadth)
  }
}

// Circle class inheriting from Shape
class Circle extends Shape {
  override def area(params: Double*): Double = {
    if (params.length != 1) {
      println("Pass one parameter for circle")
      return 0
    }
    val radius = params(0)
    Math.PI * radius * radius
  }

  override def perimeter(params: Double*): Double = {
    if (params.length != 1) {
      println("Pass one parameter for circle")
      return 0
    }
    val radius = params(0)
    2 * Math.PI * radius
  }
}

// Triangle class inheriting from Shape
class Triangle extends Shape {
  override def area(params: Double*): Double = {
    if (params.length != 2) {
      println("Pass two parameters for triangle for area")
      return 0
    }
    val base = params(0)
    val height = params(1)
    0.5 * base * height
  }

  override def perimeter(params: Double*): Double = {
    if (params.length != 3) {
      println("Pass three parameters for triangle for perimeter")
      return 0
    }
    val s1 = params(0)
    val s2 = params(1)
    val s3 = params(2)
    s1 + s2 + s3
  }
}

@main def main: Unit = {
  val objSquare = new Square("Square Plot")
  println(objSquare.area(3)) // Pass side of square
  println(objSquare.perimeter(2)) // Pass side of square for perimeter

  // Using rangeFinder to check the value range of the area
  println(objSquare.area(4).rangeFinder()) // Returns range for the area of square

  val objRectangle = new Rectangle("Rectangle Plot")
  println(objRectangle.area(5, 6)) // Pass length and breadth for rectangle
  println(objRectangle.perimeter(5, 6)) // Pass length and breadth for perimeter

  val objCircle = new Circle()
  println(objCircle.area(7)) // Pass radius for circle
  println(objCircle.perimeter(7)) // Pass radius for perimeter

  val objTriangle = new Triangle()
  println(objTriangle.area(6, 8)) // Pass base and height for triangle area
  println(objTriangle.perimeter(3, 4, 5)) // Pass sides for triangle perimeter
}
