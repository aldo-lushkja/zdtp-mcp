package com.ibm.mcp.zdtp.shared.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MarkdownConverterTest {

    @Test
    void toHtml_convertsMarkdownToHtml() {
        String markdown = "# Title\n\n- Item 1\n- Item 2";
        String html = MarkdownConverter.toHtml(markdown);
        
        assertThat(html).contains("<h1>Title</h1>");
        assertThat(html).contains("<ul>");
        assertThat(html).contains("<li>Item 1</li>");
    }

    @Test
    void toHtml_preservesExistingHtml() {
        String htmlInput = "<div>Already HTML</div>";
        String result = MarkdownConverter.toHtml(htmlInput);
        
        assertThat(result).isEqualTo(htmlInput);
    }

    @Test
    void toHtml_handlesNullAndBlank() {
        assertThat(MarkdownConverter.toHtml(null)).isNull();
        assertThat(MarkdownConverter.toHtml("")).isEqualTo("");
        assertThat(MarkdownConverter.toHtml("   ")).isEqualTo("   ");
    }
    
    @Test
    void toHtml_convertsBoldAndItalic() {
        String markdown = "**bold** and *italic*";
        String html = MarkdownConverter.toHtml(markdown);
        
        assertThat(html).contains("<strong>bold</strong>");
        assertThat(html).contains("<em>italic</em>");
    }
}
