cmake -DBUILD_SHARED_LIBS=OFF -G "Visual Studio 17" -DCMAKE_BUILD_TYPE=Release -DWITH_PROTOBUF=OFF -DBUILD_opencv_python3=OFF ..
cmake --build . --config Release
cmake --build . --target install --config Release