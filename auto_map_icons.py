import os
import difflib
import json
import re

def to_snake_case(name):
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

def main():
    solar_files = [f.replace('.xml', '') for f in os.listdir('app/src/main/res/drawable') if f.startswith('solar_')]
    
    with open('icons_list.txt', 'r') as f:
        old_icons = [line.strip() for line in f if line.strip()]

    mapping = {}
    
    # Pre-defined manual overrides for common mismatches
    overrides = {
        'play_arrow': 'solar_play',
        'arrow_back': 'solar_arrow_left',
        'arrow_forward': 'solar_arrow_right',
        'arrow_downward': 'solar_arrow_down',
        'arrow_upward': 'solar_arrow_up',
        'queue_music': 'solar_music_library',
        'library_music': 'solar_music_library_2',
        'music_note': 'solar_music_note',
        'more_vert': 'solar_menu_dots',
        'more_horiz': 'solar_menu_dots_circle',
        'search': 'solar_magnifer',
        'settings': 'solar_settings',
        'close': 'solar_close_circle',
        'done': 'solar_check_circle',
        'add': 'solar_add_circle',
        'delete': 'solar_trash_bin_trash',
        'favorite': 'solar_heart',
        'favorite_border': 'solar_heart', # wait, solar icons are already broken style
        'person': 'solar_user',
        'volume_up': 'solar_volume_loud',
        'volume_off': 'solar_muted',
        'skip_next': 'solar_skip_next',
        'skip_previous': 'solar_skip_previous',
        'shuffle': 'solar_shuffle',
        'repeat': 'solar_repeat',
        'repeat_one': 'solar_repeat_one',
        'download': 'solar_download',
        'share': 'solar_share',
        'history': 'solar_history',
        'edit': 'solar_pen',
        'clear_all': 'solar_broom',
        'home_filled': 'solar_home',
        'home_outlined': 'solar_home'
    }

    for icon in old_icons:
        # e.g., Icons.Default.CloudDone or R.drawable.ic_music
        parts = icon.split('.')
        base_name = parts[-1]
        if base_name.startswith('ic_'):
            base_name = base_name[3:]
            
        snake_name = to_snake_case(base_name)
        
        if snake_name in overrides:
            match = overrides[snake_name]
        else:
            # find closest solar match
            # simplify solar names (e.g. solar_play_broken -> solar_play)
            # Actually solar icons from our script are named `solar_play.xml`, `solar_arrow_left.xml` etc.
            matches = difflib.get_close_matches('solar_' + snake_name, solar_files, n=1, cutoff=0.6)
            if matches:
                match = matches[0]
            else:
                match = None
                
        mapping[icon] = match

    with open('icon_mapping.json', 'w') as f:
        json.dump(mapping, f, indent=4)

if __name__ == '__main__':
    main()
