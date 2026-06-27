import os
from pathlib import Path
import re

def replace_preserve_case(text):
    # ArchiveTune -> Solstice
    text = text.replace("ArchiveTune", "Solstice")
    # archivetune -> solstice
    text = text.replace("archivetune", "solstice")
    # ARCHIVETUNE -> SOLSTICE
    text = text.replace("ARCHIVETUNE", "SOLSTICE")
    
    # Also handle Archive tune -> Solstice
    text = text.replace("Archive Tune", "Solstice")
    text = text.replace("archive tune", "solstice")
    
    return text

def rename_app_name():
    count = 0
    for path in Path('.').rglob('*'):
        if not path.is_file():
            continue
            
        if ".git" in path.parts or "build" in path.parts or ".gradle" in path.parts:
            continue
            
        if path.suffix not in ['.kt', '.xml', '.kts', '.pro', '.properties', '.gradle', '.md', '.json', '.txt']:
            continue
            
        try:
            content = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
            
        # Don't replace in our header script or the header itself to avoid corrupting the original attribution
        if "Based on ArchiveTune (2026)" in content:
            # We want to preserve this exact string, so we'll temporarily replace it with a token
            token = "@@BASED_ON_ARCHIVETUNE_TOKEN@@"
            content = content.replace("Based on ArchiveTune (2026)", token)
            
            new_content = replace_preserve_case(content)
            
            new_content = new_content.replace(token, "Based on ArchiveTune (2026)")
        else:
            new_content = replace_preserve_case(content)
            
        if content != new_content:
            path.write_text(new_content, encoding="utf-8")
            count += 1
            
    print(f"Updated {count} files with new app name 'Solstice'")

if __name__ == "__main__":
    rename_app_name()
