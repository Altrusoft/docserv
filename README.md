    Copyright (c) 2015 Altrusoft AB.
    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/.


Docserv
=======

Prerequisites
-------------
*    Libreoffice 4+ needs to be installed on the machine you're running Docserv from
*    Play!Framework 2.4

Download
--------
You can download the latest version of docserv using git, from `https://github.com/Altrusoft/docserv.git`

Configuration
-------------
The configuration can be found in the `conf` folder.
* `application.conf` contains configuration about logging, server address and port. 
* Local configuration should be placed in /etc/docserv/?.conf

Data/templates
--------------
Templates are stored in __/var/docserv/__ where each template has a directory __{uuid}__. In the template directory there should be a template definition file __template.json__, example:

<pre><code>
{
	"name": "Test template 1",
	"description": "Just a test",
	"author": "Altrusoft AB"
}
</code></pre>

and subdirectories

* __template__ containing the LibreOffice file containing FreeMarker/Velocity code
* __script__ containing the JavaScript file to evaluate
* __messages__ containing language files to localize data, __messages.{lang}__ e.g. __messages.sv__, __messages.en__

Running the application
-----------------------
Start the application with `play run`. See the `scripts` folder for examples on how to call the server.

