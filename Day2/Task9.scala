object CollectionExamples {
  def main(args: Array[String]): Unit = {

    // Immutable List
    val friends = List("Yaseen", "Tinku", "Rajesh")
    println(friends(0))
    friends.foreach(friend => println(friend))
    val newFriends = "Sandeep" :: friends
    println(newFriends)

    // Mutable List
    import scala.collection.mutable.ListBuffer
    val scores = ListBuffer(10, 20, 30)
    scores += 40
    scores += 50
    println(scores)
    scores -= 20
    println(scores)

    // Immutable Set
    val hobbies = Set("Cricket", "Chess", "Coding", "Chess")
    println(hobbies)
    println(hobbies.contains("Coding"))

    // Mutable Set
    import scala.collection.mutable.Set
    val skillSet = Set("Scala", "Java", "Python")
    skillSet += "Kotlin"
    println(skillSet)
    skillSet -= "Java"
    println(skillSet)

    // Immutable Map
    val nicknames = Map("Yaseen" -> "Yaz", "Tinku" -> "TK", "Rajesh" -> "RJ")
    println(nicknames("Tinku"))
    nicknames.foreach { case (name, nickname) =>
      println(s"$name is called $nickname")
    }

    // Mutable Map
    import scala.collection.mutable.Map
    val ageMap = Map("Sandeep" -> 28, "Yaseen" -> 26)
    ageMap("Rajesh") = 29
    println(ageMap)
    ageMap("Yaseen") = 27
    println(ageMap)

    // Tuples
    val personInfo = ("Tinku", 25, "Developer")
    println(personInfo._1)
    println(personInfo._2)
    println(personInfo._3)

    val (personName, personAge, personRole) = personInfo
    println(s"$personName is $personAge years old and works as a $personRole.")
  }
}
