package br.com.libertadfacilities.blog.services;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {

    private final PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements(
                    "p", "br",
                    "strong", "b",
                    "em", "i",
                    "s", "u",
                    "h2", "h3", "h4",
                    "ul", "ol", "li",
                    "blockquote",
                    "pre", "code",
                    "hr",
                    "a", "img",
                    "mark", "span",
                    "video", "source"
            )
            .allowAttributes("href", "target", "rel")
            .onElements("a")
            .allowAttributes("src", "alt", "title")
            .onElements("img")
            .allowAttributes("src", "type")
            .onElements("source")
            .allowAttributes("src", "controls", "poster", "preload")
            .onElements("video")
            .allowUrlProtocols("http", "https", "mailto")
            .requireRelNofollowOnLinks()
            .toFactory();

    public String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }

        return policy.sanitize(html);
    }
}