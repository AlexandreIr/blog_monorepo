import { sanitizeHtml } from "../utils/sanitizeHtml";

interface Category {
    id: number;
    name: string;
    slug: string;
}

interface PostPreviewModalProps {
    isOpen: boolean;
    onClose: () => void;
    title: string;
    summary: string;
    content: string;
    coverImageUrl: string;
    authorName?: string;
    categories: Category[];
}

export function PostPreviewModal({
                                     isOpen,
                                     onClose,
                                     title,
                                     summary,
                                     content,
                                     coverImageUrl,
                                     authorName = "LCS",
                                     categories,
                                 }: PostPreviewModalProps) {
    if (!isOpen) return null;

    return (
        <div className="preview-overlay">
            <div className="preview-modal">
                <div className="preview-modal-header">
                    <div>
                        <strong>Preview do artigo</strong>
                        <span>Visualização aproximada da página pública</span>
                    </div>

                    <button type="button" onClick={onClose}>
                        Fechar
                    </button>
                </div>

                <div className="preview-modal-body">
                    <article className="article-shell preview-article">
                        <div className="article-cover">
                            {coverImageUrl ? (
                                <img src={coverImageUrl} alt={title} />
                            ) : (
                                <div className="article-cover-placeholder">LCS</div>
                            )}
                        </div>

                        <div className="article-body">
                            <div className="article-categories">
                                {categories.map((category) => (
                                    <span key={category.id}>{category.name}</span>
                                ))}
                            </div>

                            <h1>{title || "Título do artigo"}</h1>

                            <p className="article-summary">
                                {summary || "Resumo do artigo aparecerá aqui."}
                            </p>

                            <div className="article-meta">
                                <span>Por {authorName}</span>
                                <span>{new Date().toLocaleDateString("pt-BR")}</span>
                                <span>5 min de leitura</span>
                            </div>

                            <div
                                className="article-content"
                                dangerouslySetInnerHTML={{
                                    __html: sanitizeHtml(content || "<p>Conteúdo do artigo aparecerá aqui.</p>"),
                                }}
                            />
                        </div>
                    </article>
                </div>
            </div>
        </div>
    );
}