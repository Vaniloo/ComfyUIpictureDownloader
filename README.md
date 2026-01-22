# ComfyUI Picture Downloader

一个使用 **Java** 编写的工具，用于自动从 **ComfyUI** 服务中轮询生成历史，并下载最新生成的图片到本地。

该项目主要用于练习与实践：
- Java IO / 网络 IO
- HTTP 通信（HttpURLConnection）
- JSON 解析（Gson）
- 轮询与去重逻辑
- 项目结构化与 Git 工程流程

---

## 功能特性

- 从 ComfyUI `/history` 接口获取生成记录
- 自动识别**最新 prompt** 对应的输出图片
- 支持**一次生成多张图片**
- 轮询检测新生成结果，避免重复下载
- 使用 Java 原生 `HttpURLConnection` 实现网络通信

---

## 项目结构

```text
src/main/java/com/imagedownload
├── App.java                 # 程序入口
├── comfy
│   └── ComfyClient.java     # ComfyUI HTTP 客户端（GET history / 下载图片）
├── model
│   └── ImageInfo.java       # 图片信息数据模型
├── parser
│   └── HistoryParser.java   # history JSON 解析逻辑
└── service
    └── PollingService.java  # 轮询服务，控制下载流程
```
## 运行环境
	•	Java 17+
	•	Maven
	•	已运行的 ComfyUI 服务（本地或远程）
## 示例地址：http://127.0.0.1:8188
## 使用方式
	1.	启动 ComfyUI（本地或远程）
	2.	在 App.java 中配置：
	   •	baseUrl
	   •	轮询间隔
	   •	下载目录
	3.	构建并运行：mvn clean package
    java -jar target/ComfyUIpictureDownloader.jar
    程序会自动轮询 ComfyUI 的生成历史，并下载最新生成的图片。
## 技术说明
	•	网络通信基于 HTTP（底层 TCP）
	•	使用轮询方式检测生成完成（未使用 WebSocket）
	•	使用 Gson 解析 ComfyUI 返回的 JSON 结构
	•	仅下载最新 prompt 对应的输出图片
## 可扩展方向
	•	支持 POST /prompt，从代码直接提交生成任务
	•	使用 WebSocket 替代轮询，提高实时性
	•	将配置抽离为配置文件或命令行参数
	•	支持多 prompt 并发监听
	•	封装为 CLI 工具
## 说明

   本项目仅用于学习与实验，不包含任何 ComfyUI 核心代码。
