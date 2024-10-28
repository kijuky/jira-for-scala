package io.github.kijuky.zio.jira

import zio.*

trait JiraUserRepo:
  def findByName(name: String): Task[Option[JiraUser]]

object JiraUserRepo:
  def findByName(name: String): RIO[JiraUserRepo, Option[JiraUser]] =
    ZIO.serviceWithZIO(_.findByName(name))
