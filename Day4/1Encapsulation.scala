class Encapsulation(var value: Double) {

  def this(value: Double, typeCode: Int) = {
    this(value)
    this.typeCode = typeCode
  }

  def add(amount: Double): Unit = value += amount

  def subtract(amount: Double): Unit = value -= amount

  def multiply(factor: Double): Unit = value *= factor

  def getValue: Double = value

  def isDivisibleBy(divisor: Int): Boolean = {
    value % divisor == 0
  }

  private var privateField = 0
  def getPrivateField: Int = privateField

  var typeCode: Int = 0
}

@main def main(): Unit = {
  println("Inside the main function")
  val firstObj = new Encapsulation(5000.0)
  val secondObj = new Encapsulation(2500.0, 5)

  val privateField = firstObj.getPrivateField

  println(firstObj.value)
  println(secondObj.typeCode)
  println(s"The private field value through a method: $privateField")
  println(s"Checking divisibility by 10: ${firstObj.isDivisibleBy(10)}")
  firstObj.add(100)
  println(s"Current value after addition: ${firstObj.getValue}")
  println(s"Again checking divisibility by 10: ${firstObj.isDivisibleBy(10)}")
}
