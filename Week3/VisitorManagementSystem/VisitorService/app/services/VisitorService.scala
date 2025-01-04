package services

import repositories.{VisitorIdentityProofRepository, VisitorRepository}
import requests.{Visitor, VisitorIdentityProof}

import javax.inject._
import scala.concurrent.Future

@Singleton
class VisitorService @Inject()(
                                visitorRepository: VisitorRepository,
                                visitorIdentityProofRepository: VisitorIdentityProofRepository
                              ) {

  // A local cache of visitors, initialized as an empty list.
  private var visitors: List[Visitor] = List()

  /**
   * Registers a visitor by adding their information to the repository.
   *
   * @param visitorData The visitor details to register.
   * @return A Future containing the ID of the newly created visitor.
   */
  def checkIn(visitorData: Visitor): Future[Int] = {
    val visitorIdFuture: Future[Int] = visitorRepository.create(visitorData)
    visitorIdFuture
  }

  /**
   * Adds identity proof information for a visitor.
   *
   * @param visitorIdentityData The identity proof details for the visitor.
   * @return A Future containing the ID of the newly created identity proof record.
   */
  def addVisitorIdentity(visitorIdentityData: VisitorIdentityProof): Future[Int] = {
    val identityProofIdFuture: Future[Int] = visitorIdentityProofRepository.create(visitorIdentityData)
    identityProofIdFuture
  }

  /**
   * Retrieves a list of all visitors from the repository.
   *
   * @return A Future containing a sequence of visitors.
   */
  def list(): Future[Seq[Visitor]] = {
    val visitorsListFuture: Future[Seq[Visitor]] = visitorRepository.list()
    visitorsListFuture
  }

  /**
   * Fetches details of a visitor by their unique ID.
   *
   * @param id The unique ID of the visitor.
   * @return A Future containing an Option of the visitor if found, None otherwise.
   */
  def getVisitorById(id: Int): Future[Option[Visitor]] = {
    val visitorByIdFuture: Future[Option[Visitor]] = visitorRepository.getVisitorById(id)
    visitorByIdFuture
  }

  /**
   * Fetches details of a visitor by their email address.
   *
   * @param mail The email address of the visitor.
   * @return A Future containing an Option of the visitor if found, None otherwise.
   */
  def getVisitorByEmail(mail: String): Future[Option[Visitor]] = {
    val visitorByEmailFuture: Future[Option[Visitor]] = visitorRepository.getVisitorByEmail(mail)
    visitorByEmailFuture
  }

}
