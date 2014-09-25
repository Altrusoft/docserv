# Copyright (c) 2013 Altrusoft AB.
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

    Copyright (c) 2013 Altrusoft AB.
    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/.


Docserv
=======

Prerequisites
-------------
*    Libreoffice 4+ needs to be installed on the machine you're running Docserv from
*    Play!Framework 2.2.1

Download
--------
You can download the latest version of docserv using git, from `https://github.com/Altrusoft/docserv.git`

Configuration
-------------
The configuration can be found in the `conf` folder.
*     `application.conf` contains configuration about logging, server address and port. You can also configure a local configuration directory, in which you would then place your own `applicationContext.xml`.
*     `applicationContext.xml` contains information about which urls matches which templates.

You need to make soft links for the libreoffice jars.
On Ubuntu:

    mkdir lib
    cd lib
    ln -s /usr/lib/libreoffice/ure-link/share/java/juh.jar juh.jar 
    ln -s /usr/lib/libreoffice/ure-link/share/java/jurt.jar jurt.jar
    ln -s /usr/lib/libreoffice/ure-link/share/java/ridl.jar ridl.jar
    ln -s /usr/lib/libreoffice/program/classes/unoil.jar unoil.jar

On Ubuntu 14.04 the libreoffice java bindings does not work.
A fix:
    sudo ln -s /usr/lib/ure/lib/libjpipe.so /usr/lib/libjpipe.so



Running the application
-----------------------
Start the application with `play run`. See the `scripts` folder for examples on how to call the server.

