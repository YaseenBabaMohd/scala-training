abstract class Vehicle {
  // Protected variables, can be used in subclasses
  protected val carFuelCap = 40.0
  protected val bikeFuelCap = 18.0
  protected val autoFuelCap = 9.0

  // Private variables, can only be accessed within the class
  private val privateTest: String = "I am a private variable of Vehicle class"

  // Public by default, can be accessed directly
  var vehicleType: String = "I am a public variable of Vehicle class"
  var mileage: Int
  var model: String

  // Method to print private variable
  def printPrivate: String = privateTest

  // Method to print protected variable
  def printProtected: String = s"$carFuelCap"

  // Abstract methods for subclasses to implement
  def fuelEfficiency(): Double
  def setMileage(x: Int): Unit
  def describe(): String
}

class CarVehicle(var mileage: Int, var model: String) extends Vehicle {
  // Implementing the fuel efficiency calculation for car
  def fuelEfficiency(): Double = {
    mileage / carFuelCap
  }

  // Set the mileage value
  def setMileage(x: Int): Unit = mileage = x

  // Describe the car details
  def describe(): String = s"Car with mileage $mileage and model is $model"
}

class BikeVehicle(var mileage: Int, var model: String) extends Vehicle {
  // Implementing the fuel efficiency calculation for bike
  def fuelEfficiency(): Double = {
    mileage / bikeFuelCap
  }

  // Set the mileage value
  def setMileage(x: Int): Unit = mileage = x

  // Describe the bike details
  def describe(): String = s"Bike with $mileage mileage and model $model"
}

class AutoVehicle(var mileage: Int, var model: String) extends Vehicle {
  // Implementing the fuel efficiency calculation for auto
  def fuelEfficiency(): Double = {
    mileage / autoFuelCap
  }

  // Set the mileage value
  def setMileage(x: Int): Unit = mileage = x

  // Describe the auto details
  def describe(): String = s"Auto with $mileage mileage and model $model"
}

@main def runExamples(): Unit = {
  val hyundaiCar = new CarVehicle(20, "Hyundai")
  println(hyundaiCar.vehicleType) // Public variable of the abstract class Vehicle
  println(hyundaiCar.printPrivate) // Access private variable through a method in Vehicle
  println(hyundaiCar.printProtected) // Access protected variable through a method in Vehicle

  println(hyundaiCar.describe())
  println(s"Fuel Efficiency: ${hyundaiCar.fuelEfficiency()}")

  val audiCar = new CarVehicle(18, "Audi")
  println(audiCar.describe())
  println(s"Fuel Efficiency: ${audiCar.fuelEfficiency()}")

  // You can create instances of BikeVehicle and AutoVehicle as well
  val hondaBike = new BikeVehicle(30, "Honda")
  println(hondaBike.describe())
  println(s"Fuel Efficiency: ${hondaBike.fuelEfficiency()}")

  val suzukiAuto = new AutoVehicle(15, "Suzuki")
  println(suzukiAuto.describe())
  println(s"Fuel Efficiency: ${suzukiAuto.fuelEfficiency()}")
}
