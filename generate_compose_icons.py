import os
import json
import xml.etree.ElementTree as ET
from pathlib import Path
import re

SVG_NS = {'svg': 'http://www.w3.org/2000/svg'}

def find_svg(solar_name, svg_dir):
    # e.g. solar_play -> play
    base = solar_name[6:].replace('_', ' ')
    
    # Try exact match or similar
    for p in svg_dir.rglob('*.svg'):
        stem = p.stem.lower().replace('-', ' ')
        if stem == base or stem.replace(' ', '_') == base:
            return p
    return None

def parse_svg_to_compose(svg_path, kt_name):
    tree = ET.parse(svg_path)
    root = tree.getroot()
    
    width = float(root.attrib.get('width', '24').replace('px', ''))
    height = float(root.attrib.get('height', '24').replace('px', ''))
    
    viewBox = root.attrib.get('viewBox', f'0 0 {width} {height}')
    vb_parts = viewBox.split()
    vw = float(vb_parts[2]) if len(vb_parts) == 4 else width
    vh = float(vb_parts[3]) if len(vb_parts) == 4 else height

    code = f"""
    private var _{kt_name}: ImageVector? = null
    val {kt_name}: ImageVector
        get() {{
            if (_{kt_name} != null) {{
                return _{kt_name}!!
            }}
            _{kt_name} = Builder(
                name = "{kt_name}",
                defaultWidth = {width}.dp,
                defaultHeight = {height}.dp,
                viewportWidth = {vw}f,
                viewportHeight = {vh}f
            ).apply {{
"""

    for elem in root.iter():
        tag = elem.tag.split('}')[-1]
        if tag in ['path', 'circle', 'rect']:
            if tag == 'path':
                d = elem.attrib.get('d', '')
            elif tag == 'circle':
                cx, cy, r = float(elem.attrib.get('cx', 0)), float(elem.attrib.get('cy', 0)), float(elem.attrib.get('r', 0))
                d = f"M {cx} {cy-r} A {r} {r} 0 1 0 {cx} {cy+r} A {r} {r} 0 1 0 {cx} {cy-r} Z"
            elif tag == 'rect':
                x, y, w, h = float(elem.attrib.get('x', 0)), float(elem.attrib.get('y', 0)), float(elem.attrib.get('width', 0)), float(elem.attrib.get('height', 0))
                d = f"M {x} {y} H {x+w} V {y+h} H {x} Z"
            
            # extract path string properly. Compose uses VectorPathBuilder for complex, but for string we can use addPath(PathParser().parsePathString(d).toNodes())
            # Actually Compose has PathData(d) or we can use vectorPath { ... }
            
            # A simpler way in Compose: PathParser().parsePathString(d).toNodes() is internal.
            # Best is to generate addPath(
            # pathData = PathParser().parsePathString("...").toNodes(), ...
            
            # Wait, ImageVector.Builder does not have a simple way to take a raw string unless using internal APIs in standard Compose,
            # BUT we can use `addPath(pathData = addPathNodes("..."), ...)` -> `addPathNodes` is public in `androidx.compose.ui.graphics.vector`.
            
            stroke = elem.attrib.get('stroke')
            fill = elem.attrib.get('fill')
            sw = elem.attrib.get('stroke-width', '1.0')
            linecap = elem.attrib.get('stroke-linecap', 'butt').capitalize()
            linejoin = elem.attrib.get('stroke-linejoin', 'miter').capitalize()
            
            stroke_code = 'SolidColor(Color.White)' if stroke and stroke != 'none' else 'null'
            fill_code = 'SolidColor(Color.White)' if fill and fill != 'none' else 'null'
            
            # convert linecap
            if linecap == 'Round': linecap = 'StrokeCap.Round'
            elif linecap == 'Square': linecap = 'StrokeCap.Square'
            else: linecap = 'StrokeCap.Butt'

            if linejoin == 'Round': linejoin = 'StrokeJoin.Round'
            elif linejoin == 'Bevel': linejoin = 'StrokeJoin.Bevel'
            else: linejoin = 'StrokeJoin.Miter'

            code += f"""
                addPath(
                    pathData = addPathNodes("{d}"),
                    fill = {fill_code},
                    stroke = {stroke_code},
                    strokeLineWidth = {sw}f,
                    strokeLineCap = {linecap},
                    strokeLineJoin = {linejoin}
                )"""
    
    code += """
            }.build()
            return _%s!!
        }
""" % kt_name

    return code

def main():
    svg_dirs = [
        Path("/home/stark/Downloads/My Self/Music/solar-icons/icons/SVG/Broken"),
        Path("/home/stark/Downloads/My Self/Music/solar-icons/icons/SVG/Outline")
    ]
    with open('icon_mapping.json', 'r') as f:
        mapping = json.load(f)

    kt_code = """package urstark.solstice.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

object SolarIcons {
"""

    generated = set()
    for old, new_solar in mapping.items():
        if not new_solar or old.startswith('R.drawable'):
            continue

        kt_name = old.split('.')[-1]

        if kt_name in generated:
            continue

        svg_path = None
        for svg_dir in svg_dirs:
            svg_path = find_svg(new_solar, svg_dir)
            if svg_path:
                break
        
        if svg_path:
            try:
                kt_code += parse_svg_to_compose(svg_path, kt_name)
                generated.add(kt_name)
            except Exception as e:
                print(f"Failed to parse {svg_path}: {e}")
        else:
            print(f"Could not find SVG for {new_solar}")

    kt_code += "\n}\n"

    out_path = Path("app/src/main/kotlin/urstark/solstice/ui/icons/SolarIcons.kt")
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(kt_code, encoding='utf-8')
    print(f"Generated SolarIcons.kt with {len(generated)} icons")

if __name__ == '__main__':
    main()
