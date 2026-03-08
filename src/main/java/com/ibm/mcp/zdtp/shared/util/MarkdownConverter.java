package com.ibm.mcp.zdtp.shared.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownConverter {
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return markdown;
        }
        // If it already looks like HTML, don't convert
        String trimmed = markdown.trim();
        if (trimmed.startsWith("<") && trimmed.endsWith(">")) {
            return markdown;
        }
        Node document = PARSER.parse(markdown);
        String html = RENDERER.render(document).trim();
        
        // maintain compatibility with tests that don't expect <p> tags for simple one-liners
        if (html.startsWith("<p>") && html.endsWith("</p>") && html.indexOf("<p>") == html.lastIndexOf("<p>")) {
            return html.substring(3, html.length() - 4);
        }
        
        return html;
    }
}
