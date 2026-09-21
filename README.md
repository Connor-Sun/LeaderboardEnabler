# LeaderboardEnabler

A [ModTheSpire](https://github.com/kiooeht/ModTheSpire) mod for *Slay the Spire* that lets you upload Steam leaderboard scores while playing with mods, with in-game configurable options.

The approach is inspired by [AchievementEnabler](https://steamcommunity.com/sharedfiles/filedetails/?id=1692554109): patch a single check in the base game, and expose the bypass as toggles via BaseMod's `ModPanel`.

## How it works

The base game's `GameOverScreen.canUploadLeaderboards()` is:

```java
return !Settings.isModded && !Settings.isTrial && !Settings.seedSet;
```

So if any of "modded / demo / seeded" is true, the score is not uploaded.

This mod replaces that method with a `@SpirePrefixPatch` and hands `isModded` and `seedSet` over to in-game config:

```java
boolean moddedOk = !Settings.isModded || LeaderboardEnabler.bypassIsModded(); // default true
boolean trialOk  = !Settings.isTrial;                                          // always enforced
boolean seedOk   = !Settings.seedSet  || LeaderboardEnabler.bypassSeedSet();   // default false
return moddedOk && trialOk && seedOk;
```

## In-game config

Open **Mods → LeaderboardEnabler → Config** in the game. Two toggles:

| Toggle | Meaning | Default |
|--------|---------|---------|
| Allow modded runs to upload | Allow modded runs to upload to the leaderboard | On |
| Allow seeded runs to upload | Allow seeded runs to upload to the leaderboard | Off |

Settings are persisted with ModTheSpire's `SpireConfig` into the game's `saves/` directory and take effect immediately (the check is evaluated in real time when a run ends).

## Project structure

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

## Building

Requires **JDK 8+** ([Eclipse Temurin 8](https://adoptium.net/) recommended) and **Maven**.

First make sure `<Steam.path>` in `pom.xml` points to your own Steam library (defaults to `D:/Games/SteamLibrary/steamapps`).

Then run from the project root:

```bash
mvn package
```

The jar is built to `target/LeaderboardEnabler.jar` and automatically copied to `SlayTheSpire/mods/` (create the `mods` directory first if it does not exist).

### Without Maven (javac + jar only)

```bash
# 1. Build output directory
mkdir -p out

# 2. Compile (adjust the JDK and jar paths; use ';' as the classpath separator on Windows)
#    Note: -g is required, otherwise ModTheSpire can't read parameter names and throws
#    "Unknown error acquiring parameter name"
javac -encoding UTF-8 -g -source 8 -target 8 \
  -cp "D:/Games/SteamLibrary/steamapps/common/SlayTheSpire/desktop-1.0.jar;D:/Games/SteamLibrary/steamapps/workshop/content/646570/1605060445/ModTheSpire.jar;D:/Games/SteamLibrary/steamapps/workshop/content/646570/1605833019/BaseMod.jar" \
  -d out \
  src/main/java/leaderboardenabler/LeaderboardEnabler.java \
  src/main/java/leaderboardenabler/patches/GameOverScreenPatches.java

# 3. Package into a jar (put resources at the jar root)
jar cf LeaderboardEnabler.jar -C out . -C src/main/resources .

# 4. Copy to the mods directory
cp LeaderboardEnabler.jar "D:/Games/SteamLibrary/steamapps/common/SlayTheSpire/mods/"
```

## Install & use

1. Make sure `SlayTheSpire/mods/LeaderboardEnabler.jar` exists.
2. Launch the game with ModTheSpire and enable **LeaderboardEnabler** (it depends on **BaseMod**, so enable that too).
3. Adjust the toggles in the in-game Mods → Config panel.
4. Play a run with "Play with mods" and check that the score uploads when it ends.

## Notes

- Depends on **BaseMod** (for the in-game config panel).
- Leaderboard uploads write to the **real Steam public leaderboards**. Using heavy mods to farm high scores may be unfair to other players — please use responsibly.

---

# LeaderboardEnabler（中文）

一个《杀戮尖塔》的 [ModTheSpire](https://github.com/kiooeht/ModTheSpire) mod：**让「带 mod 对局」也能上传 Steam 排行榜分数，并可在游戏内配置。**

思路参考 [AchievementEnabler](https://steamcommunity.com/sharedfiles/filedetails/?id=1692554109)：篡改原版一处判定，并用 BaseMod 的 `ModPanel` 做成游戏内可配置项。

## 原理

原版 `GameOverScreen.canUploadLeaderboards()` 的判定是：

```java
return !Settings.isModded && !Settings.isTrial && !Settings.seedSet;
```

也就是说，只要「带 mod / 试玩版 / 自定义种子」任意一项成立，就不上传排行榜。

本 mod 用 `@SpirePrefixPatch` 替换这个方法，把 `isModded` 和 `seedSet` 交给游戏内配置控制：

```java
boolean moddedOk = !Settings.isModded || LeaderboardEnabler.bypassIsModded(); // 默认 true
boolean trialOk  = !Settings.isTrial;                                          // 始终保留，不可绕过
boolean seedOk   = !Settings.seedSet  || LeaderboardEnabler.bypassSeedSet();   // 默认 false
return moddedOk && trialOk && seedOk;
```

## 游戏内配置

进入 **Mods → LeaderboardEnabler → Config**，有两个开关：

| 开关 | 含义 | 默认 |
|------|------|------|
| Allow modded runs to upload | 允许带 mod 对局上传 | 开 |
| Allow seeded runs to upload | 允许自定义种子对局上传 | 关 |

配置通过 ModTheSpire 的 `SpireConfig` 持久化到游戏 `saves/` 目录，改完立刻生效（上传判定在每次对局结束时实时读取）。

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

打包成功后，`target/LeaderboardEnabler.jar` 会被自动复制到 `SlayTheSpire/mods/`（如果 `mods` 目录不存在，先手动创建）。

### 不用 Maven（仅 javac + jar）

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

1. 确认 `SlayTheSpire/mods/LeaderboardEnabler.jar` 存在。
2. 用 ModTheSpire 启动游戏，勾选 **LeaderboardEnabler**（依赖 **BaseMod**，需一并勾选）。
3. 在游戏内 Mods → Config 面板调开关。
4. 用「Play with mods」开一局，结束后查看分数是否上传。

## 注意

- 依赖 **BaseMod**（提供游戏内配置面板）。
- 排行榜是**真实写入 Steam 公共排行榜**，用大量 mod 刷高分可能对其他玩家不公平，请理性使用。
