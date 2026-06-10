<div align="center">
    <h1>QingNote</h1>
    <p>一个用 Jetpack Compose 编写的笔记和任务管理应用。</p>
    <br/>
    <br/>
</div>

## ✨ 特征

**QingNote** 是一款 Android 轻量级笔记和任务管理应用。

### 笔记功能
- **隐私优先**：不需要申请任何权限，所有运行时数据都牢固地存储在您的本地数据库中，也可以上传到您自己的 WEBDAV 私有云。
- **简洁干净**：以 #标签(TAG) 为索引，支持图文混排来记录和整理您的突发灵感。
- **随时回顾**：支持日历视图、热力图和随机漫步等方式来回顾您的笔记。
- **代码开源**：所有代码都开源在 Github 上，您可以随时查看和协作开发。
- **免费使用**：完全免费享受所有功能，没有任何内容的费用。

### 任务功能

**QingNote** 的任务管理系统是一个功能完整的 GTD 工具，帮助你高效管理待办事项。

#### 基础任务管理
- **创建任务**：快速创建任务，支持设置标题、描述、优先级（高/中/低）、开始日期和截止日期
- **任务编辑**：点击任务即可编辑所有属性，支持滑动操作完成/删除
- **批量操作**：长按选中多个任务，支持批量移动、设置日期、优先级和标签

#### 重复任务系统
- **内置重复周期**：每天、每周、每月，一键设置
- **自定义重复**：支持复杂的重复规则，如：
  - 每隔 N 天/周/月/年
  - 每周的特定几天（如周一、周三、周五）
  - 每月的第 N 天或第 N 个星期几
  - 设置结束日期或重复次数

#### 提醒系统
- **多提醒支持**：每个任务可设置多个提醒
- **灵活时间选项**：
  - 开始时提醒
  - 开始前 10 分钟/1 小时/1 天
  - 到期时提醒
  - 到期前 10 分钟/1 小时/1 天
  - 逾期后每天提醒
  - 自定义具体时间
- **随机提醒**：在指定时间范围内随机触发，避免习惯性忽视

#### 子任务
- **任务分解**：将复杂任务拆分为可管理的子任务
- **进度跟踪**：显示子任务完成进度（如 3/5 已完成）
- **独立管理**：子任务可独立添加、删除、排序

#### 附件管理
- **文件附件**：为任务添加任意文件作为附件
- **快速访问**：点击附件即可打开文件
- **批量管理**：支持添加、删除多个附件

#### 日历集成
- **系统日历同步**：将任务同步到 Android 系统日历
- **日历事件**：自动创建日历事件，包含任务详情
- **双向同步**：在日历中修改也能同步回应用

#### 分组与排序
- **多种分组模式**：
  - 按截止日期：逾期、今天、稍后、无截止日期
  - 按优先级：高、中、低、无
  - 按开始日期：已开始、今天、未开始、无开始日期
  - 按清单：按标签分组
  - 不分组
- **智能排序**：在每个分组内按优先级、日期等排序

#### 国际化
- 支持英文、简体中文和繁体中文
- 所有界面元素均已本地化

## 🤝 贡献指南
欢迎贡献！请按照以下步骤进行贡献：
1. Fork 本仓库。
2. 创建一个新分支（git checkout -b feature-branch-name）。
3. 提交你的修改（git commit -am 'Add some feature'）。
4. 推送到分支（git push origin feature-branch-name）。
5. 创建一个 Pull Request。

## 🤗 致敬

本项目基于 **ldlywt** 开发的 [IdeaMemo](https://github.com/ldlywt/IdeaMemo) 创建。任务管理功能的设计灵感来源于 **Alex Baker** 开发的 [Tasks.org](https://github.com/tasks/tasks)。

特别感谢：
- **ldlywt** - IdeaMemo 的创建者，QingNote 所构建的原始笔记应用
- **Alex Baker** - Tasks.org 的创建者，其任务管理架构和设计启发了 QingNote 的任务功能

项目部分代码源自优秀的开源项目：

- [ReadYou](https://github.com/Ashinch/ReadYou)
- [MoeMemosAndroid](https://github.com/mudkipme/MoeMemosAndroid)
- [Animius](https://github.com/lanlinju/Animius)
- [SaltUI](https://github.com/Moriafly/SaltUI)
- [memos](https://github.com/usememos/memos)

## 🧾 许可证
GNU GPL v3.0 © [QingNote](https://github.com/sun-SunnyRay/Qingnote/blob/main/LICENSE)

您的 star 是我最大的动力！ **🌟**

> 开发不易，头发渐稀。如果这个项目对你有帮助，请给个 star 吧！你的每一个 star 都是对开发者最大的鼓励，让我有动力继续熬夜写代码（不是）。

## ☕️ 捐助
可以给我买一杯咖啡，让我更有动力继续开发。

## ⭐ Star History

[![Star History Chart](https://api.star-history.com/svg?repos=sun-SunnyRay/Qingnote&type=Date)](https://star-history.com/#sun-SunnyRay/Qingnote&Date)
