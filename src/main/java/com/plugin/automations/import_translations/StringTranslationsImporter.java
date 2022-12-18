package com.plugin.automations.import_translations;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StringTranslationsImporter {

    private static final String RESOURCES_START_TAG = "<resources>";
    private static final String RESOURCES_END_TAG = "</resources>";
    private static final String XML_RESOURCES_HEADER_TAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static final String XML_RESOURCES_HEADER_XLIFF_TAG = "<resources xmlns:xliff=\"urn:oasis:names:tc:xliff:document:1.2\">";

    @NotNull
    private final ImportTranslationEventHandler eventHandler;

    public StringTranslationsImporter(ImportTranslationEventHandler eventHandler) {
        this.eventHandler = eventHandler == null ? getDefaultEventHandler() : eventHandler;
    }

    public static void main(String[] args) {
        var instance = new StringTranslationsImporter(null);
        try {
            var translationStringsPath = "/Users/siva-6508/Downloads/v6_4_0_missed_translation_strings 2";
            var resourcePath = "/Users/siva-6508/workspace_studio/servicedesk_cloud_android/app/src/main/res/";
            instance.addAllStrings(resourcePath, translationStringsPath);
        } catch (Exception e) {
            instance.eventHandler.onImportTranslationsError(e, null);
        }
    }

    public void addAllStrings(String resourcePath, String translationStringsPath) throws Exception {
        var translationDirectory = new File(translationStringsPath);
        var translationFilesList = translationDirectory.listFiles();

        eventHandler.onImportTranslationsStarted();

        if (translationFilesList == null || translationFilesList.length == 0) {
            eventHandler.onImportTranslationsError(null, "No String translations found.");
            eventHandler.onImportTranslationsEnded();
            return;
        }

        for (File file : translationFilesList) {
            // append '/' if resourcePath does not end with '/'
            resourcePath = resourcePath.endsWith("/") ? resourcePath : resourcePath + "/";

            var resourceDirectoryName = getResourceDirectoryName(resourcePath, file.getName());
            var addToFile = resourcePath + resourceDirectoryName + "/strings.xml";
            if (!(new File(addToFile).exists())) {
                createNewStringResourceFile(addToFile);
            }
            addStrings(file.getAbsolutePath(), addToFile);
        }
        eventHandler.onImportTranslationsEnded();
    }

    private void createNewDirectory(String filePath) {
        var file = new File(filePath);
        if (!file.mkdir()) {
            eventHandler.onImportTranslationsError(null, "Could not create file: " + filePath);
        }
    }

    private void createNewStringResourceFile(String filePath) throws IOException {
        var file = new File(filePath);
        if (file.createNewFile()) {
            var addToFileLines = List.of(XML_RESOURCES_HEADER_TAG, RESOURCES_START_TAG, RESOURCES_END_TAG);
            Files.write(Paths.get(filePath), addToFileLines, StandardCharsets.UTF_8);
        } else {
            eventHandler.onImportTranslationsError(null, "Could not create file: " + filePath);
        }
    }

    private String getResourceDirectoryName(String resourcePath, String fileName) {
        var resourceDirectoryNameList = getResourceDirectoryNameList(fileName);
        var existingDirectoryName = (String) null;

        for (var resourceDirectoryName : resourceDirectoryNameList) {
            var resourceDirectoryPath = resourcePath + resourceDirectoryName;
            var resourceDirectory = new File(resourceDirectoryPath);
            if (resourceDirectory.exists()) {
                existingDirectoryName = resourceDirectoryName;
                break;
            }
        }

        if (existingDirectoryName == null) {
            var defaultDirectory = resourceDirectoryNameList.get(0);
            var resourceDirectoryPath = resourcePath + defaultDirectory;
            createNewDirectory(resourceDirectoryPath);
            return defaultDirectory;
        } else {
            return existingDirectoryName;
        }
    }

    private List<String> getResourceDirectoryNameList(String fileName) {
        if (fileName.contains("_ar-EG") || fileName.contains("_ar_EG")) {
            return List.of("values-ar");
        } else if (fileName.contains("_pt-BR") || fileName.contains("_pt_BR")) {
            return List.of("values-br", "values-pt-rBR");
        } else if (fileName.contains("_bs-Latn-BA") || fileName.contains("_bs_Latn_BA")) {
            return List.of("values-bs");
        } else if (fileName.contains("_cs-CZ") || fileName.contains("_cs_CZ")) {
            return List.of("values-cs");
        } else if (fileName.contains("_cy-GB") || fileName.contains("_cy_GB")) {
            return List.of("values-cy");
        } else if (fileName.contains("_da-DK") || fileName.contains("_da_DK")) {
            return List.of("values-da");
        } else if (fileName.contains("_de-DE") || fileName.contains("_de_DE")) {
            return List.of("values-de");
        } else if (fileName.contains("_es-ES") || fileName.contains("_es_ES")) {
            return List.of("values-es");
        } else if (fileName.contains("_fi-FI") || fileName.contains("_fi_FI")) {
            return List.of("values-fi");
        } else if (fileName.contains("_fr-FR") || fileName.contains("_fr_FR")) {
            return List.of("values-fr");
        } else if (fileName.contains("_he-IL") || fileName.contains("_he_IL")) {
            return List.of("values-he", "values-iw");
        } else if (fileName.contains("_hr-HR") || fileName.contains("_hr_HR")) {
            return List.of("values-hr");
        } else if (fileName.contains("_hu-HU") || fileName.contains("_hu_HU")) {
            return List.of("values-hu");
        } else if (fileName.contains("_is-IS") || fileName.contains("_is_IS")) {
            return List.of("values-is");
        } else if (fileName.contains("_it-IT") || fileName.contains("_it_IT")) {
            return List.of("values-it");
        } else if (fileName.contains("_ja-JP") || fileName.contains("_ja_JP")) {
            return List.of("values-ja");
        } else if (fileName.contains("_ka-GE") || fileName.contains("_ka_GE")) {
            return List.of("values-ka");
        } else if (fileName.contains("_ko-KR") || fileName.contains("_ko_KR")) {
            return List.of("values-ko");
        } else if (fileName.contains("_nb-NO") || fileName.contains("_nb_NO")) {
            return List.of("values-nb", "values-no");
        } else if (fileName.contains("_nl-NL") || fileName.contains("_nl_NL")) {
            return List.of("values-nl");
        } else if (fileName.contains("_pl-PL") || fileName.contains("_pl_PL")) {
            return List.of("values-pl");
        } else if (fileName.contains("_pt-PT") || fileName.contains("_pt_PT")) {
            return List.of("values-pt");
        } else if (fileName.contains("_ro-RO") || fileName.contains("_ro_RO")) {
            return List.of("values-ro");
        } else if (fileName.contains("_ru-RU") || fileName.contains("_ru_RU")) {
            return List.of("values-ru");
        } else if (fileName.contains("_sl-SI") || fileName.contains("_sl_SI")) {
            return List.of("values-sl");
        } else if (fileName.contains("_sr-Latn-RS") || fileName.contains("_sr_Latn_RS")) {
            return List.of("values-sr");
        } else if (fileName.contains("_sv-SE") || fileName.contains("_sv_SE")) {
            return List.of("values-sv");
        } else if (fileName.contains("_tr-TR") || fileName.contains("_tr_TR")) {
            return List.of("values-tr");
        } else if (fileName.contains("_vi-VN") || fileName.contains("_vi_VN")) {
            return List.of("values-vi");
        } else if (fileName.contains("_zh-CN") || fileName.contains("_zh_CN")) {
            return List.of("values-zh");
        } else if (fileName.contains("_zh-TW") || fileName.contains("_zh_TW")) {
            return List.of("values-zh-rTW");
        } else {
            return List.of(fileName);
        }
    }

    private void addStrings(String addFromFile, String addToFile) throws Exception {
        var addToFilePath = Path.of(addToFile);
        var srcString = String.join("\n", Files.readAllLines(addToFilePath));
        var translatedStrings = getTranslatedFileContents(addFromFile);

        var index = srcString.indexOf(RESOURCES_END_TAG);
        var start = srcString.substring(0, index);
        var end = srcString.substring(index);

        var newString = start + translatedStrings + end;
        Files.write(addToFilePath, newString.getBytes());

        eventHandler.onTranslationAdded(addFromFile, addToFile);
    }

    private String getTranslatedFileContents(String file) throws Exception {
        var xml = String.join("", Files.readAllLines(Paths.get(file)));
        xml = formatXmlString(xml, 4);

        int xLiffHeaderIndex = xml.indexOf(XML_RESOURCES_HEADER_XLIFF_TAG);
        int resourcesTagIndex = xml.indexOf(RESOURCES_START_TAG);
        int resourcesEndTagIndex = xml.indexOf(RESOURCES_END_TAG);

        int start = 0;
        if (xLiffHeaderIndex != -1) {
            start = xLiffHeaderIndex + XML_RESOURCES_HEADER_XLIFF_TAG.length();
        } else if (resourcesTagIndex != -1) {
            start = resourcesTagIndex + RESOURCES_START_TAG.length();
        }
        int end = resourcesEndTagIndex == -1 ? xml.length() : resourcesEndTagIndex;
        return xml.substring(start, end);
    }

    @NotNull
    public String formatXmlString(@NotNull String xml, int indent) {
        try {
            // Turn xml string into a document
            var document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));

            // Remove whitespaces outside tags
            document.normalize();
            var xPath = XPathFactory.newInstance().newXPath();
            var nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                    document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                var node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            var transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return xml;
        }
    }

    private ImportTranslationEventHandler getDefaultEventHandler() {
        return new ImportTranslationEventHandler() {
            @Override
            public void onImportTranslationsStarted() {

            }

            @Override
            public void onImportTranslationsEnded() {

            }

            @Override
            public void onTranslationAdded(String addedFrom, String addedTo) {
                System.out.println(
                        "Translated string added from " + new File(addedFrom).getName() +
                                " to " + new File(addedTo).getParentFile().getName()
                );
            }

            @Override
            public void onImportTranslationsError(Exception e, String message) {
                if (e != null) {
                    e.printStackTrace(System.err);
                }
                if (message != null) {
                    System.err.println(message);
                }
            }
        };
    }

    interface ImportTranslationEventHandler {
        void onImportTranslationsStarted();
        void onImportTranslationsEnded();
        void onTranslationAdded(String addedFrom, String addedTo);
        void onImportTranslationsError(Exception e, String message);
    }
}
