#!/bin/bash

jar_location="cz.cuni.mff.fitoptivis.ide/build/libs/cz.cuni.mff.fitoptivis.ide-1.0.0-SNAPSHOT-ls.jar"

set -e

# move to the root folder of the project (the script's position)
cd `dirname "$0"`

cd "cz.cuni.mff.fitoptivis.parent"

./gradlew shadowJar
cp "$jar_location" "../cz.cuni.mff.fitoptivis.dsl.jar"

echo "Fat jar compiled and copied to the project's root directory"
