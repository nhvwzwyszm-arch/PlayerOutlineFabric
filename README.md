# Player Outline — Fabric 1.21.4

Client-side Fabric mod project that gives other players a red outline only while a block raycast can see at least one of their head/torso/leg sample points.

## Build

Requirements: JDK 21 and internet access for Gradle/Minecraft dependencies.

From this folder run:

```bash
gradle build
```

The built mod should appear at `build/libs/player-outline-1.0.0.jar`.

## Install

1. Install Fabric Loader for Minecraft Java Edition 1.21.4.
2. Install a matching Fabric API build.
3. Copy `player-outline-1.0.0.jar` into `.minecraft/mods`.
4. Launch the Fabric 1.21.4 profile.

## Notes

- This is a client-only visual mod; other players do not need to install it.
- The outline is enabled only when a raycast from the player's eyes reaches one of the tested points without hitting a block.
- This source project has not been compiled in this environment because Gradle is not installed and network access to download dependencies is unavailable. Build locally to produce the runnable JAR.
