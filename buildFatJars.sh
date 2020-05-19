#!/bin/bash

dsl_jar_location="cz.cuni.mff.fitoptivis.ide/build/libs/cz.cuni.mff.fitoptivis.ide-1.0.0-SNAPSHOT-ls.jar"
modelExtractor_jar_location="cz.cuni.mff.fitoptivis.modelExtractor/build/libs/cz.cuni.mff.fitoptivis.modelExtractor-1.0.0.jar"

set -e

# move to the root folder of the project (the script's position)
cd `dirname "$0"`

cd "cz.cuni.mff.fitoptivis.parent"

./gradlew shadowJar
cp "$dsl_jar_location" "../cz.cuni.mff.fitoptivis.dsl.jar"
cp "$modelExtractor_jar_location" "../cz.cuni.mff.fitoptivis.modelExtractor.jar"

echo "Fat jars compiled and copied to the project's root directory"
