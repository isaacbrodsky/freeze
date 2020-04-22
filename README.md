# Freeze

ZZT compatible-ish game engine in Java. It can (sort of) play ZZT and SuperZZT
games, and can edit both ZZT and SuperZZT games.

The Freeze engine loads and plays ZZT games, but due to differences in program
design (mainly having to do with not preserving stats ordering), it is not
capable of correctly playing a number of ZZT worlds. The engine code here is
now many years old and will probably be rewritten if I want to use it more.

Playing ZZT games using Freeze may not be very interesting due to the
[Reconstruction of ZZT](github.com/asiekierka/reconstruction-of-zzt) now being
available. However, this code might be useful for extracting data and
screenshots from ZZT worlds, for generating ZZT worlds, and as a game editor.

# ZZT Search

Also in this repository is the "ZZT Search" application, which can be used to
scan a filesystem of ZZT games and load them into Elasticsearch. A user can
then search through the text messages, both OOP and written on the board, in
Elasticsearch.

## How to start the ZZT Search application

1. Run `mvn clean install` to build your application
1. Build the index `java -jar zztsearch-web/target/zztsearch-0.0.1-SNAPSHOT.jar index zztsearch-web/config.yml`
1. Start application with `java -jar zztsearch-web/target/zztsearch-0.0.1-SNAPSHOT.jar server zztsearch-web/config.yml`
1. To check that your application is running enter url `http://localhost:8080`

# License

TBD

# Thanks

In addition to being inspired by Tim Sweeney's ZZT, I was helped by the
[Mystical Winds Encyclopedia](https://museumofzzt.com/file/m/Mwencv14.zip)
and its description of the internals of ZZT.
