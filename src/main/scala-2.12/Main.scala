import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Future}
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends App {

  val startGettingReady = 60
  val endGettingReady   = 90
  val startPuttingShoes = 35
  val endPuttingShoes = 45
  println("Let's go for a walk!")

  val gettingReady = (name: String) => Future {
    println(s"$name started getting ready")
    val timeToBeReady = calculateTimeTobeReady(startGettingReady, endGettingReady)
    TimeUnit.SECONDS.sleep(timeToBeReady)
    println(s"$name spent $timeToBeReady seconds getting ready")
    timeToBeReady
  }

  val puttinShoes = (name: String) => Future {
    println(s"$name started putting on shoes")
    val timeToBeReady = calculateTimeTobeReady(startPuttingShoes, endPuttingShoes)
    TimeUnit.SECONDS.sleep(timeToBeReady)
    println(s"$name spent $timeToBeReady seconds putting on shoes")
  }

  val alarm = (_: Int) => Future {
    println("Arming alarm")
    println("Alarm is counting down.")
    TimeUnit.SECONDS.sleep(60)
    println("Alarm is armed.")
  }

  val dailyWalk = for {
    _ <- Future.unit
    gtr1 = gettingReady("Alice")
    gtr2 = gettingReady("Bob")
    x1 <- gtr1
    x2 <- gtr2//These inputs are only required to alarm function be executed it when x1 and x2 were finished it
    startAlarm = alarm(x1 + x2)
    startShoes = awaitForShoes()
     _ <- startAlarm
     _ <- startShoes
  } yield ()

  Await.result(dailyWalk, 200.seconds)

  private def awaitForShoes(): Future[Unit] = {
    for {
      _ <- Future.unit
      aliceStartPuttingShoes = puttinShoes("Alice")
      bobStartPuttingShoes = puttinShoes("Bob")
      _ <- aliceStartPuttingShoes
      _ <- bobStartPuttingShoes
    } yield {
      println("Exiting and locking the door.")
    }
  }
  private def calculateTimeTobeReady(start: Int, end: Int): Int = {
    val rnd = new Random()
    start + rnd.nextInt((end - start) + 1)
  }
}
