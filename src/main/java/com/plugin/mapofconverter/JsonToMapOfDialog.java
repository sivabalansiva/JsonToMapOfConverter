package com.plugin.mapofconverter;

import com.intellij.lang.Language;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JsonToMapOfDialog extends DialogWrapper implements DialogActionListener {

    private final Project project;

    protected JsonToMapOfDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("Generate MapOf from Json");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JsonToMapOfSwingComponent jComponent = new JsonToMapOfSwingComponent(project, this);
        getRootPane().setDefaultButton(jComponent.buttonGenerate);
        getRootPane().setContentPane(jComponent.contentPane);
        setSize(400, 300);
        getRootPane().setMinimumSize(new Dimension(400, 300));
        return jComponent;
    }

    @Override
    public void onGenerateClicked(String inputText, String methodName, boolean serializeNulls) {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        final Editor editor = manager.getSelectedTextEditor();
        assert editor != null;
        final int cursorOffset = editor.getCaretModel().getOffset();
        final Document document = editor.getDocument();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (Utils.INSTANCE.isValidJson(inputText)) {
                String mapOfString = Utils.INSTANCE.getMapOfCodeFromJsonString(inputText, methodName, serializeNulls);

                /* Format the code so that it will be pretty */
                try {
                    CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
                    PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(Language.findLanguageByID("kotlin"), mapOfString);
                    PsiElement psiElement = styleManager.reformat(psiFile);
                    mapOfString = psiElement.getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                document.insertString(cursorOffset, mapOfString);
                JsonToMapOfDialog.this.dispose();
            } else {
                // do Nothing
            }
        });
    }

    @Override
    public void onCancel() {
        doCancelAction();
    }
}
