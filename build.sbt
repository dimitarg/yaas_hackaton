import AssemblyKeys._ // put this at the top of the file

assemblySettings


name := "yaas_proxy"

organization := "com.novarto"

scalaVersion := "2.11.7"

version := "0.1-SNAPSHOT"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"

libraryDependencies += "io.spray" %% "spray-client" % "1.3.3"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

libraryDependencies += "io.spray" %% "spray-routing" % "1.3.3"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

mainClass in assembly := Some("com.novarto.yaas.Main")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case "unwanted.txt"     => MergeStrategy.discard
  case x => old(x)
}
}
