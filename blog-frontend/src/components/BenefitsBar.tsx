import { ShieldCheck, Target, Users } from "lucide-react";

export function BenefitsBar() {
  return (
    <section className="benefits-bar">
      <div>
        <ShieldCheck size={34} />
        <div>
          <strong>Conteúdo confiável</strong>
          <p>Informações úteis e atualizadas para empresas.</p>
        </div>
      </div>

      <div>
        <Users size={34} />
        <div>
          <strong>Especialistas qualificados</strong>
          <p>Visão prática sobre desafios corporativos.</p>
        </div>
      </div>

      <div>
        <Target size={34} />
        <div>
          <strong>Foco em soluções</strong>
          <p>Conteúdos para ajudar decisões estratégicas.</p>
        </div>
      </div>
    </section>
  );
}