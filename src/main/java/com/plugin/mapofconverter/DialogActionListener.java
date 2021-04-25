package com.plugin.mapofconverter;

public interface DialogActionListener {
    void onGenerateClicked(String text, String inputText, boolean serializeNulls);
    void onCancel();
}
