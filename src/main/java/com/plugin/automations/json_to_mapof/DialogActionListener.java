package com.plugin.automations.json_to_mapof;

public interface DialogActionListener {
    void onGenerateClicked(String text, String inputText, boolean serializeNulls);
    void onCancel();
}
