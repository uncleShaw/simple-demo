git pull
git add .
set /p msg="请输入此次跟新日志："
git commit -m "%msg%"
git push
pause