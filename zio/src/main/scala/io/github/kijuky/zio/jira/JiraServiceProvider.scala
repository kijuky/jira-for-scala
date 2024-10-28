package io.github.kijuky.zio.jira

import zio.*

trait JiraServiceProvider:
  def get: Task[JiraService]

object JiraServiceProvider:
  def get: RIO[JiraServiceProvider, JiraService] =
    ZIO.serviceWithZIO(_.get)
