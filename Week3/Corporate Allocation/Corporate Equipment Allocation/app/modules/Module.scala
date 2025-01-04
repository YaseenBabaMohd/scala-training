package modules

import play.api.inject._
import services.{overDueChecker}

class Module extends SimpleModule(
  bind[overDueChecker].toSelf.eagerly()
)