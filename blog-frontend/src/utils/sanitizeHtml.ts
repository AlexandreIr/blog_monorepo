import DOMPurify from "dompurify";

export function sanitizeHtml(html: string) {
    return DOMPurify.sanitize(html, {
        USE_PROFILES: {
            html: true,
        },
        ALLOWED_TAGS: [
            "p",
            "br",
            "strong",
            "b",
            "em",
            "i",
            "s",
            "u",
            "h2",
            "h3",
            "h4",
            "ul",
            "ol",
            "li",
            "blockquote",
            "pre",
            "code",
            "hr",
            "a",
            "img",
            "mark",
            "span",
        ],
        ALLOWED_ATTR: [
            "href",
            "target",
            "rel",
            "src",
            "alt",
            "title",
            "class",
            "style",
        ],
        ALLOWED_URI_REGEXP:
            /^(?:(?:https?|mailto|tel):|[^a-z]|[a-z+.\-]+(?:[^a-z+.\-:]|$))/i,
    });
}