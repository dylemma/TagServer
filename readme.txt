Before doing anything else, open up "build.sbt" (at the same level as this file)
and modify the project name and any other desired properties.

Run "sbt.bat" on Windows, or "sbt" on *nix.

Within sbt, type "update" and press enter. SBT will go online to resolve the
various dependencies that have been set up, including Lift, Jetty, ScalaTest,
and SbtEclipse.

When updating is done, type "eclipse" and press enter. This will create the
".project" and ".classpath" files, which can be imported into Eclipse as a
Scala project.

"compile" will compile the main sources.
"test:compile" will compile tests.
"jetty-run" will run the jetty server.