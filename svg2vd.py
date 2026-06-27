import os
import xml.etree.ElementTree as ET
from pathlib import Path
import re

SVG_NS = {'svg': 'http://www.w3.org/2000/svg'}

def convert_svg_to_vd(svg_path, vd_path):
    try:
        tree = ET.parse(svg_path)
        root = tree.getroot()
        
        width = root.attrib.get('width', '24').replace('px', '')
        height = root.attrib.get('height', '24').replace('px', '')
        
        viewBox = root.attrib.get('viewBox', f'0 0 {width} {height}')
        vb_parts = viewBox.split()
        vw = vb_parts[2] if len(vb_parts) == 4 else width
        vh = vb_parts[3] if len(vb_parts) == 4 else height

        vd = ET.Element('vector', {
            'xmlns:android': 'http://schemas.android.com/apk/res/android',
            'android:width': f'{width}dp',
            'android:height': f'{height}dp',
            'android:viewportWidth': vw,
            'android:viewportHeight': vh
        })

        for elem in root.iter():
            tag = elem.tag.split('}')[-1]
            if tag in ['path', 'circle', 'rect']:
                vd_elem = ET.SubElement(vd, 'path')
                
                # Path data
                if tag == 'path':
                    vd_elem.set('android:pathData', elem.attrib.get('d', ''))
                elif tag == 'circle':
                    cx, cy, r = float(elem.attrib.get('cx', 0)), float(elem.attrib.get('cy', 0)), float(elem.attrib.get('r', 0))
                    # approximate circle with path
                    d = f"M {cx} {cy-r} A {r} {r} 0 1 0 {cx} {cy+r} A {r} {r} 0 1 0 {cx} {cy-r} Z"
                    vd_elem.set('android:pathData', d)
                elif tag == 'rect':
                    x, y, w, h = float(elem.attrib.get('x', 0)), float(elem.attrib.get('y', 0)), float(elem.attrib.get('width', 0)), float(elem.attrib.get('height', 0))
                    d = f"M {x} {y} H {x+w} V {y+h} H {x} Z"
                    vd_elem.set('android:pathData', d)

                # Styling
                stroke = elem.attrib.get('stroke')
                if stroke and stroke != 'none':
                    vd_elem.set('android:strokeColor', '#FFFFFF') # use white/tintable
                
                fill = elem.attrib.get('fill')
                if fill and fill != 'none':
                    vd_elem.set('android:fillColor', '#FFFFFF')

                stroke_width = elem.attrib.get('stroke-width')
                if stroke_width:
                    vd_elem.set('android:strokeWidth', stroke_width)
                
                stroke_linecap = elem.attrib.get('stroke-linecap')
                if stroke_linecap:
                    vd_elem.set('android:strokeLineCap', stroke_linecap)

                stroke_linejoin = elem.attrib.get('stroke-linejoin')
                if stroke_linejoin:
                    vd_elem.set('android:strokeLineJoin', stroke_linejoin)

        tree = ET.ElementTree(vd)
        ET.indent(tree, space="    ", level=0)
        tree.write(vd_path, encoding='utf-8', xml_declaration=True)
        return True
    except Exception as e:
        print(f"Failed to convert {svg_path}: {e}")
        return False

def main():
    svg_dirs = [
        Path("/home/stark/Downloads/My Self/Music/solar-icons/icons/SVG/Broken"),
        Path("/home/stark/Downloads/My Self/Music/solar-icons/icons/SVG/Outline")
    ]
    out_dir = Path("app/src/main/res/drawable")
    out_dir.mkdir(parents=True, exist_ok=True)

    count = 0
    # Keep track of generated so we prefer Broken
    generated = set()

    for svg_dir in svg_dirs:
        for path in svg_dir.rglob("*.svg"):
            name = path.stem.lower().replace(' ', '_').replace('-', '_')
            vd_path = out_dir / f"solar_{name}.xml"
            if name not in generated:
                if convert_svg_to_vd(path, vd_path):
                    count += 1
                    generated.add(name)

    print(f"Converted {count} SVG icons to VectorDrawables in {out_dir}")

if __name__ == "__main__":
    main()
