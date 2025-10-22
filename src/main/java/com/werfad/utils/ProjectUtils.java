package com.werfad.utils;

import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.werfad.KeyTagsGenerator;
import com.werfad.MarksCanvas;
import com.werfad.UserConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Collects marks from all editors, prioritizing marks closest to the caret in the currently active editor.
 */
public class ProjectUtils {

    public static List<MarksCanvas.Mark> getMarksFromAllEditors(Project project, Pattern pattern) {
        if (project == null) return new ArrayList<>();

        // 1. Get all visible editors
        FileEditorManagerEx fileEditorManagerEx = FileEditorManagerEx.getInstanceEx(project);
        List<EditorWrapper> allEditors = Arrays.stream(fileEditorManagerEx.getAllEditors())
            .filter(editor -> editor instanceof TextEditor)
            .map(editor -> ((TextEditor) editor).getEditor())
            .distinct()
            .map(editor -> new EditorWrapper(editor))
            .collect(Collectors.toList());

        String userConfigCharacters = UserConfig.getDataBean().characters;
        if (userConfigCharacters.isEmpty()) {
            throw new IllegalArgumentException("Character set for key tags is empty.");
        }

        com.intellij.openapi.editor.Editor activeEditor = fileEditorManagerEx.getSelectedTextEditor();
        int activeCaretOffset = activeEditor != null ? activeEditor.getCaretModel().getOffset() : 0;

        // 2. Sort marks by distance from the caret
        List<OffsetWithEditor> sortedOffsets = new ArrayList<>();
        for (EditorWrapper wrapper : allEditors) {
            com.intellij.openapi.editor.Editor editor = wrapper.editor;
            int[] visibleRange = getVisibleRangeOffset(editor);
            String visibleText = editor.getDocument().getText(StringUtils.createTextRange(visibleRange));

            Matcher matcher = pattern.matcher(visibleText);
            while (matcher.find()) {
                int absoluteOffset = matcher.start() + visibleRange[0];
                sortedOffsets.add(new OffsetWithEditor(editor, absoluteOffset));
            }
        }

        sortedOffsets.sort(Comparator.comparingInt(offsetWithEditor -> {
            if (offsetWithEditor.editor == activeEditor) {
                return Math.abs(offsetWithEditor.offset - activeCaretOffset);
            }
            return Integer.MAX_VALUE;
        }));

        List<String> tags = KeyTagsGenerator.createTagsTree(sortedOffsets.size(), userConfigCharacters);

        List<MarksCanvas.Mark> result = new ArrayList<>();
        for (int i = 0; i < sortedOffsets.size() && i < tags.size(); i++) {
            OffsetWithEditor offsetWithEditor = sortedOffsets.get(i);
            String tag = tags.get(i);
            result.add(new MarksCanvas.Mark(tag, offsetWithEditor.offset, offsetWithEditor.editor));
        }

        return result;
    }

    public static int[] getVisibleRangeOffset(com.intellij.openapi.editor.Editor editor) {
        return new int[]{editor.getScrollingModel().getVisibleArea().y,
                        editor.getScrollingModel().getVisibleArea().y + editor.getScrollingModel().getVisibleArea().height};
    }

    private static class EditorWrapper {
        final com.intellij.openapi.editor.Editor editor;

        EditorWrapper(com.intellij.openapi.editor.Editor editor) {
            this.editor = editor;
        }
    }

    private static class OffsetWithEditor {
        final com.intellij.openapi.editor.Editor editor;
        final int offset;

        OffsetWithEditor(com.intellij.openapi.editor.Editor editor, int offset) {
            this.editor = editor;
            this.offset = offset;
        }
    }
}