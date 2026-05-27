import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/api";

export default function NewPost() {
  const navigate = useNavigate();

  const [categories, setCategories] = useState([]);
  const [selectedCategoryIds, setSelectedCategoryIds] = useState([]);

  const [form, setForm] = useState({
    title: "",
    summary: "",
    content: "",
    coverImageUrl: "",
    metaTitle: "",
    metaDescription: "",
  });

  const loadCategories = useCallback(async () => {
    try {
      const res = await api.get("/api/admin/categories");
      const data = res.data;

      if (Array.isArray(data)) {
        setCategories(data);
        return;
      }

      if (Array.isArray(data.content)) {
        setCategories(data.content);
        return;
      }

      setCategories([]);
    } catch (error) {
      console.error("Erro ao carregar categorias:", error);
      setCategories([]);
    }
  }, []);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  function handleChange(e) {
    const { name, value } = e.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  function toggleCategory(categoryId) {
    setSelectedCategoryIds((previous) => {
      if (previous.includes(categoryId)) {
        return previous.filter((id) => id !== categoryId);
      }

      return [...previous, categoryId];
    });
  }

  async function submitPost(e) {
    e.preventDefault();

    if (selectedCategoryIds.length === 0) {
      alert("Selecione pelo menos uma categoria.");
      return;
    }

    try {
      await api.post("/api/admin/posts", {
        title: form.title,
        summary: form.summary,
        content: form.content,
        coverImageUrl: form.coverImageUrl,
        metaTitle: form.metaTitle,
        metaDescription: form.metaDescription,
        categoryIds: selectedCategoryIds,
      });

      alert("Post criado como rascunho.");
      navigate("/painel-secreto/posts");
    } catch (error) {
      console.error("Erro ao criar post:", error);
      alert("Erro ao criar post. Veja o console.");
    }
  }

  return (
    <div>
      <h1>Novo Post</h1>

      <form className="card" onSubmit={submitPost}>
        <input
          name="title"
          placeholder="Título"
          value={form.title}
          onChange={handleChange}
        />

        <textarea
          name="summary"
          placeholder="Resumo"
          value={form.summary}
          onChange={handleChange}
        />

        <textarea
          name="content"
          placeholder="Conteúdo completo"
          value={form.content}
          onChange={handleChange}
        />

        <input
          name="coverImageUrl"
          placeholder="URL da imagem de capa"
          value={form.coverImageUrl}
          onChange={handleChange}
        />

        <input
          name="metaTitle"
          placeholder="Meta title"
          value={form.metaTitle}
          onChange={handleChange}
        />

        <textarea
          name="metaDescription"
          placeholder="Meta description"
          value={form.metaDescription}
          onChange={handleChange}
        />

        <h3>Categorias</h3>

        {categories.length === 0 && <p>Nenhuma categoria criada ainda.</p>}

        <div className="checkbox-list">
          {categories.map((category) => (
            <label key={category.id} className="checkbox-item">
              <input
                type="checkbox"
                checked={selectedCategoryIds.includes(category.id)}
                onChange={() => toggleCategory(category.id)}
              />
              {category.name}
            </label>
          ))}
        </div>

        <button>Criar post</button>
      </form>
    </div>
  );
}