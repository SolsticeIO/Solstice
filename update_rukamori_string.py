import os

file_path = "app/src/main/res/values/solstice_strings.xml"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

new_content = content.replace(
    '<string name="about_position_rukamori">Creator of ArchiveTune · Foundation for Solstice</string>',
    '<string name="about_position_rukamori">Creator of ArchiveTune</string>'
)

if content != new_content:
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print("Updated solstice_strings.xml")
else:
    print("No changes made. String not found.")
