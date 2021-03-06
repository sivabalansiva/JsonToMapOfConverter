package com.plugin.mapofconverter;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class JsonToMapOfSwingComponent extends JComponent {
    public JPanel contentPane;
    JButton buttonGenerate;
    JButton buttonCancel;
    JCheckBox serializeNullsCheckBox;
    JTextField methodNameTextField;
    private MyEditorTextField myEditorTextField;

    private final Project project;
    private final DialogActionListener dialogActionListener;

    public JsonToMapOfSwingComponent(Project project, @NotNull DialogActionListener dialogActionListener) {
        this.project = project;
        this.dialogActionListener = dialogActionListener;

        // initially disable the generate button
        buttonGenerate.setEnabled(false);

        myEditorTextField.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                // enable the generate button only when a valid json is entered
                buttonGenerate.setEnabled(Utils.INSTANCE.isValidJson(myEditorTextField.getText()));
            }
        });

        buttonGenerate.addActionListener(e -> onGenerateClicked());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
        setVisible(true);
    }

    private void onGenerateClicked() {
        String inputText = myEditorTextField.getText();
        String methodName = methodNameTextField.getText();
        boolean serializeNulls = serializeNullsCheckBox.isSelected();
        dialogActionListener.onGenerateClicked(inputText, methodName, serializeNulls);
    }

    private void onCancel() {
        dialogActionListener.onCancel();
    }

    /**
     * As custom create is enabled for MyEditorTextField, createUIComponents is called for better rendering out the ui
     */
    private void createUIComponents() {
        FileType js = FileTypeManager.getInstance().getStdFileType("JavaScript");
        myEditorTextField = new MyEditorTextField(project, js);
        myEditorTextField.setPlaceholder("Enter json here");
        myEditorTextField.setOneLineMode(false);
        myEditorTextField.setAutoscrolls(true);
        myEditorTextField.setFileType(js);
    }
}
