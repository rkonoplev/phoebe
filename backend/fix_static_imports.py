#!/usr/bin/env python3
import re
from pathlib import Path

def fix_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    # Replace static star imports with specific imports
    replacements = [
        (r'import static org\.junit\.jupiter\.api\.Assertions\.\*;', 
         'import static org.junit.jupiter.api.Assertions.assertEquals;\n'
         'import static org.junit.jupiter.api.Assertions.assertNotNull;\n'
         'import static org.junit.jupiter.api.Assertions.assertNull;\n'
         'import static org.junit.jupiter.api.Assertions.assertTrue;\n'
         'import static org.junit.jupiter.api.Assertions.assertFalse;\n'
         'import static org.junit.jupiter.api.Assertions.assertThrows;'),
        (r'import static org\.mockito\.Mockito\.\*;',
         'import static org.mockito.Mockito.any;\n'
         'import static org.mockito.Mockito.verify;\n'
         'import static org.mockito.Mockito.when;\n'
         'import static org.mockito.Mockito.times;\n'
         'import static org.mockito.Mockito.never;\n'
         'import static org.mockito.Mockito.mock;')
    ]
    
    modified = False
    for pattern, replacement in replacements:
        if re.search(pattern, content):
            content = re.sub(pattern, replacement, content)
            modified = True
    
    if modified:
        with open(filepath, 'w') as f:
            f.write(content)
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
