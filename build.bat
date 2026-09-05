@echo off
chcp 65001 >nul
cd /d %~dp0
echo [1/3] 构建前端...
cd club-web
call npm run build || goto :err
echo [2/3] 拷贝前端产物到后端静态资源...
cd ..
if exist club-server\src\main\resources\static rmdir /s /q club-server\src\main\resources\static
xcopy club-web\dist club-server\src\main\resources\static /e /i /q >nul || goto :err
echo [3/3] 打包后端 jar...
cd club-server
call mvnw.cmd clean package -DskipTests -q || goto :err
echo.
echo 打包完成: club-server\target\club-server-1.0.0.jar
echo 运行 start.bat 启动系统
goto :eof
:err
echo 构建失败，请检查上方日志
exit /b 1