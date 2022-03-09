# copycat

Backup schedudling desktop app developed with [JavaFX](https://github.com/openjdk/jfx)

### Features:

- Dedicated control panel for software management
- Set directory / multiple directories to back up + backup destination
- Choose backup time intervals
- Tray icon that displays backup and software status

### Installation and Running:

There are two installation options:

- Creating an executable, and containing the app icon from the images directory using [Launch4j](http://launch4j.sourceforge.net/)
- Running `mvn compile`, `mvn package` and then `mvn install`, which will a jar file that can be run using `java -jar target/copycat-maven-0.0.1.jar`
