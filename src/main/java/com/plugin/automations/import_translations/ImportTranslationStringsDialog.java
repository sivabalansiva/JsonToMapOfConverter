package com.plugin.automations.import_translations;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImportTranslationStringsDialog extends DialogWrapper implements StringTranslationsImporter.ImportTranslationEventHandler {

    private ImportTranslationsUiBinding binding;
    
    ImportTranslationStringsDialog() {
        super(null);
        setTitle("Import String Translations");
        init();
        pack();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        binding = new ImportTranslationsUiBinding();
        getRootPane().setContentPane(binding.contentPane);
        setImportStatusTextAreaVisible(false);
        updateDialogMinimumHeight();
        setListeners();
        return binding;
    }

    private void setListeners() {
        binding.buttonOK.addActionListener((actionEvent) -> onImportClicked());
        binding.buttonCancel.addActionListener((actionEvent) -> close(1));
    }

    private void setImportStatusTextAreaVisible(boolean visible) {
        binding.scrollPane.setVisible(visible);
        binding.importStatusTextArea.setVisible(visible);
    }

    private void updateDialogMinimumHeight() {
        int dialogHeight = binding.importStatusTextArea.isVisible() ? 550 : 200;
        getRootPane().setMinimumSize(new Dimension(450, dialogHeight));
        getRootPane().setMaximumSize(new Dimension(-1, dialogHeight));
    }

    private void onImportClicked() {
        try {
            if (isValidDirectory(binding.tfTranslationStringsPath) && isValidDirectory(binding.tfStringResourcePath)) {
                String translationStringsPath = binding.tfTranslationStringsPath.getText();
                String projectResourcesPath = binding.tfStringResourcePath.getText();
                var instance = new StringTranslationsImporter(this);
                instance.addAllStrings(projectResourcesPath, translationStringsPath);
            }
        } catch (Exception e) {
            onImportTranslationsError(e, null);
            e.printStackTrace();
        }
    }

    private boolean isValidDirectory(JTextField textField) {
        String errorMessage = new File(textField.getText()).isDirectory() ? null : "Enter a valid path";
        setErrorText(errorMessage, textField);
        return errorMessage == null;
    }

    @Override
    public void onImportTranslationsStarted() {
        binding.buttonOK.setVisible(false);
        binding.buttonCancel.setVisible(false);
        setImportStatusTextAreaVisible(true);
        binding.importStatusTextArea.setText("");    // clear text
        updateDialogMinimumHeight();
    }

    @Override
    public void onTranslationAdded(String addedFrom, String addedTo) {
        var fromFile = new File(addedFrom).getName();
        var toFile = new File(addedTo);
        var toDirectory = toFile.getParentFile().getName();
        binding.importStatusTextArea.append("String added from " + fromFile + " to " + toDirectory + "\n");
    }

    @Override
    public void onImportTranslationsEnded() {
        binding.buttonOK.setVisible(false);
        binding.buttonCancel.setVisible(true);
    }

    @Override
    public void onImportTranslationsError(Exception e, String message) {
        binding.buttonOK.setVisible(true);
        binding.buttonCancel.setVisible(true);
        setImportStatusTextAreaVisible(true);
        if (message != null) {
            binding.importStatusTextArea.append(message + "\n");
        }
        if (e != null) {
            binding.importStatusTextArea.append("Error while importing translations...\n");
            binding.importStatusTextArea.append(e.getMessage() + "\n");
        }
    }
}

// Ui Components Binding class
class ImportTranslationsUiBinding extends JComponent {

    public JPanel contentPane;
    public JButton buttonOK;
    public JButton buttonCancel;
    public JScrollPane scrollPane;
    public JTextArea importStatusTextArea;
    public JTextField tfStringResourcePath;
    public JTextField tfTranslationStringsPath;
}
