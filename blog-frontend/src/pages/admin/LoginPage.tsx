import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { saveToken } from "../../api/adminApi";
// @ts-ignore
import "./adminCss.css";

const API_URL = "https://libertad-api.onrender.com";

interface LoginResponse {
    token: string;
    type: string;
}

export default function LoginPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("admin@empresa.com");
    const [password, setPassword] = useState("admin123");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function handleLogin(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();

        try {
            setLoading(true);
            setError("");

            const response = await fetch(`${API_URL}/api/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                throw new Error("Email ou senha inválidos.");
            }

            const data: LoginResponse = await response.json();

            saveToken(data.token);
            navigate("/painel-secreto");
        } catch (error) {
            console.error(error);
            setError("Não foi possível fazer login. Verifique email e senha.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <main className="login-page">
            <section className="login-card">
                <div className="login-brand">
                    <div>
                        <img src="../logo-header.png" alt="logo" width = "150px" />
                    </div>
                </div>

                <h1>Acesso interno</h1>
                <p>Entre para gerenciar posts, categorias e comentários.</p>

                {error && <div className="admin-error">{error}</div>}

                <form onSubmit={handleLogin}>
                    <label>Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />

                    <label>Senha</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />

                    <button disabled={loading}>
                        {loading ? "Entrando..." : "Entrar no painel"}
                    </button>
                </form>
            </section>
        </main>
    );
}