import os
from pathlib import Path

def update_paddings():
    # Update SettingsDimensions.kt
    dim_path = Path("app/src/main/kotlin/urstark/solstice/ui/screens/settings/SettingsDimensions.kt")
    if dim_path.exists():
        content = dim_path.read_text(encoding="utf-8")
        content = content.replace("val ScreenHorizontalPadding = 16.dp", "val ScreenHorizontalPadding = 8.dp")
        content = content.replace("val RowHorizontalPadding = 16.dp", "val RowHorizontalPadding = 8.dp")
        content = content.replace("val SectionHeaderHorizontalPadding = 20.dp", "val SectionHeaderHorizontalPadding = 12.dp")
        dim_path.write_text(content, encoding="utf-8")
        print("Updated SettingsDimensions.kt")

    # Change 26.dp padding to 12.dp across settings screens
    settings_dir = Path("app/src/main/kotlin/urstark/solstice/ui/screens/settings")
    for path in settings_dir.rglob("*.kt"):
        content = path.read_text(encoding="utf-8")
        new_content = content.replace("padding(horizontal = 26.dp)", "padding(horizontal = 12.dp)")
        new_content = new_content.replace("padding(start = 26.dp, end = 26.dp", "padding(start = 12.dp, end = 12.dp")
        new_content = new_content.replace("padding(horizontal = 24.dp", "padding(horizontal = 12.dp")
        new_content = new_content.replace("padding(horizontal = 20.dp", "padding(horizontal = 12.dp")
        
        if content != new_content:
            path.write_text(new_content, encoding="utf-8")
            print(f"Updated padding in {path.name}")

update_paddings()
