# 涂鸦智能摄像机 Android SDK

## 功能概述

涂鸦智能摄像头 SDK 提供了与远端摄像头设备通讯的接口封装，加速应用开发过程，主要包括了以下功能：

- 预览摄像头实时采集的图像。
- 播放摄像头SD卡中录制的视频。
- 手机端录制摄像头采集的图像。
- 与摄像头设备通话。
- 支持云存储

## 快速集成

**使用 AndroidStudio (版本号 3.1.3及更高版本)**

- 创建项目工程
- 在根目录build.gradle添加maven地址：

```groovy
buildscript {
    repositories {
        ...
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:4.0.1'
    }
}

allprojects {
    repositories {
        ...
        jcenter()
    }
}
```

- 在模块的build.gradle中添加如下代码:

```groovy
defaultConfig {
    ndk {
       abiFilters "armeabi-v7a","arm64-v8a"
    }
}   

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])
    implementation 'com.alibaba:fastjson:1.1.67.android'
    implementation 'com.squareup.okhttp3:okhttp-urlconnection:3.12.12'
    
    // required tuya home sdk
    implementation 'com.tuya.smart:tuyasmart:3.20.0-beta1'

    // tuya camera module
    implementation 'com.tuya.smart:tuyasmart-ipc-camera-middleware:3.20.0'
    implementation 'com.tuya.smart:tuyasmart-ipc-camera-v2:3.20.0'
    implementation 'com.tuya.smart:tuyasmart-ipc-camera-utils:3.20.0'
    implementation 'com.tuya.smart:tuyasmart-ipc-camera-diff-api:3.20.0'
    implementation 'com.tuya.smart:tuyasmart-ipc-camera-outside:3.20.0'
    implementation 'com.tuya.smart:tuyasmart-base-utils:3.18.0r143-rc.9'
    // 可选，消息中心、设备控制
    implementation 'com.tuya.smart:tuyasmart-ipc-camera-message:3.13.0r128'
    implementation 'com.tuya.smart:tuyasmart-ipc-devicecontrol:3.17.0r139'

    // 图片解密组件
    implementation 'com.tuya.smart:tuyasmart-imagepipeline-okhttp3:0.0.1'
    implementation 'com.facebook.fresco:fresco:2.2.0'
}
```

AndroidStudio的使用请参考: [AndroidStudio Guides](https://developer.android.com/studio/)


## 开发文档

更多请参考: [涂鸦智能摄像机 Android SDK使用说明](https://tuyainc.github.io/tuyasmart_home_android_sdk_doc/zh-hans/resource/ipc/)

## 更新日志
- 2020.11.10
    - 更新 sdk（版本 3.20.0）
        - 废弃 `ICameraP2P`，推荐使用 `ITuyaSmartCameraP2P`，简化视频直播流程
        - 新增图片解密相关文档
        - 音视频稳定性提升
- 2020.5.20
   - 更新 sdk，使用新的播放器用于支持老设备（tutk）
   - 更新优化 demo
- 2020.5.9
   - 更新 sdk（版本 3.17.0r139），修复音频问题（切换清晰度声音关闭），so崩溃问题，提升sdk稳定性；增加设备所有dp点操作的上报回调
   - 更新优化 demo
- 2020.3.31
    - 更新底层库，修复armabi消息中心视频播放问题
- 2020.3.4
    - 更新sdk（版本 3.15.0r135），消息中心多媒体预览（云存储视频播放）
- 2019.11.15
    - 更新sdk(版本 3.13.0r129)，对应的ffmpeg是4.1.4版本
    - 更新sdk demo
- 2019.10.8
    - 更新sdk（版本 3.12.6r125）
- 2019.8.1
    - 支持云存储功能 （版本 3.11.1r119）
- 2019.7.13
    -  新的sdk代码重构，接口方法有变更，为了兼容老版本sdk请使用tuyaCamera:3.11.0r119h2。建议老用户向上升级
- 2019.6.11
    - 支持arm64

