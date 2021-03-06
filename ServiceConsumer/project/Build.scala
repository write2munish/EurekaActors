import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions}
 
object ServiceConsumerKernelBuild extends Build {
  val Organization = "org.akka.essentials"
  val Version      = "0.0.1"
  val ScalaVersion = "2.10.1"
 
  lazy val ServiceConsumerKernel = Project(
    id = "ServiceConsumer-Kernel",
    base = file("."),
    settings = defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Dependencies.ServiceConsumerKernel,
      distJvmOptions in Dist := "-Xms256M -Xmx256M",
      outputDirectory in Dist := file("target/ServiceConsumer-dist")
    )
  )
 
  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion,
    crossPaths   := false,
    organizationName := "Typesafe Inc.",
    organizationHomepage := Some(url("http://www.typesafe.com"))
  )
  
  lazy val defaultSettings = buildSettings ++ Seq(
    resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Local Maven Repository" at "file:///home/ubuntu/.m2/repository", 

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
 
  )
}
 
object Dependencies {
  import Dependency._
 
  val ServiceConsumerKernel = Seq(
    akkaKernel, akkaActor,akkaremote,eurekaClient, serviceprovider 
  )
}
 
object Dependency {
  // Versions
  object V {
    val Akka      = "2.1.4"
  }
 
  val akkaKernel = "com.typesafe.akka" %% "akka-kernel" % V.Akka
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.Akka
  val akkaremote = "com.typesafe.akka" %% "akka-remote"  % V.Akka
  val eurekaClient = "com.netflix.eureka" % "eureka-client" % "1.1.97"
  val serviceprovider = "org.akka.essentials.ServiceProvider" % "ServiceProvider" % "0.0.2"
}
