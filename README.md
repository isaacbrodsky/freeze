# Freeze

This repo contains several ZZT related projects.

* Freeze 1: A ZZT compatible-ish game engine in Java. It can (sort of) play ZZT and SuperZZT
  games, and can edit both ZZT and SuperZZT games.
* Freeze 2: A ZZT compatible game engine in Java. It tries to be an advanced editor for ZZT
  worlds - giving precise control over stats and other details.
* ZZT Search: Code for loading ZZT worlds into Lucene, and for searching ZZT worlds.

## Freeze 1

The Freeze 1 engine loads and plays ZZT games, but due to differences in program
design (mainly having to do with not preserving stats ordering), it is not
capable of correctly playing a number of ZZT worlds. The engine code here is
now many years old and is unlikely receive updates.

Playing ZZT games using Freeze may not be very interesting due to the
[Reconstruction of ZZT](github.com/asiekierka/reconstruction-of-zzt) now being
available.

## Freeze 2

Freeze 2 is an in progress rewrite of the Freeze 1 engine for use as an advanced
ZZT world editor. It intends to provide fine-grained control over the world file.

At this time Freeze 2 does not support any gameplay. Freeze 2 is also useful for
extracting text and data from ZZT worlds, and for programmatically generating
ZZT worlds.

## ZZT Search

* Want to find that one game with the guy who says that one thing?
* Want to know how many ZZT worlds have bathrooms in them?
* Want to find easter eggs?

Enter the "ZZT Search" application, which can be used to scan a filesystem of
ZZT games and load them into Apache Lucene. A user can then search through the
text messages, both OOP and text written on the board.

In the future, this could be expanded to load the text into Elasticsearch.

### How to start the ZZT Search application

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
