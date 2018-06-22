@echo off
cd C:\Users\Project/xman
echo 1 ------ pull ------------------------------------------
call autoBuild1.bat
echo 2 ------ clean ------------------------------------------
call autoBuild2.bat
echo 3 ------ build ------------------------------------------
call autoBuild3.bat
echo 4 ------ copy ------------------------------------------
xcopy app\build\outputs\apk\release\*.apk     C:\Users\App /y
echo 5 ------ end ------------------------------------------
pause