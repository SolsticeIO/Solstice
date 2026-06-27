import os
import re
import json
from pathlib import Path

def main():
    with open('icon_mapping.json', 'r') as f:
        mapping = json.load(f)

    # Filter out obvious non-icons
    ignore = ['R.drawable.about_appbar', 'R.drawable.about_splash', 'R.drawable.app_icon_small',
              'R.drawable.animation', 'R.drawable.anime_blank', 'R.drawable.ic_dialog_info']

    kt_files = list(Path('app/src/main/kotlin/urstark/solstice').rglob('*.kt'))

    replacements_made = 0
    for path in kt_files:
        content = path.read_text(encoding='utf-8')
        new_content = content
        
        needs_solar_import = False

        for old, new_solar in mapping.items():
            if not new_solar or old in ignore:
                continue
                
            if old.startswith('R.drawable.'):
                pattern = r'\b' + re.escape(old) + r'\b'
                new_content = re.sub(pattern, f'R.drawable.{new_solar}', new_content)
                
            elif old.startswith('Icons.'):
                kt_name = old.split('.')[-1]
                pattern = r'\b' + re.escape(old) + r'\b'
                if re.search(pattern, new_content):
                    new_content = re.sub(pattern, f'SolarIcons.{kt_name}', new_content)
                    needs_solar_import = True
        
        if needs_solar_import and 'urstark.solstice.ui.icons.SolarIcons' not in new_content:
            # find package declaration and insert import
            new_content = re.sub(r'^(package urstark\.solstice.*?)$', r'\1\n\nimport urstark.solstice.ui.icons.SolarIcons', new_content, flags=re.MULTILINE)

        if new_content != content:
            path.write_text(new_content, encoding='utf-8')
            replacements_made += 1

    print(f"Updated {replacements_made} Kotlin files safely.")

if __name__ == '__main__':
    main()
