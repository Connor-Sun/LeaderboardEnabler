# LeaderboardEnabler

《杀戮尖塔》(Slay the Spire) 的 ModTheSpire mod：**让“带 mod 对局”(Play with mods) 也能上传 Steam 排行榜分数，并且可以在游戏内配置。**

实现思路参考 [AchievementEnabler](https://steamcommunity.com/sharedfiles/filedetails/?id=1692554109)：用 `@SpirePatch2` 篡改原版一处判定，同时用 BaseMod 的 `ModPanel` 做成游戏内可配置项。

## 原理

原版 `GameOverScreen.canUploadLeaderboards()` 的源码逻辑是：

```java
return !Settings.isModded && !Settings.isTrial && !Settings.seedSet;
```

也就是说，只要满足「带 mod / 试玩版 / 自定义种子」任意一项，就不上传排行榜。

本 mod 用 `@SpirePrefixPatch` 替换这个方法，把 `isModded` 和 `seedSet` 两项交给游戏内开关控制：

```java
boolean moddedOk = !Settings.isModded || LeaderboardEnabler.bypassIsModded(); // 默认 true
boolean trialOk  = !Settings.isTrial;                                          // 始终保留，不可绕过
boolean seedOk   = !Settings.seedSet  || LeaderboardEnabler.bypassSeedSet();   // 默认 false
return moddedOk && trialOk && seedOk;
```

## 游戏内配置

进入 **Mods 菜单 → LeaderboardEnabler → Config**，有两个开关：

| 开关 | 含义 | 默认 |
|------|------|------|
| Allow modded runs to upload | 允许带 mod 对局上传 | 开 |
| Allow seeded runs to upload | 允许自定义种子对局上传 | 关 |

配置通过 ModTheSpire 的 `SpireConfig` 持久化，存到游戏 `saves/` 目录，改完立刻生效（上传判定在每次对局结束时实时读取）。

## 目录结构

```
LeaderboardEnabler/
├── pom.xml
├── README.md
└── src/main/
    ├── java/leaderboardenabler/
    │   ├── LeaderboardEnabler.java
    │   └── patches/GameOverScreenPatches.java
    └── resources/
        ├── ModTheSpire.json
        └── leaderboardenabler/badge.png
```

## 构建

需要 **JDK 8+**（推荐 [Eclipse Temurin 8](https://adoptium.net/)）和 **Maven**。

先确认 `pom.xml` 里的 `<Steam.path>` 指向你自己的 Steam 库（默认是 `D:/Games/SteamLibrary/steamapps`）。

然后在项目根目录执行：

```bash
mvn package
```

打包成功后，`target/LeaderboardEnabler.jar` 会被自动复制到
`SlayTheSpire/mods/LeaderboardEnabler.jar`（如果 `mods` 目录不存在，先手动创建它）。

### 不用 Maven 的替代方式（仅 javac + jar）

```bash
# 1. 建输出目录
mkdir -p out

# 2. 编译（把 JDK 路径、jar 路径换成你自己的；Windows 用分号分隔 classpath）
#    注意：一定要带 -g，否则 ModTheSpire 读不到参数名会报
#    "Unknown error acquiring parameter name"
javac -encoding UTF-8 -g -source 8 -target 8 \
  -cp "D:/Games/SteamLibrary/steamapps/common/SlayTheSpire/desktop-1.0.jar;D:/Games/SteamLibrary/steamapps/workshop/content/646570/1605060445/ModTheSpire.jar;D:/Games/SteamLibrary/steamapps/workshop/content/646570/1605833019/BaseMod.jar" \
  -d out \
  src/main/java/leaderboardenabler/LeaderboardEnabler.java \
  src/main/java/leaderboardenabler/patches/GameOverScreenPatches.java

# 3. 打包成 jar（把 resources 一并放进根目录）
jar cf LeaderboardEnabler.jar -C out . -C src/main/resources .

# 4. 复制到 mods 目录
cp LeaderboardEnabler.jar "D:/Games/SteamLibrary/steamapps/common/SlayTheSpire/mods/"
```

## 安装 & 使用

1. 确认 `SlayTheSpire/mods/` 目录下已经有 `LeaderboardEnabler.jar`。
2. 用 ModTheSpire 启动游戏，在 mod 列表勾选 **LeaderboardEnabler**（依赖 BaseMod，需一并勾选）。
3. 进入 Mods 菜单的 Config 面板调开关。
4. 正常用「Play with mods」开一局，结束后即可看到分数上传。

## 注意

- 依赖 **BaseMod**（提供游戏内配置面板）。
- 排行榜上传是**真实写入 Steam 公共排行榜**，用大量 mod 刷高分可能对其他玩家不公平，请理性使用。
