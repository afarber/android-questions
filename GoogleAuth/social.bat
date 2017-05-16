@CHCP 65001

@SET DATABASE=%TEMP%\social.db
@SET SCRIPT_DIR=%~dp0
@SET DESTINATION=%SCRIPT_DIR%\app\src\main\assets\databases\social.db.gz

echo "DOES NOT WORK YET"
exit

del %DATABASE%
echo ".read social.sql" | %USERPROFILE%\sqlite3.exe -echo %DATABASE%
"C:\Program Files\7-Zip\7z.exe" a -tgzip %DESTINATION% %DATABASE%

