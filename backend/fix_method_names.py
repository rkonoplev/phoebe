#!/usr/bin/env python3
import re
import sys
from pathlib import Path

def to_camel_case(name):
    """Convert method name to camelCase"""
    # Split by capital letters and underscores
    parts = re.findall(r'[A-Z][a-z0-9]*|[a-z0-9]+', name)
    if not parts:
        return name
    # First part lowercase, rest capitalized
    return parts[0].lower() + ''.join(p.capitalize() for p in parts[1:])

def fix_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    # Find all method declarations starting with capital letter
    pattern = r'(\s+)(void|static\s+void)\s+([A-Z][a-zA-Z0-9_]*)\s*\('
    
    def replace_method(match):
        indent = match.group(1)
        modifier = match.group(2)
        method_name = match.group(3)
        new_name = to_camel_case(method_name)
        return f'{indent}{modifier} {new_name}('
    
    new_content = re.sub(pattern, replace_method, content)
    
    if new_content != content:
        with open(filepath, 'w') as f:
            f.write(new_content)
        return True
    return False

def main():
    test_dir = Path('src/test')
    fixed_count = 0
    
    for java_file in test_dir.rglob('*.java'):
        if fix_file(java_file):
            fixed_count += 1
            print(f'Fixed: {java_file}')
    
    print(f'\nTotal files fixed: {fixed_count}')

if __name__ == '__main__':
    main()
