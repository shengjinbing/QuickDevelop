1.启动优化相关
启动app的命令：adb shell am start -W -n com.modesty.quickdevelop/.ui.activitys.splash.SplashActivity（需启动的绝对路径）
ThisTime: 最后一个启动的Activity的启动耗时
TotalTime: 自己的所有Activity的启动耗时
WaitTime: ActivityManagerService启动App的Activity时的总时间（包括当前Activity的onPause()和自己Activity的启动）
一般查看得到的TotalTime，即应用的启动时间，包括创建进程 + Application初始化 + Activity初始化到界面显示的过程
