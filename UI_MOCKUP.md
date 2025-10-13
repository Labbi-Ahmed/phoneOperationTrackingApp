# UI Mockup - Phone Operation Tracking App

```
┌─────────────────────────────────────────────┐
│  ╔════════════════════════════════════════╗ │
│  ║     🌐 Network Monitor                 ║ │
│  ║  Track and analyze your network        ║ │
│  ║  activity                              ║ │
│  ╚════════════════════════════════════════╝ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │  Network Tracking                     │ │
│  │                                       │ │
│  │  [Tracking: Disabled]                 │ │
│  │   (red pill badge)                    │ │
│  │                                       │ │
│  │  ┌─────────────────────────────────┐  │ │
│  │  │  ▶  Start Tracking              │  │ │
│  │  └─────────────────────────────────┘  │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │  Auto Upload                          │ │
│  │                                       │ │
│  │  [Auto Upload: Disabled]              │ │
│  │   (red pill badge)                    │ │
│  │                                       │ │
│  │  ┌─────────────────────────────────┐  │ │
│  │  │  🕐  Start Scheduler             │  │ │
│  │  └─────────────────────────────────┘  │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │  Manual Upload                        │ │
│  │                                       │ │
│  │  Upload current logs to Google        │ │
│  │  Sheets immediately                   │ │
│  │                                       │ │
│  │  ┌─────────────────────────────────┐  │ │
│  │  │  ⬆  Upload Now                   │  │ │
│  │  └─────────────────────────────────┘  │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │  ℹ️ This app requires VPN permission   │ │
│  │  to monitor network traffic. Logs are │ │
│  │  stored locally and uploaded to       │ │
│  │  Google Sheets.                       │ │
│  └───────────────────────────────────────┘ │
│                                             │
└─────────────────────────────────────────────┘

When tracking is ENABLED:
┌───────────────────────────────────────┐
│  Network Tracking                     │
│                                       │
│  [Tracking: Enabled]                  │
│   (green pill badge)                  │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │  ⏹  Stop Tracking               │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
```

## Color Scheme

**Header (Gradient)**
```
┌─────────────────────────────────┐
│ #1E88E5 → #1565C0 (diagonal)   │
│ White text                      │
└─────────────────────────────────┘
```

**Status Badges**
```
Enabled:  ┌──────────────────┐
          │ #E8F5E9 (light   │
          │ green background)│
          │ #43A047 (text)   │
          └──────────────────┘

Disabled: ┌──────────────────┐
          │ #FFEBEE (light   │
          │ red background)  │
          │ #E53935 (text)   │
          └──────────────────┘
```

**Buttons**
```
Primary:    #1E88E5 (blue)   - Start Tracking
Secondary:  #43A047 (green)  - Start Scheduler
Accent:     #FB8C00 (orange) - Upload Now
```

**Cards**
```
Background: #FFFFFF (white)
Elevation:  4dp shadow
Radius:     12dp rounded corners
```

## Design Features

✅ Material Design 3 principles
✅ Elevated cards with shadows
✅ Gradient header
✅ Icon-enhanced buttons
✅ Color-coded status badges
✅ Responsive ScrollView layout
✅ Professional typography
✅ Improved spacing and padding
✅ High contrast for accessibility
✅ Touch-friendly 56dp button height
