package com.plugin.mapofconverter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;

public class MyEditorTextField extends EditorTextField {

    public MyEditorTextField(@NotNull String text) {
        this(text, null, FileTypes.PLAIN_TEXT);
    }

    public MyEditorTextField(@NotNull String text, Project project, FileType fileType) {
        super(text, project, fileType);
    }

    public MyEditorTextField(Document document, Project project, FileType fileType) {
        this(document, project, fileType, false);
    }

    public MyEditorTextField(Project project, FileType fileType) {
        this((Document) null, project, fileType);
    }

    public MyEditorTextField(Document document, Project project, FileType fileType, boolean isViewer) {
        this(document, project, fileType, isViewer, true);
    }

    public MyEditorTextField(Document document, Project project, FileType fileType, boolean isViewer, boolean oneLineMode) {
        super(document, project, fileType, isViewer, oneLineMode);
    }

    @Override
    protected EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);

        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setAutoCodeFoldingEnabled(true);
        settings.setFoldingOutlineShown(true);
        settings.setAllowSingleLogicalLineFolding(true);
        settings.setRightMarginShown(true);
        return editor;
    }
}
