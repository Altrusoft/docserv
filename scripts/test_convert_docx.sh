#! /bin/bash

# Copyright (c) 2013 Altrusoft AB.
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

TESTFILE="../test/docx/test.docx"
HOST="http://localhost:9000/convert"
cd $(dirname $0)
curl -w "HTTP Status: %{http_code}\n" -H "Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document" -H "Accept: application/pdf" --request POST --data-binary @$TESTFILE $HOST > output_test_conveert_docx.pdf
