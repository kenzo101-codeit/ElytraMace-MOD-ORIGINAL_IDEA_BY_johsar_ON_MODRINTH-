# ElytraMace-MOD(ORIGINAL_IDEA_BY_johsar_ON_MODRINTH)
## This is an multiloader mod version of johsar's elytra mace mod. make sure to visit the original idea at https://modrinth.com/mod/elytramace and https://github.com/sjavi4/ElytraMaceSmasher !

# ElytraMaceSmasher

A small client-side Minecraft mod that allows players to **use maces effectively while flying with an Elytra**. Originally created by **johsar** and ported / maintained by **Kenzo101 Studios**, this project supports **Fabric**, **Forge**, and **NeoForge** through a multiloader setup.

---

## ✨ Features

* Enables Elytra + Mace combat mechanics
* Fully **client-side** (no server installation required)
* Uses **Mixins** for clean and minimal behavior changes
* Multiloader project: Fabric, Forge, and NeoForge
* Compatible with **Minecraft 1.21+**

---

## 🧩 Supported Loaders

| Loader   | Status      | Notes                           |
| -------- | ----------- | ------------------------------- |
| Fabric   | ✅ Supported | Uses Fabric Loader + Fabric API |
| Forge    | ✅ Supported | JavaFML (Forge 51+)             |
| NeoForge | ✅ Supported | NeoForge 21+                    |

All loaders are **client-only** and safe to use on vanilla servers.

---

## 📦 Installation

### Fabric

1. Install **Fabric Loader**
2. Install **Fabric API**
3. Place the mod JAR into your `mods/` folder

### Forge

1. Install **Minecraft Forge (51+)**
2. Place the mod JAR into your `mods/` folder

### NeoForge

1. Install **NeoForge (21+)**
2. Place the mod JAR into your `mods/` folder

> ⚠️ This mod is **client-side only**. Servers do **not** need the mod installed.

---

## 🔧 Development Setup

This project uses a **multiloader Gradle setup**.

### Requirements

* Java 17+
* Gradle (wrapper included)

### Common Tasks

```bash
./gradlew build
./gradlew clean
```

### Project Structure

```
common/        # Shared code
fabric/        # Fabric-specific loader code
forge/         # Forge-specific loader code
neoforge/      # NeoForge-specific loader code
```

---

## 🧪 Mixins

Each loader has its own mixin configuration:

* Fabric: `elytramace.fabric.mixins.json`
* Forge: `elytramace.mixins.json`
* NeoForge: `elytramace.neoforge.mixins.json`

All mixins are **client-side only** and target client classes safely.

---

## 📜 License

**ARR (All Rights Reserved)**

This project is shared publicly for viewing and contribution, but redistribution or reuse may require permission from the authors.

---

## 🙏 Credits

* **johsar** — Original mod author
* **Kenzo101 Studios** — Multiloader port, maintenance, and improvements

---

## 🐛 Issues & Contributions

* Bug reports: [GitHub Issues](https://github.com/sjavi4/ElytraMaceSmasher/issues)
* Pull requests are welcome

Please include:

* Minecraft version
* Loader (Fabric / Forge / NeoForge)
* Logs if applicable

---

## 📸 Media

Screenshots and videos coming soon.

---

Happy Elytra smashing 🦅⚔️
