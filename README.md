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
import io.githug.kijuky.jira.JiraFacade
import zio._
object JiraService {
  def layer = ZLayer.succeed(JiraFacade("serverUri", "accessToken"))
}
```

```scala
import zio._
object Main extends ZIOAppDefault {
  def run = {
    for {
      jira <- ZIO.service[JiraService]
    } yield {
      import jira.Implicits._
      jira.issues(filterId = xxxxx).foreach(i => println(i.summary))
    }
  }.provide(JiraService.layer)
}
```