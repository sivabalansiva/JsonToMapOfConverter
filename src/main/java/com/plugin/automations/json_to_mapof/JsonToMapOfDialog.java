package com.plugin.automations.json_to_mapof;

import com.intellij.lang.Language;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

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
        var jComponent = new JsonToMapOfSwingComponent(project, this);
        getRootPane().setDefaultButton(jComponent.buttonGenerate);
        getRootPane().setContentPane(jComponent.contentPane);
        getRootPane().setMinimumSize(new Dimension(600, 500));
        return jComponent;
    }

    @Override
    public void onGenerateClicked(String inputText, String methodName, boolean serializeNulls) {
        var manager = FileEditorManager.getInstance(project);
        var editor = Objects.requireNonNull(manager.getSelectedTextEditor());
        final int cursorOffset = editor.getCaretModel().getOffset();
        final var document = editor.getDocument();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (Utils.INSTANCE.isValidJson(inputText)) {
                String mapOfString = Utils.INSTANCE.getMapOfCodeFromJsonString(inputText, methodName, serializeNulls);

                /* Format the code so that it will be pretty */
                try {
                    var language = Objects.requireNonNull(Language.findLanguageByID("kotlin"));
                    var styleManager = CodeStyleManager.getInstance(project);
                    var psiFile = PsiFileFactory.getInstance(project).createFileFromText(language, mapOfString);
                    var psiElement = styleManager.reformat(psiFile);
                    mapOfString = psiElement.getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                document.insertString(cursorOffset, mapOfString);
                JsonToMapOfDialog.this.dispose();
            }
        });
    }

    @Override
    public void onCancel() {
        doCancelAction();
    }
}
