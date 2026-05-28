import { Link } from "react-router-dom";
import { Category } from "../types/blog";

interface CategoriesBoxProps {
  categories: Category[];
}

export function CategoriesBox({ categories }: CategoriesBoxProps) {
  return (
    <aside className="sidebar-box">
      <h2>Categorias</h2>

      <div className="underline" />

      {categories.length === 0 && <p>Nenhuma categoria encontrada.</p>}

      <ul className="categories-list">
        {categories.map((category) => (
          <li key={category.id}>
            <Link to={`/categories/${category.slug}`}>
              <span>
                {category.name}
              </span>
            </Link>
          </li>
        ))}
      </ul>
    </aside>
  );
}