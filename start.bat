@echo off
chcp 65001 >nul
title 高校学生社团管理系统
cd /d %~dp0
echo ============================================
echo   高校学生社团管理系统 (SpringBoot + Vue)
echo   服务地址: http://localhost:8080
echo ============================================
echo 正在启动服务，请稍候...
start "club-server" /min java -jar club-server\target\club-server-1.0.0.jar
:wait
timeout /t 2 /nobreak >nul
curl -s -o nul "http://localhost:8080/api/clubs?page=1&size=1"
if errorlevel 1 goto wait
start "" http://localhost:8080
echo 启动完成，浏览器已自动打开 http://localhost:8080
echo 关闭本窗口不会停止服务，如需停止请关闭 "club-server" 窗口
pause