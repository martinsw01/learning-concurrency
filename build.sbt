name := "concurrency"

version := "1.0"

scalaVersion := "2.13.12"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at
    "https://oss.sonatype.org/content/repositories/snaphsotd",
  "Sonatype OSS Releases" at
    "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repopsitory" at
    "https://repo.typesafe.com/typesafe/releases"
)

libraryDependencies += "commons-io" % "commons-io" % "2.4"

fork := false