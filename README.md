# 奶蛙 🐸

一个简洁的 Android 视频循环播放应用，使用 Jetpack Compose 构建。

## 功能特性

- 🎬 视频循环播放
- 📱 全屏沉浸式体验
- 🌙 深色主题界面
- 🔘 简洁的一键控制

## 截图

应用界面简洁，点击按钮即可开始/停止视频循环播放。

## 技术栈

- **Kotlin** - 主要编程语言
- **Jetpack Compose** - 现代 UI 框架
- **Material 3** - UI 组件库
- **MediaPlayer** - 视频播放引擎

## 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 11+
- Android SDK (minSdk 24, targetSdk 36)
- Gradle 9.3.1

## 快速开始

### 克隆项目

```bash
git clone https://github.com/你的用户名/nailong.git
cd nailong
```

### 构建项目

```bash
./gradlew assembleDebug
```

### 安装到设备

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

或者直接在 Android Studio 中点击 Run 按钮。

## 自定义视频

1. 将你的视频文件命名为 `nl.mp4`
2. 放置在 `app/src/main/res/raw/` 目录下（文件名为 `nl`，无扩展名）
3. 重新构建项目

## 自定义图标

1. 准备一张 PNG 图片
2. 使用 Android Studio 的 Image Asset Studio 生成各分辨率的图标
   - 右键点击 `res` 目录 → New → Image Asset
   - 选择你的图片并生成图标

## 项目结构

```
app/
├── src/
│   └── main/
│       ├── java/com/example/nailong/
│       │   ├── MainActivity.kt          # 主活动
│       │   └── ui/theme/                # 主题配置
│       │       ├── Color.kt
│       │       ├── Theme.kt
│       │       └── Type.kt
│       └── res/
│           ├── raw/nl                   # 视频资源
│           ├── mipmap-*/                # 应用图标
│           └── values/                  # 字符串和主题
└── build.gradle.kts                     # 构建配置
```

## 构建类型

### Debug 版本

```bash
./gradlew assembleDebug
```

### Release 版本

```bash
./gradlew assembleRelease
```

## 开源协议

本项目采用 MIT 协议开源，详见 [LICENSE](LICENSE) 文件。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 致谢

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
