import os
import shutil
from pathlib import Path
import re

OLD_BASE_PKG = "moe.rukamori"
NEW_BASE_PKG = "urstark"

OLD_PKG = "moe.rukamori.archivetune"
NEW_PKG = "urstark.solstice"

RUKAMORI_HEADER_SNIPPET = "© Rukamori"
SOLSTICE_HEADER = """/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * Based on ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
"""

SOLSTICE_ONLY_HEADER = """/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
"""

def update_file_contents(path: Path):
    if ".git" in path.parts or "build" in path.parts:
        return

    # Only process text files
    if path.suffix not in ['.kt', '.xml', '.kts', '.pro', '.properties', '.gradle', '.json', '.md']:
        return

    try:
        content = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return

    new_content = content.replace(OLD_PKG, NEW_PKG).replace(OLD_BASE_PKG, NEW_BASE_PKG)

    # Handle Kotlin headers
    if path.suffix == '.kt':
        # Remove existing header if it exists
        has_rukamori = False
        has_stark = False
        text_without_header = new_content
        
        while text_without_header.startswith("/*"):
            end = text_without_header.find("*/")
            if end == -1:
                break
            header = text_without_header[:end + 2]
            if "GPL-3.0 License" in header:
                if "© Rukamori" in header:
                    has_rukamori = True
                if "© Stark" in header:
                    has_stark = True
                text_without_header = text_without_header[end + 2:].lstrip("\r\n")
            else:
                break
                
        # Now prepend the new header
        if has_rukamori or has_stark:
            if has_rukamori:
                new_content = SOLSTICE_HEADER + text_without_header
            else:
                new_content = SOLSTICE_ONLY_HEADER + text_without_header
        else:
            new_content = SOLSTICE_ONLY_HEADER + text_without_header

    if content != new_content:
        path.write_text(new_content, encoding="utf-8")

def move_directories():
    # Find all 'moe/rukamori/archivetune' directories and move them
    for path in list(Path('.').rglob('moe/rukamori/archivetune')):
        if not path.is_dir() or ".git" in path.parts or "build" in path.parts:
            continue
            
        new_dir = path.parent.parent.parent / "urstark" / "solstice"
        new_dir.mkdir(parents=True, exist_ok=True)
        
        for item in path.iterdir():
            shutil.move(str(item), str(new_dir / item.name))
            
        # Clean up empty directories
        shutil.rmtree(path)
        
        # Try to clean up parent if empty
        try:
            path.parent.rmdir()
            path.parent.parent.rmdir()
        except OSError:
            pass

    # Find remaining 'moe/rukamori' directories and move them (for things outside archivetune)
    for path in list(Path('.').rglob('moe/rukamori')):
        if not path.is_dir() or ".git" in path.parts or "build" in path.parts:
            continue
            
        new_dir = path.parent.parent / "urstark"
        new_dir.mkdir(parents=True, exist_ok=True)
        
        for item in path.iterdir():
            shutil.move(str(item), str(new_dir / item.name))
            
        # Clean up empty directories
        shutil.rmtree(path)
        
        try:
            path.parent.rmdir()
        except OSError:
            pass

if __name__ == "__main__":
    print("Moving directories...")
    move_directories()
    
    print("Updating file contents...")
    for file_path in Path('.').rglob('*'):
        if file_path.is_file():
            update_file_contents(file_path)
            
    print("Done!")
