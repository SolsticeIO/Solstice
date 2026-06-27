import os
from pathlib import Path
import re

def check_paddings():
    settings_dir = Path("app/src/main/kotlin/urstark/solstice/ui/screens/settings")
    for path in settings_dir.rglob("*.kt"):
        content = path.read_text(encoding="utf-8")
        matches = re.findall(r'padding\(horizontal\s*=\s*\d+\.dp', content)
        if matches:
            for match in matches:
                print(f"{path.name}: {match}")

check_paddings()
