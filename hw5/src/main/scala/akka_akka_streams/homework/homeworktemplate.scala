package akka_akka_streams.homework

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, SystemMaterializer}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipWith}


object homeworktemplate {
  implicit val system: ActorSystem = ActorSystem("fusion")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  val graph = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>

    import akka.stream.scaladsl.GraphDSL.Implicits._

    val input =  builder.add(Source(1 to 10))
    val multiplier10 = Flow[Int].map(x=>x*10)
    val multiplier2 = Flow[Int].map(x=>x*2)
    val multiplier3 = Flow[Int].map(x=>x*3)

    val broadcast = builder.add(Broadcast[Int](3))
    val zip = builder.add(ZipWith((a: Int, b: Int, c: Int) => a + b + c))

    input ~> broadcast ~> multiplier10 ~> zip.in0
    broadcast ~> multiplier2  ~> zip.in1
    broadcast ~> multiplier3  ~> zip.in2
    zip.out ~> Sink.foreach(println)

    ClosedShape
  }

  def main(args: Array[String]) : Unit ={
    RunnableGraph.fromGraph(graph).run()
  }
}