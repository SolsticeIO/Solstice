import os
from pathlib import Path

def replace_urls():
    total_updated = 0
    for path in Path('.').rglob('*'):
        if not path.is_file(): continue
        if ".git" in path.parts or "build" in path.parts or ".gradle" in path.parts: continue
        if path.suffix not in ['.kt', '.xml', '.kts', '.pro', '.properties', '.gradle', '.md', '.json', '.txt', '.yml']: continue
        
        try:
            content = path.read_text(encoding="utf-8")
        except:
            continue
            
        new_content = content.replace("github.com/SolsticeApp/Solstice", "github.com/SolsticeIO/Solstice")
        new_content = new_content.replace("github.com/SolsticeApp", "github.com/SolsticeIO")
        new_content = new_content.replace("t.me/SolsticeGC", "t.me/SolsticeIO")
        new_content = new_content.replace("t.me/ArchiveTuneGC", "t.me/SolsticeIO")
        new_content = new_content.replace("github.com/ArchiveTuneApp", "github.com/SolsticeIO")
        
        if content != new_content:
            path.write_text(new_content, encoding="utf-8")
            total_updated += 1
            print(f"Updated urls in {path}")
    print(f"Total files updated: {total_updated}")

replace_urls()
