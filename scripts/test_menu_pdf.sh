#! /bin/bash

# Copyright (c) 2013 Altrusoft AB.
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

TESTFILE="../test/json/test_menu_fail.json"
HOST="http://localhost:9001/document/menu"
cd $(dirname $0)
curl -w "HTTP Status: %{http_code}\n" -H "Content-Type: application/json" -H "Accept: application/pdf" --data-binary @$TESTFILE $HOST > output_test_menue.pdf
