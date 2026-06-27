import os
import re
import json
from pathlib import Path

def main():
    with open('icon_mapping.json', 'r') as f:
        mapping = json.load(f)

    # Filter out obvious non-icons
    ignore = ['R.drawable.about_appbar', 'R.drawable.about_splash', 'R.drawable.app_icon_small',
              'R.drawable.animation', 'R.drawable.anime_blank']

    kt_files = list(Path('app/src/main/kotlin/urstark/solstice').rglob('*.kt'))

    replacements = 0
    for path in kt_files:
        content = path.read_text(encoding='utf-8')
        new_content = content
        
        for old, new_solar in mapping.items():
            if not new_solar or old in ignore:
                continue
                
            # If the old icon is R.drawable.something, replace directly
            if old.startswith('R.drawable.'):
                # Only replace whole words
                pattern = r'\b' + re.escape(old) + r'\b'
                new_content = re.sub(pattern, f'R.drawable.{new_solar}', new_content)
                
            # If it's an Icons.* object, it might be used as an ImageVector
            # We will replace it with ImageVector.vectorResource(id = R.drawable.new_solar)
            # if we are in a composable, or we might just use the painter directly.
            # For simplicity, let's just replace Icons.X.Y with painterResource(R.drawable.new_solar)
            # This requires changing Icon(imageVector = ..., ) to Icon(painter = ... )
            elif old.startswith('Icons.'):
                # find `imageVector = Icons.X.Y` or just `Icons.X.Y`
                # Let's try replacing `Icons.X.Y` with `R.drawable.new_solar` if it's in a model class,
                # But wait, replacing ImageVector with Int will break types.
                # Let's replace `Icons.X.Y` with `ImageVector.vectorResource(id = R.drawable.new_solar)`
                # And we need to add import androidx.compose.ui.res.vectorResource
                pattern = r'\b' + re.escape(old) + r'\b'
                replacement = f'ImageVector.vectorResource(id = R.drawable.{new_solar})'
                if re.search(pattern, new_content):
                    new_content = re.sub(pattern, replacement, new_content)
                    if 'androidx.compose.ui.res.vectorResource' not in new_content:
                        new_content = 'import androidx.compose.ui.res.vectorResource\n' + new_content
        
        if new_content != content:
            path.write_text(new_content, encoding='utf-8')
            replacements += 1

    print(f"Updated {replacements} Kotlin files.")

if __name__ == '__main__':
    main()
