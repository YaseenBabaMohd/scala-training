package utils

import play.api.data.validation.ValidationError

import scala.util.matching.Regex

object Validation {

  // Regular expression for validating email addresses
  private val emailPattern: Regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$".r

  // Regular expression for validating contact numbers (e.g., basic US phone number format)
  private val phonePattern: Regex = "^[0-9]{10}$".r

  /**
   * Validate the format of an email address.
   *
   * @param emailInput The email address to validate.
   * @return None if the email is valid, Some(ValidationError) otherwise.
   */
  def validateEmail(emailInput: String): Option[ValidationError] = {
    emailInput match {
      case emailPattern(_*) => None // Email is valid
      case _ => Some(ValidationError("Invalid email format"))
    }
  }

  /**
   * Validate the format of a contact number.
   *
   * @param phoneInput The contact number to validate.
   * @return None if the contact number is valid, Some(ValidationError) otherwise.
   */
  def validateContactNumber(phoneInput: String): Option[ValidationError] = {
    phoneInput match {
      case phonePattern(_*) => None // Contact number is valid
      case _ => Some(ValidationError("Invalid contact number format. It should be a 10-digit number"))
    }
  }
}
