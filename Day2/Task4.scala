import scala.util.control.Breaks._
object Task4 {
  def main(args: Array[String]): Unit = {
    // If Statement
    val speed = 70
    if (speed > 60) {
      println("You are over-speeding!")
    } else {
      println("Your speed is within the limit.")
    }
    // Output: You are over-speeding!

    // If-Else Chain
    val temperature = 15
    if (temperature >= 30) {
      println("It's hot!")
    } else if (temperature >= 20) {
      println("It's warm.")
    } else if (temperature >= 10) {
      println("It's cool.")
    } else {
      println("It's cold.")
    }
    // Output: It's cool.

    // Match with Values
    val animal = "Cat"
    animal match {
      case "Dog" => println("Woof!")
      case "Cat" => println("Meow!")
      case "Cow" => println("Moo!")
      case _ => println("Unknown animal sound!")
    }
    // Output: Meow!

    // Match with Types
    def identify(value: Any): Unit = {
      value match {
        case n: Int if n > 0 => println(s"Positive Integer: $n")
        case n: Int => println(s"Negative Integer: $n")
        case s: String => println(s"String of length ${s.length}")
        case _ => println("Unknown type or value")
      }
    }
    identify(12)
    identify(-5)
    identify("Scala")
    identify(3.14)
    // Output:
    // Positive Integer: 12
    // Negative Integer: -5
    // String of length 5
    // Unknown type or value

    // Nested If Loops
    val age = 22
    val country = "India"
    if (age >= 18) {
      println("You are eligible to vote.")
      if (country == "India") {
        println("Please register to vote in your state.")
      } else {
        println("Check voting rules in your country.")
      }
    } else {
      println("You are not eligible to vote yet.")
    }
    // Output:
    // You are eligible to vote.
    // Please register to vote in your state.

    // If as an Expression
    val number = 7
    val parity = if (number % 2 == 0) "Even" else "Odd"
    println(s"$number is $parity.")
    // Output: 7 is Odd.

    // Return Statement
    def multiply(a: Int, b: Int): Int = {
      return a * b
    }
    val product = multiply(4, 5)
    println(s"Product: $product")
    // Output: Product: 20

    // Break and Continue
    import scala.util.control.Breaks._
    breakable {
      for (i <- 1 to 10) {
        if (i % 3 == 0) {
          println("Breaking on multiple of 3!")
          break()
        }
        println(i)
      }
    }
    // Output:
    // 1
    // 2
    // Breaking on multiple of 3!

  
  }
}
