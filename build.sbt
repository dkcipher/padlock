scalaVersion := "2.11.8"

name := "padlock"

organization := "nl.fizzylogic"

version := "0.1.0-SNAPSHOT"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  
)