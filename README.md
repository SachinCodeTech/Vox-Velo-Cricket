# VoxVelo Cricket

VoxVelo is a modern Android application built with Kotlin and Jetpack Compose for measuring bowling speed, analyzing ball flight and pitch bounce, and tracking bowler performance over time.

## Key Features

- **Delivery Speed Analysis**:
  - Video import and camera recording with frame-accurate scrubbing (-5f, -1f, +1f, +5f).
  - Exact frame marker tagging for Release (start), Bounce (pitch point), and Arrival (stumps).
  - Motion candidate suggestion to propose release and arrival points.
  - Calibration controls for pitch distance (meters), bounce distance (meters), frame rate (FPS), and camera angle.
  - Physics-based flight calculation yielding speeds in both km/h and mph with confidence indicators.
  - Interactive Pitch Overlay visualizer displaying ball trajectory arcs to scale.
- **Session Management**:
  - Record, organize, and review bowling sessions by bowler.
  - Session speed charts, speed distribution histogram, and fatigue progression curves.
  - Complete delivery logs with timestamps and confidence scores.
- **AI Cricket Coach**:
  - On-device telemetry analysis flagging pace drop, rhythm consistency ratings, and suggested training drills.
- **Bowler Profiles & Academy Hub**:
  - Bowler profiles with bowling style (Fast, Medium, Spin), handedness, and team/academy assignment.
  - Career statistics and multi-session speed progression trend curves.
  - Squad-level speed statistics and roster breakdowns.
  - Head-to-head comparison matrix (Player vs Player or Session vs Session).
- **Data & Privacy**:
  - Offline-first local data persistence with Room Database.
  - JSON data export.

## Architecture & Tech Stack

- **Platform**: Android SDK 36, Kotlin 2.2.10, Gradle 9.3.1 (Kotlin DSL)
- **UI Toolkit**: Jetpack Compose, Material 3 Dark Sports Telemetry Theme
- **State Management**: Android ViewModel & Kotlin Coroutines StateFlow
- **Local Database**: Android Room Persistence Library with KSP
