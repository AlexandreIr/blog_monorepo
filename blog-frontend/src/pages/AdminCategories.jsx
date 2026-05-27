import { useCallback, useEffect, useState } from "react";
import { api } from "../api/api";

export default function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState("");

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

  async function createCategory(e) {
    e.preventDefault();

    const trimmedName = name.trim();

    if (!trimmedName) {
      return;
    }

    try {
      const res = await api.post("/api/admin/categories", {
        name: trimmedName,
      });

      setCategories((previous) => [res.data, ...previous]);
      setName("");
    } catch (error) {
      console.error("Erro ao criar categoria:", error);
      alert("Erro ao criar categoria.");
    }
  }

  return (
    <div>
      <h1>Categorias</h1>

      <form className="card" onSubmit={createCategory}>
        <input
          placeholder="Nova categoria"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <button>Criar categoria</button>
      </form>

      {categories.length === 0 && <p>Nenhuma categoria encontrada.</p>}

      {categories.map((category) => (
        <div className="card" key={category.id}>
          <strong>{category.name}</strong>
        </div>
      ))}
    </div>
  );
}