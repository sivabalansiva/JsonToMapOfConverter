package com.plugin.automations.import_translations;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImportTranslationStringsDialog extends DialogWrapper implements StringTranslationsImporter.ImportTranslationEventHandler {

    private ImportTranslationsUiBinding jComponent;
    
    ImportTranslationStringsDialog() {
        super(null);
        setTitle("Import String Translations");
        init();
        pack();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        jComponent = new ImportTranslationsUiBinding();
        getRootPane().setContentPane(jComponent.contentPane);
        setImportStatusTextAreaVisible(false);
        updateDialogMinimumHeight();
        setListeners();
        return jComponent;
    }

    private void setListeners() {
        jComponent.buttonOK.addActionListener((actionEvent) -> onImportClicked());
        jComponent.buttonCancel.addActionListener((actionEvent) -> onCancel());
    }

    private void setImportStatusTextAreaVisible(boolean visible) {
        jComponent.scrollPane.setVisible(visible);
        jComponent.importStatusTextArea.setVisible(visible);
    }

    private void updateDialogMinimumHeight() {
        int dialogHeight = jComponent.importStatusTextArea.isVisible() ? 550 : 200;
        getRootPane().setMinimumSize(new Dimension(450, dialogHeight));
        getRootPane().setMaximumSize(new Dimension(-1, dialogHeight));
    }

    private void onImportClicked() {
        try {
            String projectResourcesPath = jComponent.tfStringResourcePath.getText();
            String translationStringsPath = jComponent.tfTranslationStringsPath.getText();
            var instance = new StringTranslationsImporter(this);
            instance.addAllStrings(projectResourcesPath, translationStringsPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCancel() {
        close(1);
    }

    @Override
    public void onImportTranslationsStarted() {
        jComponent.buttonOK.setVisible(false);
        jComponent.buttonCancel.setVisible(false);
        setImportStatusTextAreaVisible(true);
        jComponent.importStatusTextArea.setText("");    // clear text
        updateDialogMinimumHeight();
    }

    @Override
    public void onImportTranslationsEnded() {
        jComponent.buttonOK.setVisible(false);
        jComponent.buttonCancel.setVisible(true);
    }

    @Override
    public void onTranslationAdded(String addedFrom, String addedTo) {
        var fromFile = new File(addedFrom).getName();
        var toFile = new File(addedTo);
        var toDirectory = toFile.getParentFile().getName();
        jComponent.importStatusTextArea.append("String added from " + fromFile + " to " + toDirectory + "\n");
    }

    @Override
    public void onImportTranslationsError(Exception e, String message) {
        jComponent.buttonOK.setVisible(true);
        jComponent.buttonCancel.setVisible(true);
        setImportStatusTextAreaVisible(true);
        if (message != null) {
            jComponent.importStatusTextArea.append(message + "\n");
        }
        if (e != null) {
            jComponent.importStatusTextArea.append(e.getMessage() + "\n");
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
