import { PostSummary } from "../types/blog";
import "./hero.css";

interface HeroProps {
  post?: PostSummary;
}

export function Hero({ post }: HeroProps) {
  return (
    <section className="hero">
      <div className="hero-overlay">
        <span className="badge">Destaque</span>

        <h1>{post?.title ?? "Conteúdo estratégico para empresas que querem crescer"}</h1>

        <p>
          {post?.summary ??
            "Insights, análises e soluções práticas sobre negócios, gestão, legislação e serviços corporativos."}
        </p>

        <div className="hero-meta">
          <span>{post?.publishedAt ? new Date(post.publishedAt).toLocaleDateString("pt-BR") : "Conteúdo atualizado"}</span>

        </div>

        {post && (
          <a className="hero-button" href={`/posts/${post.slug}`}>
            Ler artigo completo
          </a>
        )}
      </div>
    </section>
  );
}