> 本工程是基于FFmpeg 6.0，在工程里面也有一个3.3.2, 3.4.8, 4.2.3, 5.1.1版本，有需要的可以自己去Git库里面checkout，工程17年就做好了，一直没有开源，原谅我懒，哈哈哈

#### 如何使用:

1. 在allprojects下面添加:  maven { url "https://www.raoyunsoft.com/nexus/repository/maven-releases/" }
2. implementation 'com.guaishou.bzlib:bzffmpegcmd:1.0.20@aar'
3. FFmpegCMDUtil.executeFFmpegCommand(String command, OnActionListener onActionListener)



English documentation：

> This project is based on FFmpeg 6.0. There is also a version 3.3.2, 3.4.8 4.2.3 and 5.1.1 in the project. If you need it, you can checkout it in the Git library.



#### How to use:

1. Add below allprojects:  maven { url "https://www.raoyunsoft.com/nexus/repository/maven-releases/" }
2. implementation 'com.guaishou.bzlib:bzffmpegcmd:1.0.20@aar'
3. FFmpegCMDUtil.executeFFmpegCommand(String command, OnActionListener onActionListener)

