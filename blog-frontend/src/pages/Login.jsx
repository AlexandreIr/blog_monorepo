import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/api";

export default function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("admin@empresa.com");
  const [password, setPassword] = useState("admin123");

  async function login(e) {
    e.preventDefault();

    try {
      const res = await api.post("/api/auth/login", { email, password });

      localStorage.setItem("token", res.data.token);

      alert("Login realizado com sucesso.");
      navigate("/painel-secreto/posts");
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("Erro ao fazer login. Verifique email, senha e hash BCrypt no banco.");
    }
  }

  return (
    <div className="card">
      <h1>Login Administrativo</h1>

      <form onSubmit={login}>
        <input
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button>Entrar</button>
      </form>
    </div>
  );
}