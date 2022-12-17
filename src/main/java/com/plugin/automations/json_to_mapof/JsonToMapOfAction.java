package com.plugin.automations.json_to_mapof;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class JsonToMapOfAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        new JsonToMapOfDialog(anActionEvent.getProject()).showAndGet();
    }
}