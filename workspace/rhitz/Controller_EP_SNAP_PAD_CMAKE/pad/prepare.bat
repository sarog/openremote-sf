cd %~dp0apr-1.4.6

nmake -f Makefile.win
nmake -f Makefile.win PREFIX=%~dp0apr-1.4.6\win install

if not exist %~dp0build\nul md %~dp0build  
cd %~dp0build

cmake -G "NMake Makefiles" ..

cd %~dp0 