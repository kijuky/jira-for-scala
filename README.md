# jira-for-scala

## Example

### sbt console

```toml
[env]
JIRA_SERVER_URI = "serverUri"
JIRA_ACCESS_TOKEN = "accessToken"
```

```shell
mise trust
sbt console
```

```scala
val jira = new io.github.kijuky.jira.JiraFacade("serverUri", "accessToken")
import jira.Implicits._

jira.issues(filterId = xxxxx).foreach(i => println(i.summary))
```

### cake pattern

```scala
import io.github.kijuky.jira.Implicits._
trait JiraComponent {
  implicit lazy val jira: JiraClient = createJiraClient("serverUri", "accessToken")
}
```

```scala
import io.github.kijuky.jira.Implicits._
object Main extends App with JiraComponent {
  jira.issues(filterId = xxxxx).foreach(i => println(i.summary))
}
```

### zio

```scala
import io.github.kijuky.zio.jira._
import zio._
object Main extends ZIOAppDefault {
  def run = {
    for {
      issueRepo <- ZIO.service[JiraIssueRepo]
      issues <- issueRepo.list(filterId = xxxxx)
      _ <- ZIO.foreach(issues)(i => Console.printLine(i.summary))
    } yield ()
  }.provide(JiraService.layer, JiraIssueRepoImpl.layer)
}
```