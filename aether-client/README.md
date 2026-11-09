# AETHER CLIENT

A standalone Fabric client project targeting Minecraft **1.21.11**.

## Current modules

- **Sprint Assist** — holds sprint while the forward key is held.
- **Stash Radar** — incrementally scans nearby, already-loaded client terrain for chests, barrels, hoppers, shulker boxes, ender chests and furnaces.
- **Base Surveyor** — scores nearby loaded chunks using storage, utility-block and obsidian/spawner signals. Scores are heuristic leads, not proof of a player base.
- **ClickGUI** — dark, compact module panel. Open with **Right Shift**.

## Important scanning limits

Minecraft's client does not have authoritative access to arbitrary unloaded chunks or hidden server-side inventories. Both scanners inspect block states available to the local client, skip positions without a loaded chunk, and do not query or bypass server protections. Results can contain false positives; inspect coordinates in-game.

## Requirements

- JDK 21
- Minecraft 1.21.11
- Fabric Loader 0.18.1 or newer compatible 1.21.11 loader
- Fabric API 0.141.6+1.21.11

## Build

If Gradle is installed:

```bash
gradle build
```

Or generate the Gradle wrapper once, then use it:

```bash
gradle wrapper --gradle-version 8.14.3
./gradlew build
```

Windows:

```powershell
gradle wrapper --gradle-version 8.14.3
.\gradlew.bat build
```

The remapped mod jar is written to `build/libs/`.

## Install

Copy the non-`-sources` jar from `build/libs/` into your Minecraft `mods` folder alongside Fabric API. Launch the Fabric 1.21.11 profile.

## GitHub Actions

The included workflow builds on every push and pull request. Download the `aether-client-jar` artifact from the workflow run.

## Project layout

```text
src/main/java/dev/aether/client/
  AetherClient.java
  gui/ClickGuiScreen.java
  module/Module.java
  module/ModuleManager.java
  scan/BaseSurveyor.java
  scan/StashRadar.java
  scan/ScanResult.java
src/main/resources/fabric.mod.json
.github/workflows/build.yml
```

## Development status

This is a first functional foundation, not a claim of a fully verified release binary. Minecraft 1.21.11 APIs can change across mappings and Fabric toolchain revisions. Run the Gradle build and test in a disposable world before using it in a live environment. Contributions should include a successful CI build and reproduction steps for bugs.
