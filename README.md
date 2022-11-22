# Freeze

This repo contains several ZZT related projects.

* Freeze 2: A ZZT compatible game editor in Java. It gives precise control over stats and other details.
* ZZT Search: Code for loading ZZT worlds into Lucene, and for searching ZZT worlds.
* Freeze 1: A ZZT compatible-ish game engine in Java. It can (sort of) play ZZT and SuperZZT
  games, and can edit both ZZT and SuperZZT games.

## Freeze 2

Freeze 2 is an advanced ZZT world editor. It intends to provide fine-grained control over
the world file. It supports directly manipulating the stats list (for example, to create
*clown car* objects or reordering stats), advanced manipulation of stats and OOP (supporting
adding any byte sequence into the OOP text), and more.

The editor supports several view modes to quickly understand ZZT worlds - including modes
for walkability, presence or order of stats or OOP, monochrome palette, "hidden" empty colors,
SuperZZT maximum possible visible window, and more.

At this time Freeze 2 does not support any gameplay. Freeze 2 is also useful for
extracting text and data from ZZT worlds, and for programmatically generating
ZZT worlds.

To run:
1. Run `mvn clean install` to build the application.
1. Run `java -jar freeze2/target/freeze2-1.0-zztsearch-SNAPSHOT.jar`

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

## Freeze 1

The Freeze 1 engine loads and plays ZZT games, but due to differences in program
design (mainly having to do with not preserving stats ordering), it is not
capable of correctly playing a number of ZZT worlds. The engine code here is
now many years old and is unlikely to receive updates.

Playing ZZT games using Freeze may not be very interesting due to the
[Reconstruction of ZZT](https://github.com/asiekierka/reconstruction-of-zzt) now being
available.

# License

Licensed under the [GNU GPL version 3](./LICENSE.txt).

Copyright 2011, 2020, 2022 Isaac Brodsky.

# Thanks

In addition to being inspired by Tim Sweeney's ZZT, I was helped by the
[Mystical Winds Encyclopedia](https://museumofzzt.com/file/m/Mwencv14.zip)
and its description of the internals of ZZT.

This project incorporates parts of the [Reconstruction of ZZT](https://github.com/asiekierka/reconstruction-of-zzt)
and the [Reconstruction of Super ZZT](https://github.com/asiekierka/reconstruction-of-super-zzt), used under the
terms of the MIT License.

Copyright (c) 2020 Adrian Siekierka.

Based on a reconstruction of code from ZZT,
Copyright 1991 Epic MegaGames, used with permission.
