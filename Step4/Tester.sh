#!/bin/bash
java org.antlr.v4.Tool MeldrLang.g4
javac *.java
java Driver < $1