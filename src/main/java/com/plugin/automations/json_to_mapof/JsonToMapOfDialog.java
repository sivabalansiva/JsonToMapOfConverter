package com.plugin.automations.json_to_mapof;

import com.intellij.lang.Language;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class JsonToMapOfDialog extends DialogWrapper {

    private final Project project;
    private JsonToMapOfUiBinding binding;

    protected JsonToMapOfDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("Generate MapOf from Json");
        init();
        setListeners();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        binding = new JsonToMapOfUiBinding(project);
        getRootPane().setDefaultButton(binding.buttonGenerate);
        getRootPane().setContentPane(binding.contentPane);
        getRootPane().setMinimumSize(new Dimension(600, 500));
        return binding;
    }

    private void setListeners() {
        // initially disable the generate button
        binding.buttonGenerate.setEnabled(false);

        binding.myEditorTextField.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                // enable the generate button only when a valid json is entered
                binding.buttonGenerate.setEnabled(Utils.INSTANCE.isValidJson(binding.myEditorTextField.getText()));
            }
        });

        binding.buttonGenerate.addActionListener(e -> onGenerateClicked());
        binding.buttonCancel.addActionListener(e -> doCancelAction());

        // call onCancel() on ESCAPE
        binding.contentPane.registerKeyboardAction(
                e -> doCancelAction(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    public void onGenerateClicked() {
        String inputText = binding.myEditorTextField.getText();
        String methodName = binding.methodNameTextField.getText();
        boolean serializeNulls = binding.serializeNullsCheckBox.isSelected();

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
}

class JsonToMapOfUiBinding extends JComponent {
    public JPanel contentPane;
    public JButton buttonGenerate;
    public JButton buttonCancel;
    public JCheckBox serializeNullsCheckBox;
    public JTextField methodNameTextField;
    public MyEditorTextField myEditorTextField;
    private final Project project;

    JsonToMapOfUiBinding(Project project) {
        this.project = project;
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
