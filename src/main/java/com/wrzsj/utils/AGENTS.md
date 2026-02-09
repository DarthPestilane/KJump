## Utils Package

This package contains utility classes for KJump functionality.

### Files

- `StringUtils.java` - String manipulation utilities
- `EditorUtils.java` - Editor-related utilities
- `ProjectUtils.java` - Project-related utilities

### StringUtils

The `StringUtils` class provides utility methods for string operations:

- `findAll(String str, char c, boolean ignoreCase)` - Find all occurrences of a character in a string
- `findAll(String str, char c)` - Find all occurrences of a character (case sensitive)
- `findAll(String str, String find, boolean ignoreCase)` - Find all occurrences of a substring in a string
- `findAll(String str, String find)` - Find all occurrences of a substring (case sensitive)
- `createTextRange(int[] offsets)` - Convert int array [start, end] to TextRange object
- `createTextRange(int start, int end)` - Create TextRange object from start and end positions

### Description

These utility methods are used throughout the KJump plugin for text processing, range calculations, and string searching operations. The TextRange conversion methods are particularly important for working with IntelliJ's text range system.