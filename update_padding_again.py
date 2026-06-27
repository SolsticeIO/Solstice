import os
from pathlib import Path
import re

def update_paddings():
    settings_dir = Path("app/src/main/kotlin/urstark/solstice/ui/screens/settings")
    for path in settings_dir.rglob("*.kt"):
        content = path.read_text(encoding="utf-8")
        
        # Replace padding(16.dp) in SettingsComponents.kt to padding(horizontal = 8.dp, vertical = 16.dp)
        if path.name == "SettingsComponents.kt":
            content = content.replace("padding(16.dp)", "padding(horizontal = 8.dp, vertical = 16.dp)")

        # Replace horizontal paddings > 8.dp to 8.dp
        content = re.sub(r'padding\(horizontal\s*=\s*(1[0-9]|2[0-9])\.dp', r'padding(horizontal = 8.dp', content)
        
        # In AccountSettings.kt, there might be start/end paddings
        content = re.sub(r'padding\(start\s*=\s*(1[0-9]|2[0-9])\.dp,\s*end\s*=\s*(1[0-9]|2[0-9])\.dp', r'padding(start = 8.dp, end = 8.dp', content)

        # ContentPadding values
        content = re.sub(r'PaddingValues\(horizontal\s*=\s*(1[0-9]|2[0-9])\.dp', r'PaddingValues(horizontal = 8.dp', content)

        if content != path.read_text(encoding="utf-8"):
            path.write_text(content, encoding="utf-8")
            print(f"Updated padding in {path.name}")

update_paddings()
