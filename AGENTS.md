- 在查找代码的过程中，请充分利用本项目已经构建好的多层级 AGENTS.md 系统。在 Read 或 Edit 任何目录中有 AGENTS.md 的文件时，你都会自动 Read 一份该目录中的 AGENTS.md ，它会引导你了解本目录下的全部代码文件。在阅读多级目录的时候同理。请通过这种方式准确且精准地找到你需要找到的代码，而不是简单地完全依靠 Grep 和 Glob 等工具
- 当你完成了对应代码功能的更改后，需要立刻修改完善对应目录中的 AGENTS.md ，以确保项目记忆的准确

## Kotlin 到 Java 转换记录

### 已转换完成 ✅

**第一批：核心处理器类**
- `src/main/java/com/werfad/GlobalJumpHandler.java` - 全局跳转处理器 (已从 Kotlin 转换)
- `src/main/java/com/werfad/JumpHandler.java` - 跳转处理器 (已从 Kotlin 转换)

**第二批：UI和配置类**
- `src/main/java/com/werfad/UserConfig.java` - 用户配置 (已从 Kotlin 转换)
- `src/main/java/com/werfad/ConfigUI.java` - 配置界面 (已从 Kotlin 转换)
- `src/main/java/com/werfad/KJumpConfigurable.java` - KJump配置 (已从 Kotlin 转换)
- `src/main/java/com/werfad/Actions.java` - 动作类 (已从 Kotlin 转换)

**第三批：工具类**
- `src/main/java/com/werfad/utils/StringUtils.java` - 字符串工具 (已从 Kotlin 转换)
- `src/main/java/com/werfad/utils/ProjectUtils.java` - 项目工具 (已从 Kotlin 转换)
- `src/main/java/com/werfad/utils/EditorUtils.java` - 编辑器工具 (已从 Kotlin 转换)
- `src/main/java/com/werfad/KeyTagsGenerator.java` - 按键标签生成器 (已从 Kotlin 转换)

**第四批：标记和查找器**
- `src/main/java/com/werfad/MarksCanvas.java` - 标记画布 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/Finder.java` - 查找器接口 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/Char1Finder.java` - 单字符查找器 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/Char2Finder.java` - 双字符查找器 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/Word0Finder.java` - 单词查找器0 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/Word1Finder.java` - 单词查找器1 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/LineFinder.java` - 行查找器 (已从 Kotlin 转换)
- `src/main/java/com/werfad/finder/GlobalWord0Finder.java` - 全局单词查找器0 (已从 Kotlin 转换)

## 🎉 全部转换完成！

**总计转换 18/18 个文件 ✅**

所有 Kotlin 代码已成功转换为 Java 代码，包括：
- 核心处理器类 (2个)
- UI和配置类 (4个)
- 工具类 (4个)
- 标记和查找器类 (8个)