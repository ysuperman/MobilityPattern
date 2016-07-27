@ echo off
set /p beginDate=begin:
set /p endDate=end:
set Date=%beginDate%
:str
echo now:%Date%
java -Xms2048m -Xmx2048m -Xmn256m -XX:PermSize=128m -jar getGoodUser.jar
Java -Xms2048m -Xmx2048m -Xmn256m -XX:PermSize=128m -jar getGoodRecord.jar
java -Xms2048m -Xmx2048m -Xmn256m -XX:PermSize=128m -jar getStayRecord.jar
java -Xms2048m -Xmx2048m -Xmn256m -XX:PermSize=128m -jar Config.jar
set /a Date=%Date%+1
if %Date%  gtr %endDate% goto endd
goto str 
:endd
pause