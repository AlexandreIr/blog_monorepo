import { useEffect, useState } from "react";
import { adminRequest } from "../../api/adminApi";
// @ts-ignore
import "./adminCss.css";

interface Category {
    id: number;
    name: string;
    slug: string;
}

interface PagedResponse<T> {
    content: T[];
}

export default function AdminCategories() {
    const [categories, setCategories] = useState<Category[]>([]);
    const [name, setName] = useState("");
    const [deletingId, setDeletingId] = useState<number | null>(null);
    const [errorMessage, setErrorMessage] = useState("");

    async function loadCategories() {
        try {
            const data = await adminRequest<PagedResponse<Category> | Category[]>("/admin/categories");

            if (Array.isArray(data)) {
                setCategories(data);
            } else {
                setCategories(data.content ?? []);
            }
        } catch (error) {
            console.error("Erro ao carregar categorias:", error);
            setCategories([]);
        }
    }

    async function createCategory(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!name.trim()) return;

        try {
            const created = await adminRequest<Category>("/admin/categories", {
                method: "POST",
                body: JSON.stringify({ name: name.trim() }),
            });

            setCategories((previous) => [created, ...previous]);
            setName("");
        } catch (error) {
            console.error("Erro ao criar categoria:", error);
        }
    }

 async function deleteCategory(category: Category) {
        const confirmed = window.confirm(
            `Tem certeza que deseja apagar a categoria "${category.name}"?`
        );

        if (!confirmed) return;

        try {
            setErrorMessage("");
            setDeletingId(category.id);

            await adminRequest<void>(`/admin/categories/${category.id}`, {
                method: "DELETE",
            });

            setCategories((previous) =>
                previous.filter((item) => item.id !== category.id)
            );
        } catch (error) {
            console.error("Erro ao apagar categoria:", error);
            setErrorMessage(
                "Não foi possível apagar a categoria. Verifique se ela não está vinculada a algum post."
            );
        } finally {
            setDeletingId(null);
        }
    }

    useEffect(() => {
        loadCategories();
    }, []);

    return (
        <div>
            <div className="admin-header">
                <div>
                    <h1>Categorias</h1>
                    <p>Organize os posts por temas.</p>
                </div>
            </div>

            <form className="admin-form-card" onSubmit={createCategory}>
                <input
                    placeholder="Nome da categoria"
                    value={name}
                    onChange={(event) => setName(event.target.value)}
                />

                <button>Criar categoria</button>
            </form>

            <div className="admin-list">
                {categories.map((category) => (
                    <div className="admin-card" key={category.id}>
                        <div className="flex-between">
                            <div>
                                <h2>{category.name}</h2>
                                <p>{category.slug}</p>
                            </div>
                            <span
                            className="color-red"
                            onClick={() => deleteCategory(category)}
                            disabled={deletingId === category.id} >
                                {deletingId === category.id ? "Apagando..." : "Apagar"}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}