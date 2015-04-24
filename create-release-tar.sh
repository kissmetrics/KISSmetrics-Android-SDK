#!/bin/bash

mkdir build/KISSmetricsSDK
pushd build/

cp libs/*.jar KISSmetricsSDK/
cp outputs/aar/*.aar KISSmetricsSDK/
tar cvfz KISSmetricsSDK.tar.bz KISSmetricsSDK

popd

