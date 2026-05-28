import { Mail } from "lucide-react";

export function NewsletterBox() {
  return (
    <aside className="newsletter-box">
      <Mail size={28} />

      <h2>Receba novidades</h2>

      <p>
        Assine nossa newsletter e receba conteúdos sobre gestão, legislação,
        comércio e serviços corporativos.
      </p>

      <div className="newsletter-form">
        <input placeholder="Seu melhor e-mail" />
        <button>Inscrever-se</button>
      </div>
    </aside>
  );
}