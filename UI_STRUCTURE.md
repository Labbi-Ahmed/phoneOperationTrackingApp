# UI Visual Structure

## Layout Hierarchy (After Enhancement)

```
ScrollView (background: #FAFAFA)
└── LinearLayout (vertical)
    ├── Header Section (gradient: #1E88E5 → #1565C0)
    │   ├── Title: "Network Monitor" (28sp, bold, white)
    │   └── Subtitle: "Track and analyze..." (14sp, white)
    │
    ├── Content Section (padding: 16dp)
    │   ├── Network Tracking Card
    │   │   ├── Title: "Network Tracking" (20sp, bold)
    │   │   ├── Status Badge (enabled: green / disabled: red)
    │   │   └── Button with Icon (play/stop)
    │   │
    │   ├── Auto Upload Card
    │   │   ├── Title: "Auto Upload" (20sp, bold)
    │   │   ├── Status Badge (enabled: green / disabled: red)
    │   │   └── Button with Schedule Icon
    │   │
    │   ├── Manual Upload Card
    │   │   ├── Title: "Manual Upload" (20sp, bold)
    │   │   ├── Description text
    │   │   └── Button with Upload Icon
    │   │
    │   └── Info Card (light blue background)
    │       └── Information text with emoji icon
```

## Color Palette

### Primary Colors
- Primary: `#1E88E5` (Material Blue 600)
- Primary Dark: `#1565C0` (Material Blue 800)
- Primary Light: `#42A5F5` (Material Blue 400)

### Secondary Colors
- Secondary: `#43A047` (Material Green 600)
- Secondary Dark: `#2E7D32` (Material Green 800)
- Secondary Light: `#66BB6A` (Material Green 400)

### Accent Colors
- Accent: `#FB8C00` (Material Orange 600)
- Accent Dark: `#E65100` (Material Deep Orange 800)
- Accent Light: `#FFB74D` (Material Orange 300)

### Status Colors
- Success: `#43A047` (Green)
- Error: `#E53935` (Red)
- Warning: `#FB8C00` (Orange)
- Info: `#1E88E5` (Blue)

### Text & Background
- Text Primary: `#212121`
- Text Secondary: `#757575`
- Text Hint: `#BDBDBD`
- Background Light: `#FAFAFA`
- Background Card: `#FFFFFF`

## Component Specifications

### Header
- Background: Linear gradient (135°)
- Padding: 24dp all sides
- Elevation: 4dp
- Title size: 28sp, bold
- Subtitle size: 14sp, 90% opacity

### Cards (CardView)
- Corner radius: 12dp
- Elevation: 4dp
- Padding: 20dp
- Margin bottom: 16dp
- Background: White

### Status Badges
- Background: Light green (#E8F5E9) or light red (#FFEBEE)
- Corner radius: 16dp (pill shape)
- Padding: 12dp horizontal, 6dp vertical
- Text size: 16sp

### Buttons
- Height: 56dp
- Corner radius: 8dp
- Text size: 16sp, bold
- Icon size: 24dp
- Icon padding: 8dp

### Info Card
- Background: Light blue (#E3F2FD)
- Corner radius: 12dp
- Elevation: 2dp
- Text size: 13sp
- Line spacing: +2dp

## Responsive Features

1. **ScrollView**: Ensures content is accessible on all screen sizes
2. **Match Parent**: Buttons stretch to full card width
3. **Wrap Content**: Cards adapt to content
4. **Proper Padding**: 16-24dp for comfortable spacing
5. **Touch Targets**: Minimum 56dp height for buttons

## Accessibility

1. **High Contrast**: Text colors meet WCAG AA standards
2. **Large Touch Targets**: 56dp button height
3. **Clear Visual States**: Color-coded status badges
4. **Readable Text**: Minimum 13sp for body text
5. **Hierarchical Typography**: Clear size differences between titles and body text
