package com.fit.utils;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;

import java.io.File;
import java.util.Map;

public class ASTUtils {

    public static IASTTranslationUnit getIASTTranslationUnit(char[] source, String filePath,
                                                             Map<String, String> macroList, ILanguage lang) {
        FileContent reader = FileContent.create(filePath, source);
        String[] includeSearchPaths = new String[0];
        IScannerInfo scanInfo = new ScannerInfo(macroList, includeSearchPaths);
        IncludeFileContentProvider fileCreator = IncludeFileContentProvider.getSavedFilesProvider();
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();

        try {
            return lang.getASTTranslationUnit(reader, scanInfo, fileCreator, null, options, log);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        String path = "data-test/samvu/1_Load_Tree.cpp";
        ASTUtils.printTree(path);
    }

    /**
     * In cây cấu trúc ra màn hình
     */
    public static void printTree(IASTNode n, String s) {
        String content = n.getRawSignature().replaceAll("[\r\n]", "");
        IASTNode[] child = n.getChildren();
        System.out.println(s + content + ": " + n.getClass().getSimpleName());
        for (IASTNode c : child)
            ASTUtils.printTree(c, s + "   ");

    }

    public static void printTree(String path) {
        try {
            File file = new File(path);
            String content = UtilsVu.getContentFile(file);
            ILanguage lang = file.getName().toLowerCase().endsWith(".c") ? GCCLanguage.getDefault()
                    : GPPLanguage.getDefault();
            IASTTranslationUnit u = ASTUtils.getIASTTranslationUnit(content.toCharArray(), path, null, lang);

            ASTUtils.printTree(u, " | ");
        } catch (Exception e) {

        }
    }
}
