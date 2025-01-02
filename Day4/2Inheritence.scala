trait AnimalBehavior {
  def greet(): String = {
    "I belong to the AnimalBehavior trait!"
  }

  def intToDouble: Int => Double = x => x.toDouble
}

trait MammalCharacteristics {
  def info: String = {
    "I am a mammal."
  }
}

// Abstract class for all animals
abstract class LivingBeing(val speciesName: String) extends AnimalBehavior {
  // Abstract method that subclasses must implement
  def vocalize(): String

  def intToString: Int => String = x => x.toString
}

class Dog(speciesName: String) extends LivingBeing(speciesName) with AnimalBehavior {
  // Dog subclass implementation
  override def vocalize(): String = {
    s"$speciesName barks: Woof!"
  }
}

class Cat(speciesName: String) extends LivingBeing(speciesName) {
  // Cat subclass implementation
  override def vocalize(): String = {
    s"$speciesName purrs: Meow!"
  }
}

class Zebra(speciesName: String) extends LivingBeing(speciesName) {
  // Zebra subclass implementation
  override def vocalize(): String = {
    s"$speciesName vocalizes: eehaww!"
  }
}

class Hybrid(speciesName: String) extends LivingBeing(speciesName) with AnimalBehavior with MammalCharacteristics {
  // Hybrid subclass implementation
  override def vocalize(): String = {
    s"$speciesName makes a sound: Bow Meow!"
  }
}

@main def runExamples: Unit = {
  val myDog = new Dog("Rex")
  val myCat = new Cat("Bella")
  val myHybrid = new Hybrid("Toby")

  // Test hybrid object
  println(myHybrid.info)
  println(myHybrid.vocalize())

  println(myHybrid.intToDouble(3))
  println(myHybrid.intToString(44))

  println(myDog.vocalize())
  println(myCat.vocalize())
  println(myDog.greet())

  val myZebra = new Zebra("Ziggy")
  println(myZebra.greet())

  // Type checking to verify superclass type
  println(myDog.isInstanceOf[LivingBeing])
  println(myCat.isInstanceOf[LivingBeing])
}
